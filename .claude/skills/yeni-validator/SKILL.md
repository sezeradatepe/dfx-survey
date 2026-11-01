---
name: yeni-validator
description: Projeye yeni bir doğrulayıcı (Validator) sınıfı ve onun birim testini ekler. Kullanıcı "validator ekle", "doğrulama ekle", "şu alanı kontrol et" gibi bir istekte bulunduğunda kullan.
---

# Yeni Validator Ekleme

Bu skill, dfx-survey projesine projenin mevcut stiline uygun yeni bir validator eklemek için
izlenecek adımları tanımlar. Kullanıcı `/yeni-validator` yazarak da çağırabilir.

## Adımlar

1. **Örnek oku:** Önce `validators/Validator.java` arayüzünü ve
   `validators/impl/VoteRequestValidator.java` sınıfını oku. Yeni sınıf aynı stilde olmalı.

2. **Sınıfı oluştur:** `src/main/java/com/dfxsurvey/dfxsurvey/validators/impl/` altına
   `XxxValidator.java` dosyasını ekle.
   - `Validator<T>` arayüzünü uygula.
   - `@Component` ve `@RequiredArgsConstructor` kullan.
   - Kural ihlalinde `ValidationException` fırlat, mesaj **Türkçe** olsun.

3. **Testi yaz:** `src/test/java/com/dfxsurvey/dfxsurvey/validators/impl/` altına
   `XxxValidatorTest.java` ekle.
   - `@ExtendWith(MockitoExtension.class)` kullan.
   - En az bir **başarılı** ve bir **hatalı** senaryo yaz.
   - Test isimleri `should_...` formatında olsun.

4. **Bağla:** Validator'ı ilgili controller'a inject et ve servis çağrısından **önce** çalıştır.

5. **Doğrula:** `./mvnw test -Dtest=XxxValidatorTest` ile testi çalıştır ve sonucu kullanıcıya bildir.

## Şablon

```java
package com.dfxsurvey.dfxsurvey.validators.impl;

import com.dfxsurvey.dfxsurvey.exceptions.ValidationException;
import com.dfxsurvey.dfxsurvey.validators.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class XxxValidator implements Validator<XxxDto> {

    @Override
    public void validate(XxxDto target) {
        if (target == null) {
            throw new ValidationException("İstek boş olamaz");
        }
        // Diğer kurallar...
    }
}
```

## Yapılmaması Gerekenler

- Doğrulama mantığını controller veya servis içine yazma.
- Testsiz validator ekleme.
- `RuntimeException` gibi genel exception fırlatma; `ValidationException` kullan.
