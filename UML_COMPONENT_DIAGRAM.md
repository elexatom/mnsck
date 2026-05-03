# UML Component Diagram - Architektura MNSCK

## 📊 Vygenerované Diagramy

1. **component_diagram.puml** - Hlavní component diagram všech vrstev
2. **StatePattern_Diagram.puml** - Detail State Pattern (Booking Status)
3. **StrategyPattern_Diagram.puml** - Detail Strategy Pattern (Pricing)
4. **DataFlow_CreateBooking.puml** - Sekvence vytvoření rezervace
5. **DataFlow_Payment.puml** - Sekvence zpracování platby

## 📐 Architekturní Vrstvy

```
┌─────────────────────────────────────────────────────────────┐
│         PRESENTATION LAYER (View - Thymeleaf/HTML)          │
│  - index.html, tours-list.html, bookings-list.html, ...    │
│  - HTMX pro dynamic updates bez page reload                 │
│  - DaisyUI pro responsive design                            │
└─────────────────────────────────────────────────────────────┘
                          ↑ HTTP ↓
┌─────────────────────────────────────────────────────────────┐
│       CONTROL LAYER (Controllers - Request Handlers)        │
│  - IndexController                                          │
│  - TourController → GET/POST tours, obsazenost, detail      │
│  - BookingController → CREATE booking, PAY, CANCEL          │
│  - GuideController → CRUD guides, languages                 │
│  - CustomerController → CRUD customers, change VIP         │
└─────────────────────────────────────────────────────────────┘
                      ↑ Dependencies ↓
┌─────────────────────────────────────────────────────────────┐
│     SERVICE LAYER (Business Logic & Orchestration)          │
│  - PricingStrategyService → calculate price with discount   │
│  - TourStatisticsService → tour statistics & occupancy      │
│  - BookingStatusChangedEvent → Event publishing             │
│  - EmailNotificationListener → Event listening              │
└─────────────────────────────────────────────────────────────┘
                      ↑ Dependencies ↓
┌──────────────────────────────────────────────────────────────┐
│    PATTERN IMPLEMENTATIONS (Kreativní rešení)               │
│  ┌────────────────────────────────────────────────────────┐ │
│  │ STRATEGY PATTERN (Pricing)                             │ │
│  │  - DiscountStrategy (interface)                        │ │
│  │  - NoDiscountStrategy, VipCustomerStrategy,            │ │
│  │    LastMinuteStrategy (implementations)                │ │
│  │  - DiscountStrategyFactory (decision logic)             │ │
│  │  - PriceCalculator (context)                           │ │
│  └────────────────────────────────────────────────────────┘ │
│  ┌────────────────────────────────────────────────────────┐ │
│  │ STATE PATTERN (Booking Status)                         │ │
│  │  - BookingState (interface)                            │ │
│  │  - WaitingForPaymentState, PartiallyPaidState,        │ │
│  │    PaidState, CancelledState (implementations)         │ │
│  │  - Booking.pay() & cancel() delegate na state methods  │ │
│  └────────────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────────┘
                      ↑ Dependencies ↓
┌─────────────────────────────────────────────────────────────┐
│   REPOSITORY LAYER (Data Access - Spring Data JPA)         │
│  - TourRepository                                           │
│  - BookingRepository                                        │
│  - CustomerRepository                                       │
│  - GuideRepository                                          │
│  - PaymentRepository                                        │
│  - DestinationRepository                                    │
│  - TransportRepository                                      │
│  - LanguageRepository                                       │
│  Custom queries: sumOccupiedCapacity(), findAll(), etc.    │
└─────────────────────────────────────────────────────────────┘
                      ↑ JDBC ↓
┌─────────────────────────────────────────────────────────────┐
│     ENTITY LAYER (Domain Model - JPA Entities)             │
│  - Tour, Booking, Customer, Payment, Guide, Transport,     │
│    Destination, Language, AppUser (Staff)                  │
│  - @Entity, @Table, @ManyToOne, @OneToMany, @Transient     │
│  - State management via @PostLoad/@PrePersist/@PreUpdate   │
└─────────────────────────────────────────────────────────────┘
                      ↑ SQL ↓
┌─────────────────────────────────────────────────────────────┐
│        DATABASE LAYER (H2 Embedded)                        │
│  - mnsck.mv.db (data file - persistent)                    │
│  - mnsck.trace.db (transaction log)                        │
│  - Tables: TOURS, BOOKING, PAYMENT, CUSTOMER, GUIDE, etc.  │
└─────────────────────────────────────────────────────────────┘
```

