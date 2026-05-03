# 📊 UML Component Diagrams - Průvodce Vizualizací

Vytvořil jsem pro vaši aplikaci **5 detailních PlantUML diagramů**, které dokumentují architekturu.

## 📁 Vytvořené Soubory

| Soubor | Obsah |
|--------|-------|
| `ARCHITECTURE.md` | Detailní textový přehled architektury |
| `UML_COMPONENT_DIAGRAM.md` | Kompletní dokumentace všech komponent |
| `component_diagram.puml` | Hlavní Component Diagram - všechny vrstvy |
| `StatePattern_Diagram.puml` | Detail: State Pattern (Booking Status) |
| `StrategyPattern_Diagram.puml` | Detail: Strategy Pattern (Pricing) |
| `DataFlow_CreateBooking.puml` | Sekvencediagram: Vytvoření rezervace |
| `DataFlow_Payment.puml` | Sekvencediagram: Platba s State Pattern |

## 🎨 Jak Zobrazit PlantUML Diagramy

### Option 1: Online PlantUML Editor (Nejjednodušší)
1. Jděte na https://www.plantuml.com/plantuml/uml/
2. Zkopírujte obsah `.puml` souboru
3. Vložte do editor
4. Diagram se vykreslí automaticky

### Option 2: VS Code Extension
1. Nainstalujte extension: **PlantUML** (ID: jebbs.plantuml)
2. Otevřete `.puml` soubor v VS Code
3. Stiskněte `Alt+D` (nebo `Cmd+D` na Mac) → zobrazí preview
4. Kliknutím na ikonu exportu si můžete stáhnout jako PNG/SVG

### Option 3: IntelliJ IDEA
1. V JetBrains IDE klikněte na `.puml` soubor
2. IDE nabízí built-in visuální preview
3. Klikněte na "Diagram" ikon

### Option 4: Příkazová Řádka
```powershell
# Nainstalovat PlantUML CLI
choco install plantuml

# Vygenerovat PNG z PUML souboru
plantuml component_diagram.puml -o ./diagrams/
```

### Option 5: Docker
```bash
docker run --rm -v C:\Users\elexa\IdeaProjects\mnsck:/workspace \
  plantuml/plantuml:latest \
  component_diagram.puml
```

---

## 📖 Čtení Diagramů

### component_diagram.puml
**Barvy:**
- 🔵 **Modrá** (UI) - Thymeleaf templates
- 🟠 **Oranžová** (Controller) - Spring Controllers
- 🟣 **Fialová** (Service) - Business logic
- 🟢 **Zelená** (Repository) - Data access
- 🌸 **Růžová** (Entity) - Domain objects
- 🟡 **Žlutá** (Pattern) - Design pattern implementations
- ⚪ **Šedá** (Infrastructure) - Spring, Tomcat, Database

**Legenda šipek:**
- `─→` Silná závislost
- `─.→` Slabá závislost (import, konfigurační reference)

### State Pattern Diagram
Zobrazuje:
- **BookingState** interface a 4 konkrétní stavy
- Přechody mezi stavy (transitions)
- `@PostLoad` / `@PreUpdate` lifecycle methods
- Payment processing logika

### Strategy Pattern Diagram
Zobrazuje:
- **DiscountStrategy** interface a 3 implementace
- **DiscountStrategyFactory** rozhodovací logika
- **PriceCalculator** context třída
- Jak se strategie vybírá (kritéria)

### DataFlow Diagramy
Sekvenční diagramy ukazující:
- Tok dat přes aplikaci
- Interakce mezi komponentami
- Volání metod a výměnu dat
- State transitions a event publishing

---

## 🔍 Klíčové Komponenty

### Presentation Layer
```
index.html, tours-list.html, bookings-list.html, ...
         ↓ (HTMX dynamic updates)
      HTML fragments (bez page reload)
```

### Control Layer
```
TourController
  ├─ GET /tours → list
  ├─ POST /tours/save → create
  └─ Helper: buildOccupiedByTourId()

BookingController
  ├─ GET /bookings → list
  ├─ POST /bookings/save → create
  ├─ POST /bookings/{id}/pay → payment
  └─ POST /bookings/{id}/cancel → cancel

GuideController, CustomerController, IndexController
```

### Service Layer
```
PricingStrategyService
  └─ calculatePrice() → deleguje na factory & strategy

TourStatisticsService
  └─ Statistiky a obsazenost

Event Publishing
  └─ BookingStatusChangedEvent listeners
```

