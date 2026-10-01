# Plan de trabajo backend: contrato para el Dashboard

## Objetivo

Alinear la API Spring con el frontend del dashboard y definir un contrato de métricas estable, seguro y normalizable en Chart.js. El backend entrega datos y reglas de negocio; no devuelve objetos Chart.js ni controla colores, ejes, leyendas o presentación.

Las tareas están ordenadas para resolver primero los contratos que bloquean los módulos Productos, Clientes y Órdenes, y después agregar métricas históricas.

## Estado actual

- `GET /api/products` y `GET /api/clients` usan `findAll()` y devuelven entidades desactivadas junto con activas.
- `ProductResponseDto` no indica si el producto está activo.
- `ClientResponseDto` devuelve solamente `id` y `clientName`, aunque los DTOs de alta y actualización admiten teléfono y correo.
- Las órdenes solo se consultan por cliente. `OrderResponseDto` contiene `id`, productos, total y estado, pero no fecha. Cada `OrderProductResponseDto` solo contiene nombre y precio; `OrderItem` sí conserva `productId`, `quantity` y `unitPrice`.
- `Order` tiene `createdAt`, `updatedAt`, `active` e historial de estado, pero no `completedAt`. Las fechas se guardan como `LocalDateTime`, sin zona explícita.
- El backend no tiene un endpoint global de órdenes ni agregaciones de ventas.
- `/api/**` exige JWT. Los endpoints de métricas deberán conservar la autenticación.
- Las respuestas usan `ApiResponse<T>` (`msg`, `statusCode`, `data`, `code`).
- `AuthService.validateLogin` ya aparece localmente actualizado para devolver `null` en `ApiResponse.code` en vez de copiar la contraseña; esa modificación de `AuthService.java` sigue pendiente de commit.

## Tareas

### 1. Autenticación y sesión

- [ ] Confirmar, probar y versionar la corrección para que la respuesta de login nunca incluya la contraseña en `code`.
- [ ] Definir el propósito de `idToken`. Actualmente el login devuelve una cadena vacía; documentar su uso o retirarlo del contrato de respuesta.
- [ ] Revocar el refresh token vigente en `POST /api/auth/logout`. Alinear si el token se recibe en el cuerpo durante la transición o mediante cookie HttpOnly cuando backend y frontend migren al patrón acordado.
- [ ] Mantener la rotación y expiración del refresh token; probar token inválido, revocado, expirado, repetido y logout idempotente.
- [ ] Mantener autenticación JWT en recursos protegidos y excluir credenciales/tokens de logs y respuestas de error.

**Criterio de cierre:** iniciar sesión no filtra credenciales; renovar rota el refresh token; logout invalida la sesión server-side.

### 2. Errores y validación

- [ ] Cambiar `ApiResponseErrorFormatting` para que `MethodArgumentNotValidException` responda HTTP 4xx coherente. Actualmente devuelve HTTP 500 aunque el `statusCode` del cuerpo represente el error de validación.
- [ ] Definir un formato estable de errores por campo en `ApiResponse`, por ejemplo `errors: [{ field, message }]`, y documentar cómo se distinguen errores de validación, negocio y servidor.
- [ ] No incluir valores sensibles ni cuerpos de request en mensajes/logs de error.
- [ ] Probar HTTP status y cuerpo para errores de validación, autenticación, autorización, recurso inexistente y errores internos.

**Criterio de cierre:** status HTTP y cuerpo coinciden; el frontend puede asociar errores de validación a campos sin depender de textos improvisados.

### 3. Productos y clientes

- [ ] Añadir `active` a los DTOs de producto y cliente, o acordar endpoints/listados que distingan explícitamente activos, inactivos y totales.
- [ ] Definir la semántica de `GET /api/products` y `GET /api/clients`: no presentar `findAll()` como conteo de activos si incluye elementos desactivados.
- [ ] Añadir `phone` y `email` a `ClientResponseDto`, tanto para listado como para detalle, para que la interfaz pueda mostrar y editar contactos sin perderlos al recargar.
- [ ] Mantener `DELETE` como desactivación si esa es la regla de negocio; documentar el estado devuelto y asegurar que la UI puede reconocerlo de forma persistente.
- [ ] Decidir cómo se conserva la identidad histórica de productos/clientes desactivados que aparecen en órdenes anteriores.

**Criterio de cierre:** los listados explican el estado de cada entidad y el CRUD de cliente preserva nombre, teléfono y correo al editar y volver a cargar.

### 4. Órdenes

- [ ] Añadir `productId` y `quantity` a `OrderProductResponseDto`; exponer el precio histórico de la línea con nombre explícito (`unitPrice` o documentar `price`).
- [ ] Añadir fecha de creación a `OrderResponseDto` para el historial. No depender de `updatedAt` para representar la fecha de venta.
- [ ] Definir una transición de estados válida en el servidor. Actualmente `PATCH /api/orders/{id}` acepta cualquier `OrderStatus` sin validar transiciones ni impedir cambios desde estados terminales.
- [ ] Impedir crear órdenes con productos desactivados. Actualmente el servicio comprueba que los IDs existan, pero no que los productos sigan activos.
- [ ] Mantener cancelaciones como estado `CANCELLED` y desactivación cuando corresponda; no borrar el historial necesario para métricas y auditoría.
- [ ] Añadir pruebas para cantidades inválidas, producto inexistente/desactivado, transiciones inválidas, cancelación repetida, orden vacía y acceso por cliente.

**Criterio de cierre:** se puede leer una orden con sus líneas identificables, cantidades, precios históricos, total y estado; el servidor rechaza productos inactivos y transiciones no permitidas.

### 5. Métricas del Dashboard

