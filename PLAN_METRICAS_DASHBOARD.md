# Plan de alcance: métricas para el Dashboard

## Objetivo

Definir un contrato estable para que el frontend Angular consuma métricas agregadas del restaurante y las normalice a Chart.js. El backend entrega datos de negocio; no debe enviar objetos `Chart.js`, colores, opciones de ejes ni configuración visual.

El Dashboard actual puede obtener productos y clientes desde sus endpoints de listado. Las métricas de órdenes y ventas descritas aquí son futuras y requieren agregaciones que la API todavía no ofrece.

## Estado actual y brechas

- `GET /api/products` devuelve productos con `id`, `name` y `unitPrice`; no incluye `active`.
- `GET /api/clients` devuelve clientes con `id` y `clientName`.
- Las consultas de listados usan `findAll()`, por lo que incluyen filas desactivadas; no se debe etiquetar su conteo como "activos".
- Las órdenes solo se pueden listar por cliente mediante `GET /api/orders/client/{clientId}`. No existe listado ni resumen global.
- `OrderResponseDto` devuelve total y estado, pero no fecha, `clientId` ni artículos con cantidad.
- `OrderProductResponseDto` devuelve únicamente nombre y precio. El modelo `OrderItem` sí conserva `productId`, `quantity` y `unitPrice`.
- `Order` tiene `createdAt`, `updatedAt`, `active` e historial de estados, pero no un campo explícito `completedAt`. Los tiempos actuales son `LocalDateTime` sin zona horaria.
- `/api/**` está protegido por JWT. Las métricas deben conservar esa protección.
- `ApiResponse<T>` es el envoltorio actual: `msg`, `statusCode`, `data` y `code`.

## Contrato propuesto

### Endpoint

`GET /api/metrics/dashboard`

Parámetros de consulta:

| Parámetro | Tipo | Requisito | Semántica |
| --- | --- | --- | --- |
| `from` | `YYYY-MM-DD` | Obligatorio | Inicio inclusivo del rango. |
| `toExclusive` | `YYYY-MM-DD` | Obligatorio | Fin exclusivo del rango. |
| `timeZone` | ID IANA | Obligatorio | Zona usada para asignar órdenes a días y meses, por ejemplo `America/Mexico_City`. |
| `topProductsLimit` | entero | Opcional; por defecto 5, máximo 10 | Cantidad de productos más solicitados. |

La respuesta usa el `ApiResponse<DashboardMetricsResponse>` existente. `data` no debe ser `null` ante un resultado vacío: debe contener resumen y colecciones vacías o períodos con valor cero.

### Forma de respuesta

```json
{
  "msg": "OK",
  "statusCode": "OK",
  "data": {
    "generatedAt": "2026-10-01T18:30:00Z",
    "range": {
      "from": "2026-01-01",
      "toExclusive": "2026-07-01",
      "timeZone": "America/Mexico_City"
    },
    "summary": {
      "productCount": 18,
      "clientCount": 42,
      "averageUnitPrice": 126.5
    },
    "charts": [
      {
        "key": "top-requested-products",
        "title": "Productos más solicitados",
        "categoryAxisLabel": "Producto",
        "valueAxisLabel": "Unidades",
        "unit": "count",
        "categories": [
          { "key": "product:12", "label": "Sopa" },
          { "key": "product:8", "label": "Tarta" }
        ],
        "series": [
          {
            "key": "ordered-quantity",
            "label": "Unidades solicitadas",
            "values": [126, 93]
          }
        ]
      },
      {
        "key": "monthly-sales",
        "title": "Ventas por mes",
        "categoryAxisLabel": "Mes",
        "valueAxisLabel": "Ventas",
        "unit": "currency",
        "currencyCode": "<ISO-4217 acordado>",
        "categories": [
          { "key": "2026-01", "label": "2026-01" },
          { "key": "2026-02", "label": "2026-02" }
        ],
        "series": [
          { "key": "total-sales", "label": "Ventas totales", "values": [12500.5, 0] }
        ]
      },
      {
        "key": "monthly-sales-by-product",
        "title": "Ventas mensuales por producto",
        "categoryAxisLabel": "Mes",
        "valueAxisLabel": "Ventas",
        "unit": "currency",
        "currencyCode": "<ISO-4217 acordado>",
        "categories": [
          { "key": "2026-01", "label": "2026-01" },
          { "key": "2026-02", "label": "2026-02" }
        ],
        "series": [
          { "key": "product:12", "label": "Sopa", "values": [8000, 0] },
          { "key": "product:8", "label": "Tarta", "values": [4500.5, 0] }
        ]
      }
    ]
  },
  "code": null
}
```

El ejemplo ilustra la forma, no fija la moneda. Antes de implementar ventas se debe elegir el `currencyCode` de negocio; el modelo actual usa `BigDecimal` y no define una moneda.

