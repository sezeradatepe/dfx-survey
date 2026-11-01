# DFX Survey - Spring Boot Eğitim Dokümanı

## 📋 İçindekiler

1. [Proje Hakkında](#1-proje-hakkında)
2. [Teknoloji Stack](#2-teknoloji-stack)
3. [Proje Mimarisi](#3-proje-mimarisi)
4. [Katmanlı Mimari (Layered Architecture)](#4-katmanlı-mimari)
5. [Entity Yapısı ve İlişkiler](#5-entity-yapısı-ve-ilişkiler)
6. [Repository Katmanı](#6-repository-katmanı)
7. [Service Katmanı](#7-service-katmanı)
8. [Controller Katmanı](#8-controller-katmanı)
9. [DTO (Data Transfer Object) Pattern](#9-dto-pattern)
10. [Güvenlik ve JWT](#10-güvenlik-ve-jwt)
11. [Mail Servisi](#11-mail-servisi)
12. [Validator Pattern](#12-validator-pattern)
13. [MapStruct ile Object Mapping](#13-mapstruct)
14. [API Endpoints](#14-api-endpoints)
15. [Veritabanı Şeması](#15-veritabanı-şeması)

---

## 1. Proje Hakkında

**DFX Survey**, kullanıcıların anket oluşturmasına, dağıtmasına ve sonuçlarını analiz etmesine olanak sağlayan bir Spring Boot uygulamasıdır.

### Temel Özellikler:
- ✅ Admin paneli ile anket oluşturma
- ✅ JWT tabanlı kimlik doğrulama
- ✅ E-posta ile anket daveti gönderme
- ✅ Likert ölçekli ve serbest metin soruları
- ✅ Anket sonuçlarının istatistiksel analizi
- ✅ RESTful API tasarımı

---

## 2. Teknoloji Stack

| Teknoloji | Versiyon | Açıklama |
|-----------|----------|----------|
| **Java** | 21 | Programlama dili |
| **Spring Boot** | 4.0.0 | Ana framework |
| **Spring Security** | - | Güvenlik katmanı |
| **Spring Data JPA** | - | ORM ve veritabanı işlemleri |
| **PostgreSQL** | - | Veritabanı |
| **JWT (jjwt)** | 0.12.5 | Token tabanlı kimlik doğrulama |
| **Lombok** | - | Boilerplate kod azaltma |
| **MapStruct** | 1.5.5 | Object mapping |
| **Spring Mail** | - | E-posta gönderimi |
| **Springdoc OpenAPI** | 2.5.0 | API dokümantasyonu (Swagger) |

### pom.xml Dependency Yapısı:

```xml
<!-- Ana Spring Boot Starter'ları -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

---

## 3. Proje Mimarisi

```
src/main/java/com/dfxsurvey/dfxsurvey/
│
├── DfxSurveyApplication.java          # Ana uygulama sınıfı
│
├── configurations/                     # Konfigürasyon sınıfları
│   └── SecurityConfigurations.java
│
├── controllers/                        # REST Controller'lar
│   ├── admin/
│   │   ├── LoginController.java
│   │   └── SurveyController.java
│   ├── user/
│   │   └── SurveyActionController.java
│   └── dto/                           # Data Transfer Objects
│       ├── CreateSurveyRequest.java
│       ├── VoteRequestDto.java
│       ├── SurveyDto.java
│       └── ...
│
├── dao/                               # Data Access Object
│   ├── entity/                        # JPA Entity'leri
│   │   ├── SurveyEntity.java
│   │   ├── QuestionEntity.java
│   │   ├── LikertOptionEntity.java
│   │   ├── AnswerEntity.java
│   │   ├── CompletedSurveyEntity.java
│   │   └── TokenEntity.java
│   └── repository/                    # Spring Data JPA Repository'leri
│       ├── SurveyRepository.java
│       ├── QuestionRepository.java
│       └── ...
│
├── services/                          # İş mantığı katmanı
│   ├── ISurveyService.java           # Interface
│   ├── IVoteService.java
│   ├── IMailService.java
│   └── impl/                         # Implementasyonlar
│       ├── SurveyServiceImpl.java
│       ├── VoteServiceImpl.java
│       └── MailServiceImpl.java
│
├── filters/                          # HTTP Filtreleri
│   └── JwtAuthFilter.java
│
├── validators/                       # Validasyon sınıfları
│   ├── Validator.java               # Generic interface
│   └── impl/
│       └── SurveyValidator.java
│
├── mapper/                          # Object Mapper'lar
│   ├── ServiceMapper.java
│   └── cfg/
│       └── MapstructConfig.java
│
├── exceptions/                      # Özel Exception sınıfları
│   └── ValidationException.java
│
└── utils/                          # Yardımcı sınıflar
    └── JwtUtil.java
```

---

## 4. Katmanlı Mimari

### 4.1 Katmanlar ve Sorumlulukları

```
┌─────────────────────────────────────────────────────────────┐
│                    CONTROLLER KATMANI                        │
│  • HTTP isteklerini karşılar                                │
│  • Request/Response dönüşümü                                │
│  • Endpoint tanımları                                       │
└─────────────────────────┬───────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                     SERVICE KATMANI                          │
│  • İş mantığı (Business Logic)                              │
│  • Transaction yönetimi                                     │
│  • Validasyon koordinasyonu                                 │
└─────────────────────────┬───────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                   REPOSITORY KATMANI                         │
│  • Veritabanı işlemleri                                     │
│  • CRUD operasyonları                                       │
│  • Custom sorgular                                          │
└─────────────────────────┬───────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                      VERİTABANI                              │
│                     (PostgreSQL)                             │
└─────────────────────────────────────────────────────────────┘
```

### 4.2 Neden Katmanlı Mimari?

| Avantaj | Açıklama |
|---------|----------|
| **Separation of Concerns** | Her katman kendi sorumluluğuna odaklanır |
| **Test Edilebilirlik** | Her katman bağımsız test edilebilir |
| **Bakım Kolaylığı** | Değişiklikler izole kalır |
| **Yeniden Kullanılabilirlik** | Servisler farklı controller'lardan çağrılabilir |

---

## 5. Entity Yapısı ve İlişkiler

### 5.1 Entity-Relationship Diyagramı

```
┌──────────────────┐       ┌──────────────────┐       ┌──────────────────┐
│   SurveyEntity   │       │  QuestionEntity  │       │LikertOptionEntity│
├──────────────────┤       ├──────────────────┤       ├──────────────────┤
│ id (PK)          │       │ id (PK)          │       │ id (PK)          │
│ title            │──1:N──│ survey_id (FK)   │──1:N──│ question_id (FK) │
│ description      │       │ questionText     │       │ value            │
│ createdAt        │       │ questionType     │       │ label            │
│ expiresAt        │       │ required         │       └──────────────────┘
│ mailList         │       └──────────────────┘
└──────────────────┘
         │
         │ 1:N
         ▼
┌──────────────────┐       ┌──────────────────┐
│CompletedSurvey   │       │   AnswerEntity   │
├──────────────────┤       ├──────────────────┤
│ id (PK)          │       │ id (PK)          │
│ survey_id (FK)   │──1:N──│ response_id (FK) │
│ token_id (FK)    │       │ question_id (FK) │
│ submittedAt      │       │ freetextAnswer   │
└──────────────────┘       │ likertValue      │
                           └──────────────────┘
```

### 5.2 SurveyEntity - Ana Anket Entity'si

```java
@Entity
@Table(name = "surveys")
public class SurveyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private String mailList;

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionEntity> questions = new ArrayList<>();
}
```

**Önemli Kavramlar:**

| Annotation | Açıklama |
|------------|----------|
| `@Entity` | Bu sınıfın bir JPA entity'si olduğunu belirtir |
| `@Table(name = "surveys")` | Veritabanındaki tablo adını belirler |
| `@Id` | Primary key alanını işaretler |
| `@GeneratedValue(strategy = GenerationType.IDENTITY)` | ID'nin otomatik artırılacağını belirtir |
| `@OneToMany` | Bir-çok ilişkisini tanımlar |
| `cascade = CascadeType.ALL` | Ana entity kaydedildiğinde alt entity'ler de kaydedilir |
| `orphanRemoval = true` | Ana entity'den kaldırılan alt entity'ler silinir |

### 5.3 QuestionEntity - Soru Entity'si

```java
@Entity
@Table(name = "questions")
public class QuestionEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "survey_id")
    @JsonIgnore
    private SurveyEntity survey;

    private String questionText;
    private String questionType;  // "LIKERT" veya "FREETEXT"
    private Boolean required = true;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LikertOptionEntity> likertOptions = new ArrayList<>();
}
```

**@JsonIgnore Kullanımı:**
- Bidirectional (çift yönlü) ilişkilerde sonsuz döngüyü önler
- `Survey -> Questions -> Survey -> Questions -> ...` döngüsünü kırar

### 5.4 Soru Tipleri

| Tip | Açıklama | Örnek |
|-----|----------|-------|
| **LIKERT** | Ölçekli değerlendirme sorusu | "Hizmet kalitesi nasıldı?" (1-5 arası) |
| **FREETEXT** | Serbest metin cevaplı soru | "Eklemek istediğiniz bir şey var mı?" |

---

## 6. Repository Katmanı

### 6.1 Spring Data JPA Repository

Spring Data JPA, repository pattern'ini implement ederek veritabanı işlemlerini kolaylaştırır.

JPA Repository, Spring Data JPA’nın sunduğu ve Repository → CrudRepository → PagingAndSortingRepository soyutlamalarının üzerine eklenen en kapsamlı repository arayüzüdür.

Bu interface sayesinde:

* CRUD operasyonları (create, read, update, delete)

```java  

User user = userRepository.findById(1L).get();
user.setStatus("PASSIVE");
userRepository.save(user);

userRepository.deleteById(1L);

User user = userRepository.findById(1L).orElse(null);
```
* Custom query yazma
```java  
List<User> findByStatus(String status);

Impl -> 
List<User> activeUsers = userRepository.findByStatus("ACTIVE");

```
* JPQL / Native SQL sorguları ( SQL sorgusudur. Entity Kullanılmaz. )

```java  
@Query(value = "SELECT * FROM users WHERE status = :status", nativeQuery = true)
List<User> getByStatusNative(String status);
```

* Paging (sayfalama)

```java  
Page<User> users = userRepository.findAll(
        PageRequest.of(0, 10) // page=0, size=10
);
```
 
* Sorting (sıralama)
```java  
List<User> users = userRepository.findAll(
  Sort.by("name").ascending()
  );
```
 
* Specification / dynamic query

```java  
public class UserSpecs {
    public static Specification<User> statusEquals(String status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }
}

List<User> activeUsers =
        userRepository.findAll(UserSpecs.statusEquals("ACTIVE"));

```

* Projection (Yalnızca belirli alanları döndürmek için.)

```java  
public interface UserProjection {
    String getName();
    String getStatus();
}

@Query("SELECT u.name AS name, u.status AS status FROM User u")
List<UserProjection> getUserSummaries();

```

* Transaction yönetimi (otomatik)

```java  
@Service
public class UserService {

    @Transactional
    public void updateUserStatus(Long id, String status) {
        User user = userRepository.findById(id).get();
        user.setStatus(status);
        userRepository.save(user);
    }
}
```

* Entity yönetimi

```java  
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String status;

    // getters/setters
}

```
### 🔥 spring.jpa.hibernate.ddl-auto Özelliği

ddl-auto, entity sınıflarını kullanarak veritabanında:

* tablo oluşturma,

* tabloyu update etme,

* tabloyu silip tekrar yaratma

| Değer        | Açıklama |
|--------------|----------|
| `create`     | Her çalıştırmada tabloları **silip yeniden oluşturur**. |
| `create-drop`| Uygulama kapanınca tabloları **siler**. |
| `update`     | Yeni alan varsa **ekler**, ancak **silmez** veya mevcut kolonları değiştirmez. |
| `validate`   | Sadece şema ile entity'leri **karşılaştırır**; uyumsuzluk varsa hata verir. |
| `none`       | Veritabanına **hiçbir müdahale yapmaz** (production için önerilir). |



```java
@Repository
public interface SurveyRepository extends JpaRepository<SurveyEntity, Long> {

    // ID ile survey bul - JpaRepository'den kalıtım
    Optional<SurveyEntity> findById(Long id);

    // Custom JPQL sorgusu
    @Query("SELECT s FROM SurveyEntity s WHERE s.expiresAt > CURRENT_TIMESTAMP()")
    List<SurveyEntity> findActiveSurveys();

    // Method naming convention ile otomatik sorgu
    List<SurveyEntity> findByTitleContainingIgnoreCase(String title);
}
```

### 6.2 JpaRepository'den Gelen Hazır Metodlar

| Metod | Açıklama |
|-------|----------|
| `save(entity)` | Entity'yi kaydeder veya günceller |
| `findById(id)` | ID ile entity bulur |
| `findAll()` | Tüm entity'leri getirir |
| `deleteById(id)` | ID ile entity siler |
| `count()` | Toplam kayıt sayısını döner |
| `existsById(id)` | ID'nin var olup olmadığını kontrol eder |

### 6.3 Query Methods (Sorgu Metodları)

Spring Data JPA, metod isimlerinden otomatik sorgu oluşturur:

```java
// findBy + FieldName + Keyword
List<SurveyEntity> findByTitleContainingIgnoreCase(String title);
// → SELECT * FROM surveys WHERE LOWER(title) LIKE LOWER('%title%')

List<QuestionEntity> findBySurveyIdAndQuestionType(Long surveyId, String questionType);
// → SELECT * FROM questions WHERE survey_id = ? AND question_type = ?
```

---

## 7. Service Katmanı

### 7.1 Interface ve Implementation Ayrımı

**Neden Interface Kullanıyoruz?**
1. **Loose Coupling:** Loose Coupling (Gevşek Bağımlılık), yazılım bileşenlerinin birbirine minimum düzeyde bağımlı olmasını ifade eden bir mimari prensiptir. Bir sınıfın, başka bir sınıfın nasıl çalıştığına değil, sadece ne yaptığına bağımlı olması amaçlanır.
2. **Test Kolaylığı:** Mock nesneler oluşturulabilir
3. **Esneklik:** Farklı implementasyonlar değiştirilebilir

```java
// Interface
public interface ISurveyService {
    SurveyEntity createSurvey(CreateSurveyRequest request);
    List<SurveyEntity> getActiveSurveys();
    SurveyEntity getSurveyById(Long id);
    SurveyDetailResultResponse surveyResponse(Long id);
}

// Implementation
@Service
@RequiredArgsConstructor
public class SurveyServiceImpl implements ISurveyService {
    
    private final SurveyRepository surveyRepository;
    private final IMailService mailService;
    
    @Override
    @Transactional
    public SurveyEntity createSurvey(CreateSurveyRequest request) {
        // İş mantığı burada
    }
}
```

### 7.2 @Transactional Annotation

```java
@Override
@Transactional
public SurveyEntity createSurvey(CreateSurveyRequest request) {
    SurveyEntity survey = new SurveyEntity();
    // ... entity doldurma işlemleri
    
    SurveyEntity savedSurvey = surveyRepository.save(survey);
    surveyRepository.flush();  // Transaction'ı commit eder
    
    return savedSurvey;
}
```

| Özellik | Açıklama |
|---------|----------|
| `@Transactional` | Metodun transaction içinde çalışmasını sağlar |
| `@Transactional(readOnly = true)` | Salt okunur transaction, performans optimizasyonu |
| Rollback | Exception fırlatılırsa tüm değişiklikler geri alınır |

### 7.3 Cascade Save Örneği

#### Cascade Type Nedir?

JPA Cascade, bir parent (ana) entity üzerinde yapılan işlemlerin, ona bağlı olan child (alt) entity’lere otomatik olarak uygulanmasını sağlar.

Örneğin:

Parent kaydedilince child da kaydedilsin

Parent silinince child da silinsin

Parent detach edilince child da detach edilsin gibi işlemler otomatik yapılır.

| Cascade Type        | Açıklama                                                                                                                        |
|---------------------|---------------------------------------------------------------------------------------------------------------------------------|
| `PERSIST`           | Parent persist edildiğinde child da persist edilir (save). Siparişi kaydedince içindeki ürünlerin de kaydedilmesini istiyorsun. |
| `MERGE`             | Parent merge edildiğinde child da merge edilir (update). Order güncellenirken OrderItem güncellemelerini de otomatik yapsın.    |
| `REMOVE`            | Parent silindiğinde child da silinir. Bir siparişi sildiğinde siparişe bağlı tüm order item'lar da silinsin.                                                                                          |
| `REFRESH`           | Parent refresh edildiğinde child entity de DB’den yeniden yüklenir.                                                             |
| `DETACH`            | Parent detach edildiğinde child da persistence context’ten ayrılır.                                                             |
| `ALL`               | Tüm işlemleri kapsar: PERSIST + MERGE + REMOVE + REFRESH + DETACH.                                                              

@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)

Genelde yukarıdaki şekilde kullanılır. Cascade sadece ilişkide sahip olan taraf (owning side) üzerinden çalışır.

Owning Side Nedir? 


```java 
@Entity
public class Order {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items = new ArrayList<>();
}

@Entity
public class OrderItem {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}

```

OrderItem tarafı Owning Side → Foreign key order_id burada tutuluyor.

Order tarafı Inverse Side → mappedBy="order" ile ilişkiyi sadece gösteriyor.

```java
// Survey oluşturulurken sorular da otomatik kaydedilir
SurveyEntity survey = new SurveyEntity();
survey.setTitle(request.getTitle());

for (QuestionCreateRequest qReq : request.getQuestions()) {
    QuestionEntity question = new QuestionEntity();
    question.setSurvey(survey);  // İlişkiyi kur
    question.setQuestionText(qReq.getQuestionText());
    
    survey.getQuestions().add(question);  // Listeye ekle
}

surveyRepository.save(survey);  // Tek save ile tüm sorular da kaydedilir!
```

---

## 8. Controller Katmanı

### 8.1 REST Controller Yapısı

```java
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class SurveyController {

    private final ISurveyService surveyService;
    private final ServiceMapper serviceMapper;

    @PostMapping("/api/v1/surveys")
    public ResponseEntity<?> createSurvey(@RequestBody CreateSurveyRequest request) {
        SurveyEntity survey = surveyService.createSurvey(request);
        return ResponseEntity.ok(survey.getId());
    }

    @GetMapping("/api/v1/surveys/active")
    public ResponseEntity<?> getAllActiveSurveys() {
        List<SurveyEntity> surveys = surveyService.getActiveSurveys();
        return ResponseEntity.ok(serviceMapper.toSurveyDtos(surveys));
    }
}
```

### 8.2 Temel Annotation'lar

| Annotation | Açıklama |
|------------|----------|
| `@RestController` | REST API controller'ı işaretler, @ResponseBody içerir |
| `@RequestMapping("/admin")` | Base URL path'ini belirler |
| `@GetMapping` | HTTP GET isteğini eşler |
| `@PostMapping` | HTTP POST isteğini eşler |
| `@PathVariable` | URL'deki değişkeni alır: `/surveys/{id}` |
| `@RequestBody` | HTTP body'yi Java nesnesine dönüştürür |
| `@RequestParam` | Query parametrelerini alır: `?name=value` |

### 8.3 ResponseEntity Kullanımı

```java
// Başarılı yanıt
return ResponseEntity.ok(data);                    // 200 OK

// Oluşturuldu yanıtı
return ResponseEntity.status(201).body(data);      // 201 Created

// Hata yanıtı
return ResponseEntity.status(401).body("Invalid"); // 401 Unauthorized

// Header ile yanıt
return ResponseEntity.ok()
        .header("Authorization", "Bearer " + token)
        .body("Login successful");
```

---

## 9. DTO Pattern

### 9.1 Neden DTO Kullanıyoruz?

| Sebep | Açıklama |
|-------|----------|
| **Güvenlik** | Entity'deki hassas alanları gizler |
| **Performans** | Sadece gerekli alanları taşır |
| **Esneklik** | API ve veritabanı modeli bağımsız kalır |
| **Sonsuz Döngü** | Bidirectional ilişkilerdeki döngüyü önler |

### 9.2 Request DTO Örneği

```java
@Data
public class CreateSurveyRequest {
    private String title;
    private String description;
    private LocalDateTime expiresAt;
    private String mailList;
    private List<QuestionCreateRequest> questions;

    @Data
    public static class QuestionCreateRequest {
        private String questionText;
        private String questionType;
        private Boolean required = true;
        private List<LikertOptionRequest> likertOptions;

        @Data
        public static class LikertOptionRequest {
            private Integer value;
            private String label;
        }
    }
}
```

### 9.3 Response DTO Örneği

```java
@Data
public class SurveyDto {
    private Long id;
    private String title;
    private String description;
    private String expiresAt;
    private String mailList;
}
```

---

## 10. Güvenlik ve JWT

### 10.1 Security Konfigürasyonu

```java
@Configuration
@EnableMethodSecurity
public class SecurityConfigurations {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) 
            throws Exception {

        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/admin/login").permitAll()   // Login herkes erişebilir
                .requestMatchers("/admin/**").authenticated()  // Admin JWT gerektirir
                .anyRequest().permitAll()                      // Diğer endpoint'ler açık
        );

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

### 10.2 JWT Token Akışı

```
┌────────────┐      1. Login Request        ┌────────────┐
│            │ ─────────────────────────────▶│            │
│   Client   │      (username, password)    │   Server   │
│            │◀───────────────────────────── │            │
└────────────┘      2. JWT Token            └────────────┘
      │
      │ 3. API Request + JWT Token
      │    (Authorization: Bearer <token>)
      ▼
┌────────────┐      4. Validate Token       ┌────────────┐
│            │ ─────────────────────────────▶│            │
│   Client   │                              │   Server   │
│            │◀───────────────────────────── │            │
└────────────┘      5. Response             └────────────┘
```

### 10.3 JwtUtil Sınıfı

```java
@Component
public class JwtUtil {

    private final String SECRET = "MY_SUPER_LONG_SECRET_KEY_ADAX_123456";

    // Token oluştur
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 24*60*60*1000)) // 1 gün
                .signWith(getSigningKey())
                .compact();
    }

    // Token doğrula ve kullanıcı adını al
    public String validateTokenAndGetUsername(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
}
```

### 10.4 JWT Filter

```java
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) 
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Sadece /admin/** path'leri için JWT kontrolü
        if (path.startsWith("/admin") && !path.equals("/admin/login")) {

            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            String token = authHeader.substring(7); // "Bearer " prefix'ini kaldır

            try {
                String username = jwtUtil.validateTokenAndGetUsername(token);
                
                // Spring Security Context'e authentication bilgisini ekle
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                            username, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                        );
                SecurityContextHolder.getContext().setAuthentication(authToken);
                
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
```

---

## 11. Mail Servisi

### 11.1 Konfigürasyon (application.properties)

```properties
# Google SMTP Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password

spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
```

### 11.2 Mail Service Implementation

```java
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements IMailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendSimpleMail(String to, String subject, String text, 
                               SurveyEntity surveyEntity) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Birden fazla alıcı varsa virgülle ayır
            if (to.contains(",")) {
                helper.setTo(to.split(","));
            } else {
                helper.setTo(to);
            }
            
            helper.setSubject(subject);
            helper.setText(buildMailContent(text, surveyEntity), true); // HTML

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Mail gönderilemedi", e);
        }
    }
}
```

### 11.3 HTML Mail Template

Mail servisi, profesyonel görünümlü HTML e-posta şablonu kullanır:
- Mavi-beyaz renk paleti
- Responsive tasarım
- Anket başlığı ve açıklaması
- "Ankete Katıl" butonu

---

## 12. Validator Pattern

### 12.1 Generic Validator Interface

```java
public interface Validator<T> {
    void validate(T target);
}
```

### 12.2 Survey Validator Implementation

```java
@Component
public class SurveyValidator implements Validator<SurveyEntity> {

    @Override
    public void validate(SurveyEntity target) {
        if (target == null) {
            throw new ValidationException("Aktif anket bulunamamıştır.");
        }

        if (LocalDateTime.now().isAfter(target.getExpiresAt())) {
            throw new ValidationException("Anket tarihi dolmuştur. Oy veremezsiniz.");
        }
    }
}
```

### 12.3 Controller'da Kullanım

```java
@GetMapping("/api/v1/surveys/detail/{id}")
public ResponseEntity<?> surveyDetail(@PathVariable Long id) {
    SurveyEntity survey = surveyService.getSurveyById(id);
    
    surveyValidator.validate(survey);  // Validasyon çalışır
    
    return ResponseEntity.ok(survey);
}
```

---

## 13. MapStruct

### 13.1 MapStruct Nedir?

MapStruct, compile-time'da çalışan bir Java annotation processor'dır. Entity-DTO dönüşümlerini otomatikleştirir.

### 13.2 Konfigürasyon

```java
@MapperConfig(
        componentModel = "spring",  // Spring bean olarak inject edilebilir
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface MapstructConfig {
}
```

### 13.3 Mapper Tanımı

```java
@Mapper(config = MapstructConfig.class)
public abstract class ServiceMapper {

    public abstract List<SurveyDto> toSurveyDtos(List<SurveyEntity> surveyEntities);
}
```

### 13.4 Avantajları

| Özellik | Açıklama |
|---------|----------|
| **Type Safety** | Compile-time hata kontrolü |
| **Performans** | Reflection kullanmaz, direkt kod üretir |
| **Kolay Kullanım** | Basit interface tanımı yeterli |
| **Spring Entegrasyonu** | `@Autowired` ile inject edilebilir |

---

## 14. API Endpoints

### 14.1 Admin Endpoints (JWT Gerekli)

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| POST | `/admin/login` | Admin girişi, JWT token alır |
| POST | `/admin/api/v1/surveys` | Yeni anket oluşturur |
| GET | `/admin/api/v1/surveys/active` | Aktif anketleri listeler |
| GET | `/admin/api/v1/surveys/response/{id}` | Anket sonuçlarını getirir |

### 14.2 User Endpoints (Herkese Açık)

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| GET | `/user/api/v1/surveys/detail/{id}` | Anket detayını getirir |
| POST | `/user/api/v1/surveys/complete-survey` | Anketi tamamlar/oy verir |

### 14.3 Örnek Request/Response

**Login Request:**
```
POST /admin/login?username=admin&password=1234
```

**Login Response:**
```
Headers: Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Body: "Login successful"
```

**Create Survey Request:**
```json
POST /admin/api/v1/surveys
Authorization: Bearer <token>

{
  "title": "Müşteri Memnuniyet Anketi",
  "description": "Hizmetimizi değerlendiriniz",
  "expiresAt": "2025-12-31T00:00:00",
  "mailList": "user1@mail.com,user2@mail.com",
  "questions": [
    {
      "questionText": "Hizmet kalitesi nasıldı?",
      "questionType": "LIKERT",
      "required": true,
      "likertOptions": [
        { "value": 1, "label": "Çok Kötü" },
        { "value": 5, "label": "Mükemmel" }
      ]
    }
  ]
}
```

**Vote Request:**
```json
POST /user/api/v1/surveys/complete-survey

{
  "surveyId": 1,
  "answers": [
    { "questionId": 1, "likertValue": 5 },
    { "questionId": 2, "textAnswer": "Harika bir deneyimdi!" }
  ]
}
```

---

## 15. Veritabanı Şeması

### 15.1 Tablolar

```sql
-- Anketler
CREATE TABLE surveys (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255),
    description TEXT,
    created_at TIMESTAMP,
    expires_at TIMESTAMP,
    mail_list TEXT
);

-- Sorular
CREATE TABLE questions (
    id BIGSERIAL PRIMARY KEY,
    survey_id BIGINT REFERENCES surveys(id),
    question_text TEXT,
    question_type VARCHAR(50),  -- 'LIKERT' veya 'FREETEXT'
    required BOOLEAN DEFAULT TRUE
);

-- Likert Seçenekleri
CREATE TABLE likert_options (
    id SERIAL PRIMARY KEY,
    question_id BIGINT REFERENCES questions(id),
    value INTEGER,
    label VARCHAR(255)
);

-- Tamamlanmış Anketler
CREATE TABLE responses (
    id SERIAL PRIMARY KEY,
    survey_id BIGINT REFERENCES surveys(id),
    token_id INTEGER REFERENCES tokens(id),
    submitted_at TIMESTAMP
);

-- Cevaplar
CREATE TABLE answers (
    id SERIAL PRIMARY KEY,
    response_id INTEGER REFERENCES responses(id),
    question_id BIGINT REFERENCES questions(id),
    freetext_answer TEXT,
    likert_value INTEGER
);

-- Tokenlar
CREATE TABLE tokens (
    id SERIAL PRIMARY KEY,
    survey_id BIGINT REFERENCES surveys(id),
    token VARCHAR(255) UNIQUE,
    created_at TIMESTAMP,
    expires_at TIMESTAMP
);
```

---

## 📚 Önemli Kavramlar Özeti

| Kavram | Açıklama |
|--------|----------|
| **@Entity** | JPA entity sınıfı |
| **@Repository** | Data access layer bileşeni |
| **@Service** | Business logic bileşeni |
| **@RestController** | REST API controller |
| **@Transactional** | Transaction yönetimi |
| **DTO** | Data Transfer Object |
| **JWT** | JSON Web Token |
| **Cascade** | Alt entity'lerin otomatik yönetimi |
| **@JsonIgnore** | JSON serialization'dan hariç tutma |

---

## 🔗 Faydalı Kaynaklar

- [Spring Boot Resmi Dokümantasyonu](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring Security](https://spring.io/projects/spring-security)
- [MapStruct](https://mapstruct.org/)
- [JWT.io](https://jwt.io/)

---

**Hazırlayan:** DFX Developer School  
**Tarih:** 2025