## 🔧 Infrastruktura

### Spring Framework Components
- **Spring IoC Container** - Dependency Injection
  - `@Service`, `@Component`, `@Controller`, `@Repository`
  - `@RequiredArgsConstructor` (Lombok constructor injection)
  - `@Autowired` (field injection)

- **Spring MVC** - Web Framework
  - `@RequestMapping`, `@GetMapping`, `@PostMapping`
  - `@ModelAttribute` (form binding)
  - Model & View resolution

- **Spring Data JPA** - ORM Abstraction
  - `Repository<T, ID>` interface
  - Query methods: `findAll()`, `save()`, `findById()`
  - Custom queries with `@Query`

### Server & Template Engine
- **Tomcat Embedded** - Servlet Container (port 8080)
- **Thymeleaf** - Template Engine
  - `th:each`, `th:if`, `th:action`, `th:field`
  - Spring Expression Language (SpEL)
  - Fragment inclusion (`th:insert`)

### Frontend Technologies
- **HTMX** - Dynamic Updates
  - `hx-get`, `hx-post`, `hx-target`, `hx-swap`
  - Partial HTML replacement without page reload
  
- **DaisyUI** - TailwindCSS Component Library
  - Responsive design
  - Pre-styled components (buttons, modals, tables)

- **HTML/CSS** - Base markup and styling

### Database
- **H2 Database** (Embedded)
  - In-memory with persistent file backup
  - JDBC driver: `org.h2.drivers.H2Driver`
  - Console available: `http://localhost:8080/h2-console`

### Build Tool & Version
- **Gradle** - Build automation
- **Java 25** - Language version
- **Hibernate 7.2.4** - ORM Provider

---

## 📦 Komponenty Detailně

### 1️⃣ CONTROLLERS

#### TourController
```java
@Controller
@RequestMapping("/tours")
```
**Zodpovídá za:**
- GET /tours → zobrazit seznam zájezdů
- POST /tours/save → uložit nový zájezd
- Výpočet obsazenosti per tour (helper metoda)
- Načítání dropdown zdrojů (destinace, průvodci, doprava)

**Závislosti:**
- TourRepository
- BookingRepository (pro obsazenost)
- DestinationRepository
- GuideRepository
- TransportRepository

---

#### BookingController
```java
@Controller
@RequestMapping("/bookings")
```
**Zodpovídá za:**
- GET /bookings → zobrazit seznam rezervací
- POST /bookings/save → vytvořit novou rezervaci
- POST /bookings/{id}/pay → zaplatit
- POST /bookings/{id}/cancel → zrušit
- Kalkulace ceny pomocí PricingStrategyService

**Závislosti:**
- BookingRepository
- PricingStrategyService (cena)
- BookingStatusChangedEvent (notifikace)

---

#### GuideController
```java
@Controller
@RequestMapping("/guides")
```
**Zodpovídá za:**
- CRUD operace na průvodcích
- Správa jazyků (primární/sekundární)
- Validace - zabránit duplicitním jazykům (toast warning)

**Závislosti:**
- GuideRepository
- LanguageRepository

---

#### CustomerController, IndexController
- Podobné struktury jako výše
- Správa zákazníků, zobrazení úvodní stránky

---

### 2️⃣ SERVICES

#### PricingStrategyService
```java
@Service
public class PricingStrategyService {
    @Autowired
    private DiscountStrategyFactory strategyFactory;
    
    public double calculatePrice(Customer customer, Tour tour, 
                                int fullPrice, int reducedPrice) {
        DiscountStrategy strategy = 
            strategyFactory.createStrategy(customer, tour);
        PriceCalculator calculator = new PriceCalculator(strategy);
        return calculator.calculateFinal(
            tour.getPrice_person(),
            tour.getPrice_person_reduced(),
            fullPrice, reducedPrice);
    }
}
```

**Zodpovídá za:**
- Orchestrace Strategy Pattern
- Výpočet finální ceny

---

#### TourStatisticsService
```java
@Service
public class TourStatisticsService {
    // Calculate average occupancy
    // Get occupied capacity for tour
    // etc.
}
```

---

#### Event Publishing & Listening
```java
// Event
public class BookingStatusChangedEvent 
    extends ApplicationEvent {
    private Booking booking;
    private String newStatus;
}

// Listener
@Component
public class EmailNotificationListener 
    implements ApplicationListener<BookingStatusChangedEvent> {
    
    @Override
    public void onApplicationEvent(BookingStatusChangedEvent event) {
        // Send email notification
        // Triggered when booking.pay() or booking.cancel()
    }
}
```