### Patterns
```
STRATEGY PATTERN:
  DiscountStrategy (interface)
    ├─ NoDiscountStrategy
    ├─ VipCustomerStrategy
    └─ LastMinuteStrategy
  DiscountStrategyFactory (creates right strategy)
  PriceCalculator (context)

STATE PATTERN:
  BookingState (interface)
    ├─ WaitingForPaymentState
    ├─ PartiallyPaidState
    ├─ PaidState
    └─ CancelledState
  Booking uses current_status (transient)
```

### Repository Layer
```
TourRepository, BookingRepository, CustomerRepository, ...
     ↓ (Spring Data JPA)
Custom queries: sumOccupiedCapacity(), findAll(), ...
```

### Entity Layer
```
Tour ── Guide ── Language
 │
 ├─ Destination
 ├─ Transport
 │
 └─ Booking ──── Payment
                  │
           Customer (foreign key)
```

---

## 🎯 Běžné Use Cases & Data Flow

### 1️⃣ Vytvoření Zájezdu
```
User Form → TourController.save()
  → TourRepository.save()
    → Database: INSERT INTO TOURS
      → HTMX: refresh tours-list
        → Show new tour in table
```

### 2️⃣ Vytvoření Rezervace s Cenou
```
User Form → BookingController.save()
  → PricingStrategyService.calculatePrice()
    → DiscountStrategyFactory.createStrategy()
      Decision: daysUntilTour? isVip?
        → LastMinuteStrategy / VipCustomerStrategy / NoDiscountStrategy
          → PriceCalculator.calculateFinal()
            Kalkulace: basePrice * qty + reducedPrice * reduced_qty
              → Booking.setTotal_price()
                → BookingRepository.save()
                  → Database: INSERT BOOKING + initial Payment (0)
                    → HTMX: refresh bookings table
                      → Show new booking with calculated price
```

### 3️⃣ Zpracování Platby
```
User clicks Pay → BookingController.pay(amount)
  → Booking.pay(amount)
    → Create Payment object
      → current_status.pay(this, amount)  // STATE PATTERN
        WaitingForPaymentState:
          if (amountPaid == totalPrice)
            → setStatus(PaidState)
          else
            → setStatus(PartiallyPaidState)
      → @PreUpdate syncStatus()
        → status_string = current_status.getStateName()
          → BookingRepository.save()
            → Database: UPDATE BOOKING + INSERT PAYMENT
              → BookingStatusChangedEvent.publish()
                → EmailNotificationListener receives event
                  → Send email notification
                    → HTMX updates payment info
                      → Show new status + amount remaining
```

### 4️⃣ Zrušení Rezervace
```
User clicks Cancel → BookingController.cancel()
  → Booking.cancel()
    → current_status.cancel(this)  // STATE PATTERN
      → ANY state → CancelledState
        → @PreUpdate syncStatus()
          → BookingRepository.save()
            → Database: UPDATE BOOKING
              → BookingStatusChangedEvent.publish()
                → EmailNotificationListener
                  → HTMX updates status
```

---

## 📊 Architekturní Vlastnosti

| Vlastnost | Hodnota |
|-----------|---------|
| **Vrstvená Architektura** | ✅ Ano (Presentation → Control → Service → Repository → Entity → DB) |
| **Separation of Concerns** | ✅ Ano (každá vrstva má svou odpovědnost) |
| **Design Patterns** | ✅ 5+ (Strategy, State, Factory, Observer, Repository) |
| **Dependency Injection** | ✅ Spring IoC |
| **Circular Dependencies** | ✅ Ne (čistá struktura) |
| **Code Reusability** | ✅ Services, Repositories, Patterns |
| **Testability** | ✅ Ano (mockable components, interfaces) |
| **Extensibility** | ✅ Ano (přidat novou strategii, stav, feature) |

---

## 🚀 Příklady Rozšíření

### Přidat Novou Pricing Strategii
```java
// 1. Vytvořit třídu
public class CorporateDiscountStrategy implements DiscountStrategy {
    @Override
    public double calculateFinal(...) {
        // Custom calculation: 40% off for corporate
    }
}

// 2. Upravit Factory
public class DiscountStrategyFactory {
    public DiscountStrategy createStrategy(Customer customer, Tour tour) {
        // ... existing logic ...
        if (customer.getIs_corporate()) 
            return new CorporateDiscountStrategy();
    }
}

// Hotovo! Bez změn v ostatního kódu
```

