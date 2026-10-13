# Keycloak Role Converter

Библиотека для интеграции ролей из JWT-токенов Keycloak со Spring Security. Преобразует роли из `realm_access` и `resource_access` в `GrantedAuthority`, совместимые с `hasRole()` и `hasAuthority()`.

## Возможности

- Извлечение Realm Roles из `realm_access.roles`.
- Извлечение Client Roles из `resource_access.{clientId}.roles`.
- Настройка извлечения Realm Roles через `application.yml` или `application.properties`.
- Выбор клиента, из которого извлекаются Client Roles.
- Преобразование ролей в формат `ROLE_{NAME}`.
- Автоматическая регистрация конфигурации через Spring Boot Auto-Configuration.
- Удаление повторяющихся ролей.

## Требования

- Java 21 или новее.
- Spring Boot 3.x.
- Spring Security OAuth2 Resource Server.
- Keycloak, выдающий JWT с ролями в стандартных claims.

## 1. Подключение библиотеки

Библиотека распространяется через [JitPack](https://jitpack.io/#Gendarmee/role-converter-keycloak).

### Gradle — Groovy DSL

Добавьте JitPack в `build.gradle`:

```groovy
repositories {
    mavenCentral()
    maven {
        url 'https://jitpack.io'
    }
}
```

Добавьте зависимость:

```groovy
dependencies {
    implementation 'com.github.Gendarmee:role-converter-keycloak:v1.0.2'
}
```

### Gradle — Kotlin DSL

В `build.gradle.kts`:

```kotlin
repositories {
    mavenCentral()
    maven("https://jitpack.io")
}
```

Зависимость:

```kotlin
dependencies {
    implementation("com.github.Gendarmee:role-converter-keycloak:v1.0.2")
}
```

### Maven

Добавьте репозиторий в `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Добавьте зависимость:

```xml
<dependency>
    <groupId>com.github.Gendarmee</groupId>
    <artifactId>role-converter-keycloak</artifactId>
    <version>v1.0.2</version>
</dependency>
```

Если JitPack ещё не собирал эту версию, откройте [страницу сборки библиотеки](https://jitpack.io/#Gendarmee/role-converter-keycloak) и проверьте статус `v1.0.2`.

## 2. Как использовать

После подключения зависимости библиотека должна зарегистрировать `KeycloakConverter` через Spring Boot Auto-Configuration.

Чтобы Spring Security использовал его при обработке JWT, настройте `JwtAuthenticationConverter`.

### Конфигурация Spring Security

```java
import com.gendarme.role.converter.config.KeycloakConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(
            KeycloakConverter keycloakConverter
    ) {
        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(keycloakConverter);

        return converter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter jwtAuthenticationConverter
    ) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/users/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter)
                )
            );

        return http.build();
    }
}
```

В этом примере `KeycloakConverter` отвечает за преобразование ролей. Проверка подписи JWT, срока действия токена и других параметров его валидности остаётся задачей Spring Security.

Для проверки JWT также необходимо настроить issuer:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8080/realms/my-realm
```

Замените адрес и имя realm на значения вашего Keycloak.

## 3. Конфигурация библиотеки

Настройки библиотеки имеют общий префикс `converter.keycloak`.

### application.yml

```yaml
converter:
  keycloak:
    realm-roles-enabled: true
    client-id: my-client
```

### application.properties

```properties
converter.keycloak.realm-roles-enabled=true
converter.keycloak.client-id=my-client
```

### Доступные параметры

| Параметр | Тип | Значение по умолчанию | Описание |
|---|---|---|---|
| `converter.keycloak.realm-roles-enabled` | `boolean` | `true` | Включает извлечение Realm Roles |
| `converter.keycloak.client-id` | `String` | Не задан | Идентификатор клиента Keycloak, из которого извлекаются Client Roles |

`client-id` необходимо задать, если приложение должно использовать роли конкретного клиента из `resource_access`.

### Вариант 1. Использовать только Realm Roles

```yaml
converter:
  keycloak:
    realm-roles-enabled: true
```

Realm Roles извлекаются из `realm_access.roles`. Client Roles не используются, если `client-id` не задан.

### Вариант 2. Использовать только Client Roles

```yaml
converter:
  keycloak:
    realm-roles-enabled: false
    client-id: my-client
```

Realm Roles отключены, а Client Roles извлекаются из `resource_access.my-client.roles`.

### Вариант 3. Использовать Realm Roles и Client Roles

```yaml
converter:
  keycloak:
    realm-roles-enabled: true
    client-id: my-client
```

При включённых обоих механизмах роли из обоих claims объединяются. Повторяющиеся роли не дублируются.

## 4. Как преобразуются роли

Предположим, JWT содержит следующие claims:

```json
{
  "realm_access": {
    "roles": [
      "user",
      "admin"
    ]
  },
  "resource_access": {
    "my-client": {
      "roles": [
        "manager",
        "user"
      ]
    }
  }
}
```

При включённых Realm Roles и Client Roles библиотека извлечёт роли `user`, `admin` и `manager`.

Каждая роль преобразуется в `GrantedAuthority` с префиксом `ROLE_` и верхним регистром:

```text
ROLE_USER
ROLE_ADMIN
ROLE_MANAGER
```

Это позволяет использовать стандартные правила Spring Security:

```java
.requestMatchers("/admin/**").hasRole("ADMIN")
```

или:

```java
.requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
```

`hasRole("ADMIN")` автоматически учитывает стандартный префикс `ROLE_`. В `hasAuthority()` указывается полное имя authority.

## 5. Где должны находиться роли в Keycloak

### Realm Roles

Realm Roles назначаются пользователю или его группам на уровне realm. В access token они должны попадать в claim следующего вида:

```json
{
  "realm_access": {
    "roles": [
      "admin",
      "user"
    ]
  }
}
```

### Client Roles

Client Roles создаются в настройках конкретного клиента Keycloak. В access token они должны попадать в `resource_access`:

```json
{
  "resource_access": {
    "my-client": {
      "roles": [
        "manager",
        "operator"
      ]
    }
  }
}
```

Убедитесь, что необходимые роли действительно присутствуют в access token. Наличие роли в настройках Keycloak само по себе не гарантирует её включение в токен: это зависит от назначения ролей и настроек протокола.

## 6. Частые проблемы

**Роль не определяется через `hasRole()`.**

Проверьте, что роль присутствует в JWT, находится в поддерживаемом claim и не отключена настройками библиотеки. Также проверьте регистр и используемый префикс `ROLE_`.

**Client Roles не извлекаются.**

Убедитесь, что `converter.keycloak.client-id` совпадает с ключом клиента в `resource_access`. Это именно идентификатор клиента в claim, а не имя realm.

**Spring не находит `KeycloakConverter`.**

Проверьте, что JitPack успешно собрал указанную версию и что библиотека содержит корректные записи Spring Boot Auto-Configuration.

**JWT возвращает 401.**

Проверьте `issuer-uri`, подпись токена, срок действия и настройки Spring Security. Конвертер ролей не заменяет валидацию JWT.
