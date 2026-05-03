# Architekturní Analýza - MNSCK Travel Agency

## Přehled Aplikace
MNSCK je Spring Boot aplikace pro správu cestovní agentury. Aplikace spravuje zájezdy, zákazníky, průvodce, transport, destinace, rezervace a platby.

## Vrstvená Architektura

```
┌─────────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER (UI)                      │
│  Thymeleaf Templates (HTML) + HTMX, DaisyUI                     │
│  - index.html, tours-list.html, bookings-list.html, etc.        │
└─────────────────────────────────────────────────────────────────┘
                              ↑ ↓
┌─────────────────────────────────────────────────────────────────┐
│                      CONTROL LAYER                              │
│  Controllers (@Controller)                                      │
│  - IndexController, TourController, BookingController,          │
│    GuideController, CustomerController                          │
└─────────────────────────────────────────────────────────────────┘
                              ↑ ↓
┌─────────────────────────────────────────────────────────────────┐
│                       SERVICE LAYER                             │
│  Business Logic (@Service)                                      │
│  - PricingStrategyService                                       │
│  - TourStatisticsService                                        │
│  - Event Publishing & Handling                                  │
└─────────────────────────────────────────────────────────────────┘
                              ↑ ↓
┌─────────────────────────────────────────────────────────────────┐
│                     REPOSITORY LAYER                            │
│  Data Access (Spring Data JPA)                                  │
│  - BookingRepository, TourRepository, CustomerRepository,       │
│    GuideRepository, DestinationRepository, TransportRepository, │
│    PaymentRepository, LanguageRepository                        │
└─────────────────────────────────────────────────────────────────┘
                              ↑ ↓
┌─────────────────────────────────────────────────────────────────┐
│                       DATABASE LAYER                            │
│  H2 In-Memory Database (s persistent souborem)                  │
│  - Entitní objekty mapované na tabulky                          │
└─────────────────────────────────────────────────────────────────┘
```

## Komponenty a Jejich Role

### 1. ENTITY (Doménové objekty)
- **Tour** - Zájezd s cestovním cílem, průvodcem, transportem
- **Booking** - Rezervace s State Pattern pro správu stavů
- **Customer** - Zákazník (VIP informace pro strategii)
- **Payment** - Platba k rezervaci
- **Guide** - Průvodce (jazyky, kvalifikace)
- **Destination** - Destinace zájezdu
- **Transport** - Dopravy (autobus, letadlo, atd.)
- **Language** - Jazyky průvodců
- **AppUser** - Uživatelia aplikace (Staff)

### 2. REPOSITORY (Přístup k datům)
Poskytují metody pro CRUD operace a custom queries:
- `findAll()`, `save()`, `delete()`
- Custom queries (např. `sumOccupiedCapacity()` v BookingRepository)

### 3. CONTROLLER (Řídící vrstva)
- **TourController** - Správa zájezdů
- **BookingController** - Správa rezervací a plateb
- **GuideController** - Správa průvodců (jazyky)
- **CustomerController** - Správa zákazníků
- **IndexController** - Úvodní stránka

### 4. SERVICE (Obchodní logika)
- **PricingStrategyService** - Výpočet ceny dle strategie
- **TourStatisticsService** - Statistiky zájezdů
- **Event Publishing** - Publikování events pro notifikace

## Design Patterns

### 1. **STATE PATTERN** (Booking Status Management)
Správa stavů rezervace s flexibilními přechody:

```
BookingState (interface)
    ├── WaitingForPaymentState
    ├── PartiallyPaidState
    ├── PaidState
    └── CancelledState
    
Booking entity:
    - current_status: BookingState (Transient)
    - status_string: String (Persisted)
    - pay(amount) → current_status.pay(this, amount)
    - cancel() → current_status.cancel(this)
```

**Benefits:**
- Čistý kód - logika pro každý stav je oddělena
- Flexibilní přechody mezi stavy
- Snadné přidání nových stavů

### 2. **STRATEGY PATTERN** (Pricing)
Flexibilní výpočet ceny podle podmínek:

```
DiscountStrategy (interface)
    ├── NoDiscountStrategy
    ├── VipCustomerStrategy (50% sleva pro VIP)
    └── LastMinuteStrategy (sleva 14 dní před)

DiscountStrategyFactory:
    - createStrategy(customer, tour) → DiscountStrategy
    
PriceCalculator:
    - setDiscountStrategy()
    - calculateFinal() → final price
```

**Kritéria výběru strategii:**
- Počet dní do zájezdu (≤14 → LastMinute)
- VIP status zákazníka → VipCustomer
- Jinak → NoDiscount

### 3. **FACTORY PATTERN** (Strategy Creation)
`DiscountStrategyFactory` vytváří správnou strategii na základě:
- Customer.is_vip
- Tour.start_date (proximity)

### 4. **EVENT/LISTENER PATTERN** (Notifications)
```
BookingStatusChangedEvent:
    - Publikován při změně stavu
    
EmailNotificationListener:
    - Poslouchá BookingStatusChangedEvent
    - Odesílá notifikace
```

### 5. **REPOSITORY PATTERN** (Data Access)
Spring Data JPA repositories skrývají komplexnost přístupu k datům.

### 6. **DEPENDENCY INJECTION** (Spring IoC)
- `@Service`, `@Component`, `@Controller` (stereotypes)
- `@RequiredArgsConstructor` (Lombok - automatické konstruktory)
- `@Autowired` (Spring dependency injection)