---

### 3️⃣ STRATEGY PATTERN - Pricing

```
Decision Tree:

                    createStrategy(customer, tour)
                            |
                    ┌───────┴───────┐
                    |               |
        daysUntilTour ≤ 14?  customer.is_vip?
                 |                 |
                YES               YES
                 |                 |
        LastMinuteStrategy  VipCustomerStrategy
        (30% off)           (50% off basePrice)
                 
                              NO
                              |
                        NoDiscountStrategy
                        (full price)
```

**Konkrétní implementace:**
- **NoDiscountStrategy**: `final = (basePrice * baseQty) + (reducedPrice * reducedQty)`
- **VipCustomerStrategy**: `final = (basePrice*0.5 * baseQty) + (reducedPrice * reducedQty)`
- **LastMinuteStrategy**: `final = (basePrice*0.7 * baseQty) + (reducedPrice*0.7 * reducedQty)`

---

### 4️⃣ STATE PATTERN - Booking Status

```
Transitions:

    ┌─────────────────────────────────────────┐
    │    WaitingForPaymentState (initial)     │
    │  (First payment received)               │
    └──────────────┬──────────────────────────┘
                   │pay(amount < total)
                   ↓
    ┌─────────────────────────────┐
    │ PartiallyPaidState          │
    │ (Some payments, not all)    │
    └──────┬──────────────┬───────┘
           │pay(amount)   │
           │(completes)   │
           ↓              ├─→ [can cancel with refund]
    ┌──────────────────┐
    │ PaidState        │
    │ (Fully paid)     │
    └──────┬───────────┘
           │(cannot pay more / already done)
           │(can cancel - depends on policy)
           ↓
    ┌──────────────────┐
    │ CancelledState   │
    │ (Terminal)       │
    └──────────────────┘

    ANY STATE + cancel() → CancelledState
```

**Soubor stavů:**
- `WaitingForPaymentState` - žádná platba
- `PartiallyPaidState` - částečná platba
- `PaidState` - plná platba
- `CancelledState` - zrušeno

---

### 5️⃣ REPOSITORY LAYER

```java
@Repository
public interface TourRepository 
    extends JpaRepository<Tour, Long> {
    
    // Inherited from JpaRepository:
    // - findAll() : List<Tour>
    // - findById(Long) : Optional<Tour>
    // - save(Tour) : Tour
    // - delete(Tour) : void
    // - etc.
}

@Repository
public interface BookingRepository 
    extends JpaRepository<Booking, Long> {
    
    // Custom query
    @Query("SELECT COALESCE(SUM(b.persons_qty + b.persons_reduced_qty), 0) " +
           "FROM Booking b WHERE b.tour = ?1 AND b.status_string != ?2")
    Long sumOccupiedCapacity(Tour tour, String excludeStatus);
}
```

---

### 6️⃣ ENTITY RELATIONSHIPS

```
Tour (1) ─── (N) Booking
  ∟ Has many bookings

Tour (1) ─── (1) Guide
  ∟ Needs a tour guide

Tour (1) ─── (1) Destination
  ∟ Goes to a destination

Tour (1) ─── (1) Transport
  ∟ Uses specific transport

Guide (1) ─── (N) Language
  ∟ Primary and secondary language

Customer (1) ─── (N) Booking
  ∟ Can book multiple tours

Booking (1) ─── (N) Payment
  ∟ Can have multiple payments
  ∟ CascadeType.ALL - delete booking → delete payments

AppUser (Staff)
  ∟ Manages customers, guides, tours
```

---

## 🔄 Typické Use Cases & Data Flow

### USE CASE: Vytvoření Rezervace s Cenovou Strategií

**Sekvence:**

1. **UI** → User vyplní formulář (customer, tour, qty, reduced_qty)
2. **Controller** → BookingController.save(@ModelAttribute Booking)
3. **Service** → PricingStrategyService.calculatePrice()
4. **Factory** → DiscountStrategyFactory.createStrategy()
   - Rozhodne: LastMinute? VIP? NoDiscount?
5. **Strategy** → Concrete strategy impl. calculateFinal()
6. **Calculator** → PriceCalculator.calculateFinal()
7. **Entity** → Booking.setTotal_price(finalPrice)
   - Booking.setStatus(new WaitingForPaymentState())
8. **Repository** → BookingRepository.save(booking)
9. **Database** → INSERT INTO BOOKING + INSERT INTO PAYMENT (initial = 0)
10. **View** → HTMX refresh booking list (no page reload)