- [ ] Acordar moneda, zona horaria, rango máximo y reglas de negocio para ventas antes de sumar importes.
- [ ] Crear DTOs de métricas independientes de Chart.js: resumen, rango, gráfico, categorías y series.
- [ ] Implementar agregaciones de base de datos para conteos/promedios, productos más solicitados, ventas mensuales y ventas mensuales por producto. No descargar órdenes completas para agregarlas en el navegador ni cargarlas todas a memoria en Java.
- [ ] Exponer `GET /api/metrics/dashboard` con JWT, parámetros `from`, `toExclusive`, `timeZone` y `topProductsLimit` acotados.
- [ ] Mantener `ApiResponse<DashboardMetricsResponse>`. Ante periodos sin datos devolver estructuras válidas con listas vacías y ceros, no `404` ni `null` ambiguos.
- [ ] Usar una respuesta genérica con categorías y series numéricas; el frontend asigna labels/data a Chart.js y define visualización.
- [ ] Probar límites de fechas, zona horaria, meses vacíos, top N, decimales, órdenes canceladas/no completadas, productos históricos y ausencia de datos.

**Decisiones de negocio pendientes:**

1. Contar como ventas solo órdenes `COMPLETED` y activas, excluyendo `PENDING`, `PREPARING`, `READY` y `CANCELLED`, sujeto a aprobación de negocio.
2. Agrupar por fecha de finalización, no por creación. Añadir `completedAt` o usar de forma inequívoca el historial de estados.
3. Guardar instantes en UTC y aplicar una zona IANA acordada al agrupar meses/días; las fechas actuales son `LocalDateTime`.
4. Definir moneda ISO 4217. `BigDecimal` por sí solo no identifica moneda.
5. Definir si el resumen cuenta solo activos o también inactivos; devolver campos explícitos si se necesitan ambos.
6. Mantener en agregaciones ventas históricas de productos luego desactivados usando la línea de orden y su precio histórico.

El contrato detallado y el ejemplo JSON están en [PLAN_METRICAS_DASHBOARD.md](PLAN_METRICAS_DASHBOARD.md).

**Criterio de cierre:** una llamada agregada y protegida proporciona resumen/series precisos, vacíos estables y datos suficientes para normalizar a Chart.js.

### 6. Pruebas, seguridad y entrega

- [ ] Añadir pruebas unitarias y de integración para auth, errores, productos, clientes, órdenes y métricas.
- [ ] Verificar consultas agregadas con volúmenes representativos e índices; limitar rangos para proteger la base de datos.
- [ ] Añadir el `Jenkinsfile` backend con etapas de pruebas/build y un despliegue futuro parametrizado. Hosts/IPs, directorios, rutas y credenciales deben venir de parámetros, variables de entorno o Jenkins Credentials, nunca de valores de máquina fijos.
- [ ] Mantener secretos fuera del repositorio y del artefacto; configurar DB y claves RSA desde el entorno/gestor de secretos.

## Orden sugerido

1. Confirmar la corrección local del campo `code` y cerrar auth/logout.
2. Corregir el contrato de errores.
3. Completar DTOs de productos, clientes y líneas de orden; definir reglas de estado/productos activos.
4. Acordar moneda, fechas, zona y semántica de ventas.
5. Implementar y probar `/api/metrics/dashboard`.
6. Añadir pipeline Jenkins parametrizado y completar pruebas/seguridad.

## Suite de pruebas backend

**Estado:** Pendiente. Actualmente no hay clases de prueba Java en `src/test`; crear la suite antes de dar por cerrados los contratos y antes del primer despliegue.

### Alcance

- [ ] Confirmar/configurar JUnit 5, Spring Boot Test, Mockito y un perfil de pruebas sin credenciales externas.
- [ ] **Unitarias de servicios:** auth (no filtrar contraseñas, tokens y errores), refresh (rotación, expirado, revocado y repetido), logout (revocación e idempotencia), CRUD de productos/clientes, estado `active`, transiciones/cancelación de órdenes y cálculo del total usando precio histórico por cantidad.
- [ ] **Persistencia/repositorios:** probar filtros de activos y consultas agregadas con PostgreSQL real de pruebas, preferiblemente Testcontainers; cubrir rangos de fechas, meses sin ventas, productos históricos y orden/límite de resultados.
- [ ] **Web/MVC:** probar status HTTP, envoltorio `ApiResponse`, DTOs y validación para auth, productos, clientes, órdenes y `/api/metrics/dashboard`.
- [ ] **Seguridad:** verificar rutas públicas/privadas, JWT ausente/inválido/válido, revocación de refresh, CORS/preflight permitido y que respuestas/logs no revelen secretos.
- [ ] **Integración de flujos críticos:** registrar/iniciar sesión/renovar/cerrar sesión; CRUD con desactivación; crear una orden, actualizar estado, cancelar y consultar historial; consumir métricas vacías y con datos.
- [ ] Usar reloj fijo o controlable para expiraciones y fechas, datos de prueba aislados y limpieza determinista entre casos.
- [ ] Ejecutar `./mvnw test` (Unix) o `mvnw.cmd test` (Windows) localmente y en Jenkins; hacer que el pipeline falle si cualquier prueba falla.

### Criterios de aceptación

- Las pruebas unitarias de reglas de negocio y errores son deterministas y no requieren credenciales ni servicios externos; las pruebas de repositorio usan una base de datos aislada.
- Las consultas específicas de PostgreSQL se prueban contra el mismo motor que producción, no solo contra mocks o una base distinta.
- Cada endpoint tiene cobertura de éxito, validación y autorización aplicable; los estados HTTP coinciden con `ApiResponse.statusCode`.
- La suite se ejecuta en CI antes de empaquetar/desplegar y deja evidencia del resultado.
