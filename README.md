# Restaurant API

## Requisitos

- Java 17
- PostgreSQL
- OpenSSL para generar una pareja RSA local, si todavía no existe

## Configuración

`src/main/resources/application.properties` obtiene la configuración sensible del entorno:

| Variable | Uso | Valor por defecto |
| --- | --- | --- |
| `DB_URL` | URL JDBC de PostgreSQL | `jdbc:postgresql://localhost:5432/restaurant` |
| `DB_USERNAME` | Usuario de PostgreSQL | Obligatoria |
| `DB_PASSWORD` | Contraseña de PostgreSQL | Obligatoria |
| `RSA_PRIVATE_KEY_LOCATION` | Ubicación de la clave privada RSA | `classpath:keys/private_key.pem` |
| `RSA_PUBLIC_KEY_LOCATION` | Ubicación de la clave pública RSA | `classpath:keys/public_key.pem` |
| `CORS_ALLOWED_ORIGINS` | Orígenes web permitidos, separados por comas | `http://localhost:4200` |

Spring Boot resuelve los placeholders al iniciar. No carga archivos `.env` automáticamente. En local, las keys de clase están en `src/main/resources/keys/`; esa carpeta está ignorada por Git. Si no tienes una pareja local, puedes generarla con OpenSSL:

```powershell
New-Item -ItemType Directory -Force src/main/resources/keys
openssl genpkey -algorithm RSA -out src/main/resources/keys/private_key.pem -pkeyopt rsa_keygen_bits:2048
openssl pkey -in src/main/resources/keys/private_key.pem -pubout -out src/main/resources/keys/public_key.pem
```

No compartas ni subas la clave privada.

## Desarrollo local

En PowerShell, establece las variables en la terminal desde la que iniciarás Maven. El password se solicita sin mostrarse ni incluirse en el comando:

```powershell
$env:DB_URL = 'jdbc:postgresql://localhost:5432/restaurant'
$env:DB_USERNAME = Read-Host 'Usuario PostgreSQL'
$securePassword = Read-Host 'Password PostgreSQL' -AsSecureString
$env:DB_PASSWORD = [System.Net.NetworkCredential]::new('', $securePassword).Password
.\mvnw.cmd spring-boot:run
```

Al detener la aplicación, puedes limpiar las variables de esa terminal:

```powershell
Remove-Item Env:DB_URL, Env:DB_USERNAME, Env:DB_PASSWORD
```

En Eclipse, abre **Run > Run Configurations...**, selecciona **Spring Boot App** (o **Java Application**) y agrega `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` en la pestaña **Environment**. Si arrancas mediante el goal Maven `spring-boot:run`, configura esas variables en la pestaña **Environment** de esa configuración Maven. No compartas una configuración de Eclipse que contenga passwords.

## Producción

Configura `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` en el gestor de secretos o en el entorno del servicio que ejecute la aplicación. No los guardes en `application.properties`, `pom.xml` ni en el repositorio. Por ejemplo, el servicio puede ejecutar:

Si el frontend se sirve desde otro origen, configura `CORS_ALLOWED_ORIGINS` con el origen exacto del frontend (o la lista de orígenes QA/producción separados por comas). No uses `*` en producción.

```text
java -jar target/spring_api_restaurant_demo-0.0.1-SNAPSHOT.jar
```

Para producción, monta la pareja RSA como archivos protegidos fuera del JAR y configura sus rutas, por ejemplo:

```text
RSA_PRIVATE_KEY_LOCATION=file:/run/secrets/private_key.pem
RSA_PUBLIC_KEY_LOCATION=file:/run/secrets/public_key.pem
SPRING_JPA_SHOW_SQL=false
```

Los archivos RSA locales no deben incluirse en el artefacto de producción. Usa un gestor de secretos o un montaje protegido del entorno de despliegue. Si las credenciales anteriores del repositorio eran reales y válidas, cámbialas; reemplazarlas aquí no revoca credenciales ya expuestas.
