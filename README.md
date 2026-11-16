# CinemaUQ

Sistema de gestión de cine en **Java + JavaFX** (Maven), proyecto final de Programación II · Universidad del Quindío · 2026-2.

**Integrantes:** Andrés David Santafé · Juan Diego Quitián · Juan Carlos Polanía

## Entrega 1

| Entregable | Ubicación |
|---|---|
| Documento de análisis: actores, RF-001…RF-025, reglas de negocio RN-001…RN-015, casos de uso, casos de prueba y SOLID | [`docs/Pensamiento Computacional Entregable 1 Programacion 2.md`](docs/Pensamiento%20Computacional%20Entregable%201%20Programacion%202.md) |
| Diagrama de clases | `docs/Diagrama de clases Entrega 1.drawio` (y su versión en PDF) |
| Patrón Singleton | [`docs/Patron Singleton.md`](docs/Patron%20Singleton.md) · `ConsecutivoFactura` |
| Patrón Builder | [`docs/Patron Builder.md`](docs/Patron%20Builder.md) · `Funcion.Builder` |
| Patrón Prototype | [`docs/Patron Prototype.md`](docs/Patron%20Prototype.md) · `Clonable`, `Sala`, `Asiento` |

## Entrega 2

| Entregable | Ubicación |
|---|---|
| Interfaz JavaFX con MVC (cartelera, asientos, confitería, compra y tarjeta virtual) | [`docs/Interfaz JavaFX MVC.md`](docs/Interfaz%20JavaFX%20MVC.md) · paquete `controller` y `resources/.../view` |
| Patrones estructurales | Composite (`ElementoProducto`, `Producto`, `Combo`), Decorator (`Entrada`, `EntradaDecorator`…), Adapter (`MedioPago`, `AdaptadorTarjetaVirtual`), Facade (`CinemaFacade`) |
| Excepciones propias del dominio | paquete `exception` |

## Entrega 3 (final)

| Entregable | Ubicación |
|---|---|
| **Documento final:** 10 patrones, SOLID con evidencia, política de reembolso, puntos y usuarios de prueba | [`PDF`](docs/Documento%20final%20CinemaUQ.pdf) · [`.md`](docs/Documento%20final%20CinemaUQ.md) |
| **Diagrama de clases (PDF)** | [`docs/Diagrama de clases CinemaUQ Entrega 3.pdf`](docs/Diagrama%20de%20clases%20CinemaUQ%20Entrega%203.pdf) · editable en `.drawio` |
| Patrones de comportamiento | Observer (`ObservadorSaldo`), Strategy (`IPoliticaReembolso`), State (`EstadoCompra`) |
| Puntos con vigencia y redención | `MontoPuntos`, `LotePuntos`, `RedencionPuntos` |
| Cancelación y reembolso | `ServicioCompra`, `Compra` y sus estados |
| Reportes exportables (.txt) | `ServicioReportes`, `ExportadorReporte`, `ExportadorReporteTxt` · ejemplos en [`docs/reportes-ejemplo`](docs/reportes-ejemplo) |
| Reportes visuales (barras, pastel, líneas) y estadísticas | `EstadisticasCine`, `ReportesAdminController` |
| Datos iniciales | `DatosDemostracion` |
| Taller de integración de patrones | [`docs/Taller integracion de patrones.md`](docs/Taller%20integracion%20de%20patrones.md) |

### Usuarios de prueba

| Perfil | Identificación | Contraseña |
|---|---|---|
| Administrador | `A01` (o `admin`) | `Admin123` |
| Cliente de demostración | `1001` | `Demo123` |
| Otros clientes | `laura`, `mateo`, `sara` | `Laura123`, `Mateo123`, `Sara1234` |

La lista completa está en el documento final.

## Estructura

```
src/main/java/com/example/cinemauq/
├── CinemaUQApplication, Launcher   arranque de JavaFX
├── controller/   controladores de las vistas (MVC)
├── model/        dominio, patrones y EstadisticasCine
├── service/      SistemaCinema, CinemaFacade, ServicioCompra, ServicioReportes,
│                 exportadores, DatosDemostracion, MedioPago y su adaptador
└── exception/    excepciones del dominio
src/main/resources/com/example/cinemauq/
├── view/         vistas FXML (view/admin: panel del administrador)
└── css/          estilos
src/test/java/com/example/cinemauq/   53 pruebas JUnit
docs/             documento final, diagramas, fichas de patrones y capturas
```

## Cómo ejecutar
- **Aplicación:** desde IntelliJ, ejecutar `Launcher`, o con `mvnw javafx:run`.
- **Pruebas:** en IntelliJ, clic derecho sobre `src/test/java` → *Run 'All Tests'*.
