# CLAUDE.md

Bu dosya, Claude Code bu projede çalışırken her oturumun başında otomatik olarak okunur.
Projenin "kısa tanıtımı" gibi düşünün: ne yapıyoruz, nasıl çalıştırıyoruz, hangi kurallara uyuyoruz.

## Proje Hakkında

**dfx-survey**, anket oluşturma ve oylama uygulamasıdır.

- **Admin** anket oluşturur, sonuçları ve istatistikleri görür.
- **Kullanıcı (katılımcı)** giriş yapar, anketi görüntüler ve oy verir.
- **AI Usage** modülü, Claude API ile prompt kalitesini değerlendirir.

## Teknolojiler

- Java 21, Spring Boot 4
- Spring Web, Spring Data JPA, Spring Security (JWT – jjwt)
- PostgreSQL
- Lombok, MapStruct
- springdoc-openapi (Swagger UI)
- anthropic-java (Claude API)

## Sık Kullanılan Komutlar

```bash
# Veritabanını ayağa kaldır (Docker)
docker run -d --name dfx-postgres -e POSTGRES_DB=dfx_survey_db -e POSTGRES_USER=dfx_user -e POSTGRES_PASSWORD=<sifre> -p 5432:5432 postgres

# Uygulamayı çalıştır
./mvnw spring-boot:run

# Uygulamayı H2 (bellek içi veritabanı) ile çalıştır; Docker gerekmez
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2

# Testleri çalıştır
./mvnw test

# Tek bir test sınıfını çalıştır
./mvnw test -Dtest=VoteRequestValidatorTest
```

Swagger UI: http://localhost:8080/swagger-ui.html

## Paket Yapısı

`src/main/java/com/dfxsurvey/dfxsurvey/` altında:

| Paket | Görevi |
|---|---|
| `controllers/admin` | Admin endpoint'leri (`/admin/...`) |
| `controllers/user` | Katılımcı endpoint'leri (`/user/...`) |
| `controllers/dto` | Request / response DTO'ları |
| `services` + `services/impl` | İş mantığı (arayüz `IXxxService`, implementasyon `XxxServiceImpl`) |
| `dao/entity` | JPA entity'leri (`XxxEntity`) |
| `dao/repository` | Spring Data repository'leri |
| `validators` + `validators/impl` | `Validator<T>` arayüzünü uygulayan doğrulayıcılar |
| `mapper` | MapStruct mapper'ları |
| `filters` | JWT filtresi |
| `aiusage` | Claude API ile prompt kalite değerlendirme modülü |

## Kodlama Kuralları

- Katmanlı mimariye uy: **Controller → Validator → Service → Repository**. Controller'da iş mantığı yazma.
- Servisler arayüz üzerinden kullanılır: `IVoteService` → `VoteServiceImpl`.
- Bağımlılıklar constructor injection ile alınır: `@RequiredArgsConstructor` + `private final`.
- Doğrulama hataları için `ValidationException` fırlat.
- Entity'leri doğrudan dışarı döndürmek yerine DTO + MapStruct kullanmayı tercih et.
- Yorumlar ve hata mesajları **Türkçe** yazılır.
- Endpoint'ler versiyonludur: `/api/v1/...`

## Test Kuralları

- JUnit 5 + Mockito + AssertJ kullanılır.
- Birim testlerde `@ExtendWith(MockitoExtension.class)`, `@Mock` ve `@InjectMocks` kullan.
- Test isimleri `should_<beklenen_davranis>` formatındadır.
  Örnek: `should_reject_user_who_already_voted`
- Yeni bir validator veya servis metodu eklendiğinde mutlaka test de eklenir.

## Güvenlik

- API anahtarları, şifreler ve token'lar **koda yazılmaz**; ortam değişkeninden okunur
  (örn. `ANTHROPIC_API_KEY`).
- `application.properties` dosyasına gerçek şifre commit etme.

## Claude İçin Notlar

- Değişiklik yapmadan önce ilgili sınıfı ve benzer bir örneği oku, aynı stili takip et.
- Büyük değişikliklerden önce plan çıkar ve onay iste.
- İş bittiğinde `./mvnw test` ile testlerin geçtiğini doğrula.
