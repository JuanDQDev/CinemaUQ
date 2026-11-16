# Taller de integración de patrones · Clase 21

Programación II · 2026-2 · nota de seguimiento, en grupo. Entregado en clase el 11 de noviembre de 2026.

Esta es la transcripción del documento que entregamos (versión original en PDF, en Moodle). Las tablas se conservan como se entregaron. Al final, una nota indica qué cambió en el código después del taller.

| Campo | Valor |
|---|---|
| Grupo | CinemaUQ |
| Integrantes | Andrés David Santafé · Juan Diego Quitián · Juan Carlos Polanía |
| Repositorio | https://github.com/JuanDQDev/CinemaUQ |
| Revisión | `main` en `fe35a15` · commits del equipo `157efee`, `b708e6c`, `15131cd` |

## Parte A · Mapa de patrones

| Patrón | Clase(s) de nuestro código | Problema que resuelve | Consecuencias (el precio) | Lo redactó |
|---|---|---|---|---|
| **Creacionales** | | | | |
| Singleton | `ConsecutivoFactura` | Genera una secuencia común de facturas FAC para las compras de la aplicación. | El contador vive en memoria y vuelve a 1 al reiniciar. Su estado se comparte entre pruebas en la misma JVM. | Juan Diego Quitián |
| Builder | `Funcion.Builder` | Construye la función por pasos y valida sus cinco campos obligatorios en `build()`. | Los campos se declaran en `Funcion` y en `Builder`. Los datos faltantes se detectan al ejecutar `build()`. | Juan Diego Quitián, Andrés David Santafé y Juan Carlos Polanía |
| Prototype | `Clonable`, `Sala`, `Asiento` | Clona la sala y cada asiento. Cada función conserva su propio mapa de ocupación. | Cada función guarda una copia de todos los asientos. Clonar una plantilla ocupada también copia ese estado. | Juan Carlos Polanía |
| Abstract Factory | No lo usamos | | | |
| Factory Method | No lo usamos | | | |
| **Estructurales** | | | | |
| Adapter | `MedioPago`, `AdaptadorTarjetaVirtual`, `TarjetaVirtual` | Traduce verificar, cobrar y devolver a las operaciones de la tarjeta virtual. | Cada medio nuevo debe cumplir el contrato. `CinemaFacade` instancia directamente el adaptador actual. | Juan Diego Quitián y Andrés David Santafé |
| Proxy | No lo usamos | | | |
| Composite | `Producto`, `Combo`, `ElementoProducto` | Permite sumar productos y combos mediante `calcularPrecio()`. Un combo puede contener otros combos. | El cálculo recorre el árbol y aplica el descuento de cada combo. `AgregarProducto` debe evitar ciclos. | Andrés David Santafé y Juan Diego Quitián |
| Decorator | `EntradaBase`, `EntradaDecorator`, `LenteTresDDecorator`, `PantallaIMAXDecorator`, `Entrada` | Añade recargos de 3D e IMAX a la entrada base. Una sala IMAX 3D combina ambos. | Cada recargo agrega una clase. `crearEntrada()` depende de las etiquetas del formato para armar la cadena. | Andrés David Santafé |
| Facade | `CinemaFacade` | Ofrece a JavaFX un acceso común a cartelera, totales y confirmación de compras. | Concentra consultas e historial en memoria y queda acoplada a `ServicioCompra` y al adaptador de tarjeta. | Juan Carlos Polanía y Juan Diego Quitián |
| **De comportamiento** | | | | |
| Observer | `ContextoAplicacion`, `PrincipalController` | Los listeners de JavaFX actualizan el saldo visible y el indicador de pasos al cambiar sus propiedades. | Tras pagar o recargar hay que llamar a `actualizarSaldo()`. Si se omite, el saldo visible queda desactualizado. | Juan Carlos Polanía |
| Strategy | `ReembolsoCien`, `ReembolsoOchenta`, `SinReembolso`, `ServicioCompra`, `IPoliticaReembolso` | Delega el cálculo del reembolso a una política de 100 %, 80 % o 0 %, según las horas restantes. | Cambiar los rangos exige editar el selector en `ServicioCompra`. `toHours()` trunca las fracciones de hora. | Andrés David Santafé y Juan Diego Quitián |
| Command | No lo usamos | | | |
| State | Se considera su implementación; no se ha realizado | Controlaría cuándo se puede cancelar una compra y evitaría reembolsarla dos veces. | Requeriría mantener una clase por estado y probar que la compra pase a cancelada solo cuando el reembolso se complete. | |
| Mediator | No lo usamos | | | |

## Parte B · La traza de una compra