## Datový Model

### Klíčové Vztahy
```
Tour (1) ─── (N) Booking
Tour (1) ─── (1) Guide
Tour (1) ─── (1) Destination  
Tour (1) ─── (1) Transport

Guide (1) ─── (N) Language

Customer (1) ─── (N) Booking

Booking (1) ─── (N) Payment
```

## Technologický Stack

| Vrstva | Technologie |
|--------|------------|
| Frontend | Thymeleaf, HTMX, HTML/CSS, DaisyUI |
| Backend | Spring Boot 4.0.3, Spring MVC |
| ORM | Spring Data JPA, Hibernate 7.2.4 |
| Database | H2 (in-memory + persistent file) |
| Build | Gradle |
| Java | JDK 25 |
| Other | Lombok (annotations), Tomcat (embedded) |

## Klíčové Features

### 1. Správa Zájezdů
- CRUD operace na tours
- Propojení s guides, destinations, transport
- Obsazenost podle bookings

### 2. Rezervační Systém
- PaymentState Management
- Více plateb na jednu rezervaci
- Kalkulace zbývajícího zůstatku

### 3. Cenová Strategie
- Dynamická cena dle vip statusu
- Last-minute slevy
- Flexibilní rozšíření nových strategií

### 4. Správa Průvodců
- Primární a sekundární jazyk
- Validace duplicitních jazyků
- Event notifikace

### 5. Frontend (HTMX + Thymeleaf)
- Dynamické aktualizace bez page reload
- Modální dialogy
- Tabulkové přehledy (Tours, Bookings, Customers, Guides)

## Datové Toky - Klíčové UI Operace

### 1. Přidání Nového Zájezdu
```
UI Form (tours-list.html)
    ↓
TourController.save(@ModelAttribute Tour)
    ↓
TourRepository.save(tour)
    ↓
Database (INSERT INTO TOURS)
    ↓
Controller vrací updated fragment (HTMX) - bez page reload
```

### 2. Vytvoření Rezervace
```
UI Form (bookings-list.html)
    ↓
BookingController.save(@ModelAttribute Booking)
    ↓
PricingStrategyService.calculatePrice()
    ↓
DiscountStrategyFactory.createStrategy()
    ↓
PriceCalculator.calculateFinal() - aplikuje strategii
    ↓
Booking.setTotal_price()
    ↓
BookingRepository.save(booking) - s initial state WAITING_FOR_PAYMENT
    ↓
HTMX refresh tabulky
```

### 3. Placení Rezervace
```
UI Payment Form
    ↓
BookingController.pay(bookingId, amount)
    ↓
Booking.pay(amount) - vytvoří Payment objekt
    ↓
booking.current_status.pay(booking, amount) - STATE PATTERN
    ├─ WaitingForPaymentState → PartiallyPaidState
    ├─ PartiallyPaidState → (incomplete) nebo (PaidState)
    └─ PaidState → zůstane PaidState
    ↓
BookingRepository.save(booking) - s aktualizovaným status_string
    ↓
BookingStatusChangedEvent publish
    ↓
EmailNotificationListener receives event
    ↓
Email notification odeslan
```

### 4. Zrušení Rezervace
```
Booking.cancel()
    ↓
current_status.cancel(booking) - STATE PATTERN
    ↓
SetStatus(CancelledState) + refund logika
    ↓
Database update + event publish
```

## Doporučené Vylepšení Archektury

### 1. **DTO (Data Transfer Objects) vrstva**
- Oddělení entit od API
- Validace vstupů
- Kontrola přístupu

### 2. **Exception Handling**
- Centralizovaný ExceptionHandler
- Custom exceptions
- Proper HTTP status codes

### 3. **Logging**
- SLF4J + Logback
- Audit trail pro kritické operace

### 4. **Security**
- Spring Security
- Autentizace a autorizace
- CSRF protection

### 5. **Caching**
- @Cacheable na repositories
- Redis nebo in-memory cache

### 6. **Validation**
- Jakarta Bean Validation (@Valid, @NotNull, atd.)
- Custom validators

### 7. **Unit Testing**
- JUnit 5
- Mockito pro mocking
- TestContainers pro integration testing

### 8. **API Documentation**
- OpenAPI / Swagger
- /api endpoints místo template serving

## Deployment Architecture

```
┌──────────────┐
│   Client     │
│  (Browser)   │
└──────┬───────┘
       │ HTTP/HTTPS
       ↓
┌────────────────────────────┐
│   Tomcat (embedded)        │
│   Spring Boot Application  │
│   - Controllers            │
│   - Services               │
│   - Repositories           │
│   - Entity Managers        │
└────────────┬───────────────┘
             │ JDBC
             ↓
        ┌─────────────┐
        │  H2 Database│
        │ (mnsck.db)  │
        └─────────────┘
```

## Shrnutí

Aplikace následuje **vrstvené architektuře** s jasným oddělením:
- **Presentation** (Thymeleaf + HTMX)
- **Business Logic** (Services, Strategies)
- **Data Access** (Repositories, JPA)
- **Domain** (Entities)

Použitá design patterns:
- 🔄 **State Pattern** - Booking State Management
- 🎯 **Strategy Pattern** - Dynamic Pricing
- 🏭 **Factory Pattern** - Strategy Creation
- 📢 **Observer Pattern** - Event Publishing
- 📦 **Repository Pattern** - Data Access

Aplikace je **modulární, testabilní a rozšiřitelná**.