### Přidat Nový Booking State
```java
// 1. Vytvořit třídu
public class RefundedState implements BookingState {
    @Override
    public void pay(Booking b, double amount) { /* no-op */ }
    @Override
    public void cancel(Booking b) { /* no-op */ }
}

// 2. Upravit Booking.init()
@PostLoad
private void init() {
    // ... existing switch ...
    case "REFUNDED" -> this.current_status = new RefundedState();
}

// Hotovo!
```

### Přidat Nový Event Listener
```java
@Component
public class SMSNotificationListener 
    implements ApplicationListener<BookingStatusChangedEvent> {
    
    @Override
    public void onApplicationEvent(BookingStatusChangedEvent event) {
        // Send SMS instead of email
    }
}
```

---

## 🔗 Technology Stack Summary

```
Frontend:
  ├─ Thymeleaf (template engine)
  ├─ HTMX (dynamic updates)
  ├─ DaisyUI (component library)
  └─ HTML/CSS

Backend:
  ├─ Spring Boot 4.0.3 (framework)
  ├─ Spring Data JPA (ORM abstraction)
  ├─ Hibernate 7.2.4 (ORM provider)
  ├─ Spring MVC (web controller)
  └─ Spring Framework (IoC container)

Database:
  └─ H2 Embedded (in-memory + file-based)

Build:
  └─ Gradle

Java:
  └─ JDK 25

Other:
  └─ Lombok (code generation)
```

---

## 📚 Reference Files

1. **ARCHITECTURE.md** - Vrstvená architektura a technologie
2. **UML_COMPONENT_DIAGRAM.md** - Detailní dokumentace komponent
3. **component_diagram.puml** - PlantUML main diagram (zkopírujte do editoru)
4. **StatePattern_Diagram.puml** - State Pattern detail
5. **StrategyPattern_Diagram.puml** - Strategy Pattern detail
6. **DataFlow_CreateBooking.puml** - Use case flow
7. **DataFlow_Payment.puml** - Payment processing flow

---

## 💡 Tipy pro Porozumění

1. **Začněte s ARCHITECTURE.md** - přehled vrstvené architektury
2. **Podívejte se na component_diagram.puml** - vizuální výhled
3. **Čtěte UML_COMPONENT_DIAGRAM.md** - detailní vysvětlení
4. **Prohlédněte StatePattern/StrategyPattern diagramy** - pattern detail
5. **Sledujte DataFlow diagramy** - jak se data pohybují

---

## ❓ FAQ

### Q: Která vrstva by měla obsahovat X logiku?
**A:** Vrstvy:
- **Presentation** - Jen UI rendering (HTML)
- **Control** - Request handling, parameter binding
- **Service** - Business logic (pricing, statistics)
- **Repository** - Database queries
- **Entity** - Domain objects + lifecycle

### Q: Jak Přidat Novou Entitu?
**A:**
1. Vytvořit `src/main/.../entity/MyEntity.java`
2. Vytvořit `src/main/.../repository/MyEntityRepository.java`
3. Vytvořit `src/main/.../controller/MyEntityController.java`
4. Vytvořit template `src/main/resources/templates/my-entity-list.html`
5. Přidat route do `IndexController` (pokud chcete navigaci)

### Q: Jak se Přidává Nový Feature?
**A:** Vzor je vždy stejný:
```
UI (template) 
  → Controller 
    → Service (if complex logic)
      → Repository
        → Database
          ← zpět na UI
```

### Q: Co znamená @Transient u current_status?
**A:** JPA nebude toto pole persistovat do DB. Místo toho se čte z `status_string` pomocí `@PostLoad` (deserializace).

### Q: Proč State Pattern místo enum?
**A:** Enum by měl všechny stavy obsahovat jednu velkou switch statement. State Pattern = OOP přístup, snazší přidávání nových stavů.

---

## 📞 Kontakt & Autorita

**Analytik:** Tomáš Elexa  
**Projekt:** MNSCK - Travel Agency Management  
**Datum:** Květen 2026  
**Verze:** 1.0

---

**Užijte si diagramů a pokud máte otázky, prostě si je vizualizujte v PlantUML editoru online! 🎨**