| Nº | Clase | Qué hace en la compra | Patrón |
|---|---|---|---|
| 1 | `CarteleraController` · `Funcion` | Recibe la selección de una función en la cartelera y pide a la fachada el precio de la entrada. | |
| 2 | `CinemaFacade` · `ServicioCompra` · `EntradaBase` · `LenteTresDDecorator` · `PantallaIMAXDecorator` | Para mostrar el precio, la fachada pide a `ServicioCompra` una entrada base y este le agrega los recargos del formato. | Facade en `CinemaFacade`; Decorator en las entradas |
| 3 | `CarritoCompra` · `AsientosController` · `Sala` · `Asiento` | Guarda la función en el carrito y abre la vista de asientos. Esta deshabilita los ocupados y guarda los códigos elegidos. | Asientos creados con Prototype |
| 4 | `ConfiteriaController` · `CarritoCompra` · `ElementoProducto` · `Producto` · `Combo` | Agrega o retira productos y combos del carrito. Usa `calcularPrecio()` en ambos casos y consulta el total en la fachada. | Composite en `Producto` y `Combo` |
| 5 | `PagoController` · `CinemaFacade` | Muestra el resumen y el saldo estimado. Al pulsar Pagar, llama a `confirmarCompra()` con la función, los asientos y los extras. | Facade en `CinemaFacade` |
| 6 | `ServicioCompra` | Valida la edad y el horario. Recalcula el total, detecta asientos repetidos y comprueba que existan y estén libres. | |
| 7 | `MedioPago` · `AdaptadorTarjetaVirtual` · `TarjetaVirtual` | El adaptador verifica que la tarjeta esté activa y tenga saldo suficiente antes de generar la factura. | Adapter |
| 8 | `ConsecutivoFactura` · `AdaptadorTarjetaVirtual` · `TarjetaVirtual` · `MovimientoTarjeta` | Genera el consecutivo FAC. El adaptador cobra y la tarjeta registra el débito con la factura como referencia. | Singleton en `ConsecutivoFactura`; Adapter en `AdaptadorTarjetaVirtual` |
| 9 | `ServicioCompra` · `Sala` · `Asiento` · `MontoPuntos` · `Compra` · `CinemaFacade` | Ocupa los asientos tras el cobro, abona puntos y crea la compra. La fachada la añade al historial de la sesión. | Facade en `CinemaFacade` |
| 10 | `PagoController` · `PrincipalController` · `Alertas` | Vacía el carrito y refresca el saldo observable de la barra. Muestra la factura, el total y los asientos, y abre *Mis compras*. | Observer en el saldo de la barra |
| 11 | `ComprasController` | Muestra la compra confirmada en el historial con su factura, fecha, función, asientos, confitería y total. | |

## Parte C · Plan de reportes y gráficos

| Qué entregan | Evidencia |
|---|---|
| Clase que calculará las estadísticas | `EstadisticasCine`, en el modelo, recibe las compras de la sesión y las funciones programadas. Calcula: (1) entradas vendidas por película, sumando los asientos de cada `Compra`; (2) ingresos brutos en COP, sumando `Compra.getTotal()`, incluida la confitería; (3) ocupación por función, como asientos ocupados divididos por capacidad, usando `Sala`. |
| Interfaz y exportador del reporte (TXT o PDF) | `ExportadorReporte` define `exportar(ResumenEstadistico, Path)`. `ExportadorReporteTxt` escribe las entradas por película, los ingresos brutos y la ocupación por función. |
| Gráfico JavaFX que recibirá un Map | `ReportesController` recibe de `EstadisticasCine` un `Map<String, Integer>` con el título de cada película y la cantidad de entradas vendidas. Lo convierte en una serie de `BarChart<String, Number>`. Las barras permiten comparar los totales entre películas. |

## Qué cambió en el código después del taller

**Parte A.** Dos filas cambiaron con los commits del 14 y 15 de noviembre:

| Patrón | En el taller | Hoy |
|---|---|---|
| State | "Se considera su implementación; no se ha realizado" | Implementado: `EstadoCompra`, `CompraConfirmada`, `CompraCancelada`; `Compra` es el contexto |
| Observer | Listeners de JavaFX en `ContextoAplicacion` | Observer propio: `TarjetaVirtual` avisa a `ObservadorSaldo`; `ContextoAplicacion` lo implementa |
| Strategy | "`toHours()` trunca las fracciones de hora" | Corregido: el plazo se compara con fechas exactas (`isAfter(hora.plusHours(24))`) |

**Parte C.** El plan se implementó así:

| Plan del taller | Cómo quedó | Por qué |
|---|---|---|
| `EstadisticasCine` en el modelo | Igual: `model/EstadisticasCine`, sin JavaFX. Recibe compras, funciones y clientes, y devuelve un `ResumenEstadistico` con `entradasPorPelicula` (`Map<String, Integer>`), ingresos brutos y `ocupacionPorFuncion` | Los clientes se necesitan para sumar las recargas |
| `ExportadorReporte.exportar(ResumenEstadistico, Path)` | `ExportadorReporte.exportar(Reporte, Path)`, con `ExportadorReporteTxt` | El enunciado pide 7 reportes. `Reporte` sirve para todos, y el resumen estadístico es uno de ellos |
| `ReportesController` con un `BarChart` que recibe un `Map` | `ReportesAdminController`: barras (`Map<String, Integer>`), pastel y líneas. Cada gráfico recibe su `Map` y no calcula | El enunciado pide los tres tipos de gráfico |

El detalle está en [`Documento final CinemaUQ.md`](Documento%20final%20CinemaUQ.md).