### Reglas de normalización

- `categories` define el orden y la identidad estable de cada posición. Cada serie contiene un `values` con exactamente la misma longitud y el mismo orden.
- `key` identifica la métrica, categoría o serie sin depender de su texto visible. En productos conviene incluir el ID (`product:<id>`); en meses se usa `YYYY-MM`.
- `values` contiene números JSON, nunca números serializados como texto. Para ausencia de ventas dentro de un período se devuelve `0`; no se usa `null` para representar cero.
- El frontend normaliza un gráfico con `labels = categories.map(category => category.label)` y `datasets = series.map(item => ({ label: item.label, data: item.values }))`. El tipo `bar`, `line`, colores, leyenda, tooltip y accesibilidad se deciden en Angular.
- `unit` distingue conteos (`count`) de importes (`currency`). Para `currency`, `currencyCode` es obligatorio y el frontend aplica formato monetario con `Intl.NumberFormat`.
- El orden de categorías de productos se fija por cantidad descendente; las categorías mensuales van en orden cronológico ascendente.
- Una respuesta válida sin filas devuelve `summary` con conteos y promedios definidos, `categories: []` cuando no hay categorías y `series: []` o series vacías. No se responde `404` por falta de datos.

## Decisiones de negocio antes de codificar

1. **Qué cuenta como venta:** se recomienda sumar solo órdenes `COMPLETED` que sigan activas, excluyendo `CANCELLED`, `PENDING`, `PREPARING` y `READY`. Confirmar con negocio antes de fijarlo.
2. **Fecha de venta:** decidir si se agrupa por fecha de creación o de finalización. Para ventas se recomienda la transición a `COMPLETED`; puede usarse el historial de estados o añadirse `completedAt`.
3. **Zona horaria:** acordar una zona de negocio y cómo se guardarán/interpretarán `LocalDateTime`. Para evitar cambios de mes por ambiente, se recomienda almacenar instantes UTC y aplicar la zona indicada solo al agrupar.
4. **Moneda:** definir la moneda única del catálogo o almacenar moneda por precio/orden. No mostrar símbolo ni asumir una moneda en el frontend sin este acuerdo.
5. **Productos desactivados:** conservar productos históricos en los agregados de órdenes; la desactivación no debe borrar ventas previas ni cambiar su ID/nombre histórico.
6. **Conteos del resumen:** decidir si `productCount` y `clientCount` incluyen entidades desactivadas. Para compatibilidad inicial, deben coincidir con los registros que devuelven hoy sus endpoints GET (`findAll`, incluidos desactivados), o exponer conteos explícitos `total` y `active`.

## Plan de implementación

1. Acordar los cinco puntos de negocio anteriores y documentar moneda y zona por entorno.
2. Definir DTOs Java para resumen, rango, gráfico, categoría y serie; mantenerlos independientes de Chart.js.
3. Añadir consultas de agregación en repositorios. Calcular ventas con el `unitPrice` histórico de `OrderItem * quantity`, no con el precio actual del producto. Evitar cargar todas las órdenes y líneas a memoria para agregarlas en Java.
4. Exponer `GET /api/metrics/dashboard` con autenticación JWT y validación de rango (`from < toExclusive`, límite de rango y `topProductsLimit` acotado).
5. Añadir pruebas de repositorio/servicio/controlador para períodos vacíos, ceros mensuales, orden y límite de productos, importes con decimales, estados cancelados/no completados y límites de zona horaria.
6. Documentar ejemplos reales y errores de parámetros. Los errores de validación deben conservar un estado HTTP 4xx coherente; no responder 500 para errores de cliente.
7. Consumir el contrato desde Angular y mapear las estructuras genéricas a Chart.js, conservando tabla o descripción accesible y estados de carga/error/vacío.

## Criterios de aceptación

- Una sola llamada puede alimentar el resumen y las series acordadas sin pedir una lista global de órdenes al navegador.
- Cada serie se puede mapear a Chart.js sin conversiones específicas por métrica, salvo formateo de unidad/moneda.
- Las sumas respetan estado final, cancelaciones, rango `[from, toExclusive)` y zona horaria acordados.
- Los meses sin ventas aparecen con cero; la ausencia total de datos es una respuesta válida y estable.
- El backend no expone datos personales de clientes en las métricas.
- Las consultas agregan en la base de datos y soportan rangos acotados; índices y límites se validan con el volumen esperado.
- Las respuestas mantienen el envoltorio `ApiResponse<T>`, requieren JWT y tienen pruebas para el contrato.

## Fuera de alcance

- Devolver objetos `Chart.js` o permitir que el backend controle presentación, paleta, animaciones o componentes visuales.
- Exponer una lista global de órdenes o información personal solo para construir gráficos.
- Inventar ventas cuando faltan datos, moneda, fecha de finalización o semántica de estados.