---

### USE CASE: Zpracování Platby

**Sekvence:**

1. **UI** → User vybere booking, zadá amount, klikne "Pay"
2. **Controller** → BookingController.pay(bookingId, amount)
3. **Entity** → Booking.pay(amount)
   - Vytvoří Payment object
   - Přidá do payments list
4. **State** → current_status.pay(this, amount)
   - WaitingForPaymentState → check if fully paid
     - Pokud ano → setStatus(PaidState)
     - Pokud ne → setStatus(PartiallyPaidState)
5. **@PreUpdate** → syncStatus() serializes state → status_string
6. **Repository** → BookingRepository.save(booking)
   - INSERT INTO PAYMENT
   - UPDATE BOOKING SET status = '...'
7. **Event** → BookingStatusChangedEvent published
8. **Listener** → EmailNotificationListener sends email
9. **View** → HTMX updates booking row (status, amount paid, remaining)

---

### USE CASE: Zrušení Rezervace

**Sekvence:**

1. **UI** → User klikne "Cancel Booking"
2. **Controller** → BookingController.cancel(bookingId)
3. **Entity** → Booking.cancel()
4. **State** → current_status.cancel(this)
   - Any state → CancelledState (with possible refund logic)
5. **@PreUpdate** → syncStatus()
6. **Repository** → BookingRepository.save(booking)
7. **Event** → BookingStatusChangedEvent published
8. **UI** → Status změněn na "CANCELLED"

---

## 📝 Doporučení pro Další Vývoj

### 1. **Přidat DTO Vrstvu**
```java
// Místo Direct Entity → API
@Data
public class BookingDTO {
    private Long id;
    private CustomerDTO customer;
    private TourDTO tour;
    private Double totalPrice;
    // etc.
}
```

### 2. **Exception Handling**
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleNotFound(EntityNotFoundException e) {
        return ResponseEntity.notFound().build();
    }
}
```

### 3. **Security**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // Add Spring Security for authentication/authorization
}
```

### 4. **Logging & Monitoring**
```java
@Service
public class BookingService {
    private static final Logger logger = LoggerFactory.getLogger(...);
    
    public void createBooking(Booking booking) {
        logger.info("Creating booking for customer: {}", booking.getCustomer().getId());
        // ...
    }
}
```

### 5. **Input Validation**
```java
@Entity
public class Booking {
    @NotNull
    private Customer customer;
    
    @Min(0)
    @Max(1000)
    private Integer persons_qty;
}
```

### 6. **API Endpoints** (/api/v1/...)
Přidat REST API endpoints pro frontend integraci

### 7. **Caching**
```java
@Service
public class TourService {
    @Cacheable(value = "tours")
    public List<Tour> getAllTours() {
        return tourRepository.findAll();
    }
}
```

---

## 🎯 Shrnutí Architektur Vzorů

| Pattern | Použití | Benefit |
|---------|---------|---------|
| **Strategy Pattern** | Pricing (3 strategies) | Flexibilní výpočet, snadné přidání nových strategií |
| **State Pattern** | Booking Status (4 states) | Čistý kód, správa přechodů, kontextová logika |
| **Factory Pattern** | DiscountStrategyFactory | Oddělení logiky pro rozhodování |
| **Observer Pattern** | Event Publishing | Decoupling, notifikace bez těsné vazby |
| **Repository Pattern** | Data Access | Abstrakce DB, snadné testování |
| **Dependency Injection** | Spring IoC | Loose coupling, testability |

---

## 🏛️ Vrstvená Architektura Princip

```
                    Presentation
                        ↑↓
                    Business Logic
                        ↑↓
                    Data Access
                        ↑↓
                    Database
```

**Dependencies:**
- TOP-DOWN: Presentation → Business Logic → Data Access → Database
- BOTTOM-UP: Każá vrstva vrací data vyšší vrstvě
- **NO CIRCULAR DEPENDENCIES** - čistá architektura

---

## 📈 Komplexita Projektu

- **Entities**: 9 (+1 Staff/AppUser)
- **Controllers**: 5
- **Repositories**: 8
- **Services**: 2+ (statistiky, pricing)
- **Design Patterns**: 5+ (Strategy, State, Factory, Observer, Repository)
- **Lines of Code**: ~2000-3000
- **Database Tables**: 9+
- **Templates**: 6+

**Verdikt:** Středně komplexní webová aplikace s dobrou architekturou a design patterns.

---

Generated: May 3, 2026
Author: Tomáš Elexa

