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

## Estructura

```
src/main/java/com/example/cinemauq/
├── model/      clases del dominio y patrones creacionales
└── ...         aplicación JavaFX (HelloApplication, Launcher)
src/test/java/com/example/cinemauq/model/
├── ConsecutivoFacturaTest   (Singleton)
├── FuncionBuilderTest       (Builder)
└── SalaPrototypeTest        (Prototype)
docs/           análisis, diagrama y fichas de patrones
```

## Cómo ejecutar
- **Aplicación:** desde IntelliJ, ejecutar `Launcher`, o con `mvnw javafx:run`.
- **Pruebas:** en IntelliJ, clic derecho sobre `src/test/java` → *Run 'All Tests'*.
