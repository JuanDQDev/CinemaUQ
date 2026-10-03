# Pensamiento Computacional — Entregable 1 · Programación 2

**CinemaUQ** · Universidad del Quindío · 2026-2

Juan Diego Quitián · Juan Carlos Polanía · Andrés David Santafé

**Contenido**

1. Abstracción
2. Actores
3. Información relevante
4. Requisitos funcionales (RF)
5. Reglas de negocio (RN)
6. Casos de uso principales
7. Agrupación en módulos
8. Casos de prueba
9. Patrones creacionales
10. Principios SOLID

---

## 1. Abstracción

### ¿Qué se solicita finalmente?

CinemaUQ necesita un sistema que permita a sus clientes **consultar** películas, funciones, su tarjeta virtual y sus movimientos; **comprar** entradas, combos o productos; y administrar los puntos obtenidos.

Sus administradores necesitan gestionar cada apartado del sistema:

- registrar películas, funciones y salas;
- **cancelar** funciones;
- emitir la factura después de cada compra;
- generar reportes.

## 2. Actores

| Actor | Descripción |
|---|---|
| **Cliente** | Consulta las películas y funciones, selecciona asientos y compra sus entradas y productos. Paga con la tarjeta virtual y redime puntos. Consulta sus movimientos y solicita recargas o cancelaciones. |
| **Administrador** | Gestiona películas, salas, productos, promociones, combos y funciones. Realiza recargas, genera reportes y estadísticas, y cancela funciones. |

## 3. Información relevante

| Elemento | Información |
|---|---|
| Cliente | Entrada, datos de acceso (nombre, cédula, etc.), tarjeta virtual, cuenta virtual de puntos e historial de compras |
| Administrador | Credenciales y permisos para administrar las operaciones |
| Función | Película, sala, fecha, hora y disponibilidad de asientos |
| Película | Clasificación, límite de edad y género |
| Sala | Formato de la sala y número de asientos |
| Reporte | Formato visual o exportable |
| Tarjeta virtual | Saldo, estado y movimientos de compras, recargas y reembolsos |
| Combo | Agrupación de productos con sus descuentos (si los tienen) |
| Producto | Nombre, precio y cantidad |
| Entrada | Valor, función, sala y tipo de entrada (niño, adulto, mayor) |
| Factura | Número único de identificación de la compra |
| Cinema | NIT, nombre comercial, ubicación y correo |

### ¿Cómo se agrupa la información relevante?

| Grupo | Clase | Atributos |
|---|---|---|
| Usuarios | Cliente, Administrador | Nombre, identificación, edad |
| Pago | TarjetaVirtual, puntos | Saldo, estado |
| Entrada | Función, Película, Sala, tipo de entrada | Fecha y hora, idioma, clasificación |
| Facturas | Factura, ConsecutivoFactura | Consecutivo |
| Reporte | Reporte y sus formatos | Tipo, formato |
| Negocio | Cinema | NIT, nombre comercial, ubicación, correo |

## 4. Requisitos funcionales (RF)

| Código | Requisito | Actor |
|---|---|---|
| RF-001 | Registrar cliente | Cliente |
| RF-002 | Iniciar sesión como cliente | Cliente |
| RF-003 | Iniciar sesión como administrador | Administrador |
| RF-004 | Ingresar películas | Administrador |
| RF-005 | Registrar productos e inventario | Administrador |
| RF-006 | Registrar combos | Administrador |
| RF-007 | Consultar películas disponibles | Cliente |
| RF-008 | Consultar funciones de las películas | Cliente |
| RF-009 | Seleccionar asientos disponibles | Cliente |
| RF-010 | Seleccionar productos y combos | Cliente |
| RF-011 | Pagar con tarjeta virtual | Cliente |
| RF-012 | Redimir puntos obtenidos | Cliente |
| RF-013 | Consultar pagos | Cliente |
| RF-014 | Consultar puntos obtenidos | Cliente |
| RF-015 | Consultar lista de clientes | Administrador |
| RF-016 | Consultar compras | Administrador |
| RF-017 | Cancelar funciones | Administrador |
| RF-018 | Recargar tarjetas virtuales | Administrador |
| RF-019 | Generar reportes | Administrador |
| RF-020 | Solicitar la cancelación de una compra | Cliente |
| RF-021 | Consultar saldo y movimientos de la tarjeta virtual | Cliente |
| RF-022 | Consultar historial de compras | Cliente |
| RF-023 | Gestionar salas y funciones | Administrador |
| RF-024 | Gestionar promociones | Administrador |
| RF-025 | Emitir la factura de cada compra confirmada | Sistema |

RF-020 a RF-025 se agregaron para cubrir funciones que el enunciado y las reglas de negocio ya mencionan: cancelar compras, consultar la tarjeta, gestionar salas, funciones y promociones, y emitir la factura.

## 5. Reglas de negocio (RN)

Origen: **E** = regla del enunciado · **A** = regla adicional definida por el grupo.

| Código | Requisito asociado | Descripción | Regla | Origen |
|-------|---------|------------------|------------------|-----|
| RN-001 | Consultar funciones (RF-008, RF-023) | Cada función está asociada a una película, una sala, una fecha y una hora. | La función debe llevar película, sala, fecha y hora. | E |
| RN-002 | Seleccionar asientos (RF-009) | Los asientos deben mostrar su disponibilidad, y un mismo asiento no puede venderse dos veces para una misma función. | Un asiento no puede asignarse dos veces en una misma función. | E |
| RN-003 | Validación del estado de la tarjeta (RF-011) | El sistema verifica que la tarjeta esté activa y tenga saldo suficiente. | No se realiza ninguna compra si la tarjeta no está activa o su saldo no es suficiente. | E |
| RN-004 | Comprar (RF-011) | El valor de la compra se descuenta automáticamente y se registra el movimiento correspondiente. | Cada compra confirmada se descuenta del saldo virtual y se registra en los movimientos. | E |
| RN-005 | Recargar tarjetas (RF-018) | Únicamente el administrador puede recargar la tarjeta. | El administrador es el único que aprueba las recargas de los clientes. | E |
| RN-006 | Consultar tarjeta (RF-021) | Cada recarga queda registrada en el historial de movimientos. | Las recargas confirmadas se registran en los movimientos de la tarjeta. | E |
| RN-007 | Cancelar compra (RF-020) | Las compras pueden cancelarse según el tiempo que falte para la función. | La solicitud de cancelación se resuelve según la fecha y hora de la función. | E |
| RN-008 | Reembolso (RF-020) | Todo reembolso regresa a la tarjeta virtual usada en la compra. | Ningún reembolso se realiza fuera del saldo virtual. | E |
| RN-009 | Evitar reembolso duplicado (RF-020) | Una misma compra no puede devolverse dos veces. | Una compra se reembolsa como máximo una vez. | E |
| RN-010 | Vigencia de puntos (RF-012, RF-014) | Los puntos tienen una vigencia de un año desde la fecha en que se obtuvieron. | Los puntos se pueden redimir en un plazo máximo de un año. | E |
| RN-011 | Orden de redención de puntos (RF-012) | Si hay puntos obtenidos en diferentes fechas, se usan primero los más próximos a vencer. | Se redimen primero los puntos más próximos a vencer. | E |
| RN-012 | Clasificación de edad (RF-009, RF-011) | La venta de entradas depende de la clasificación de la película. | Solo se venden entradas según la clasificación de edad de la película. | A |
| RN-013 | Promociones (RF-024) | La promoción debe estar disponible al momento de la compra. | La promoción solo aplica si está activa y vigente en la fecha de compra; máximo una promoción por compra. | A |
| RN-014 | Factura (RF-025) | Cada compra confirmada tiene un consecutivo único. | No puede haber facturas con el mismo consecutivo. | A |
| RN-015 | Puntos (RF-011, RF-014) | Los puntos generados por una compra deben beneficiar al cliente. | El sistema otorga en puntos el 10 % del total de la compra. | A |

## 6. Casos de uso principales

### CU-01 · Registrarse e iniciar sesión
- **Actor:** Cliente (también el Administrador para iniciar sesión).
- **RF:** RF-001, RF-002, RF-003.
- **Flujo principal:**
  1. El cliente ingresa sus datos: nombre, identificación y edad.
  2. El sistema valida que no exista otro cliente con la misma identificación.
  3. El sistema crea la cuenta del cliente con su tarjeta virtual.
  4. El cliente inicia sesión con sus credenciales.
- **Excepciones:** identificación ya registrada; credenciales incorrectas.

### CU-02 · Comprar entradas y confitería
- **Actor:** Cliente.
- **RF:** RF-007 a RF-011 y RF-025. **RN:** RN-001, RN-002, RN-003, RN-004, RN-012, RN-013, RN-014 y RN-015.
- **Flujo principal:**
  1. El cliente consulta las películas y elige una función.
  2. El sistema muestra el mapa de asientos de esa función.
  3. El cliente selecciona sus asientos y, si quiere, productos o combos.
  4. El sistema valida la clasificación de edad y aplica la promoción vigente, si la hay.
  5. El cliente paga con su tarjeta virtual.
  6. El sistema valida el estado y el saldo de la tarjeta, descuenta el valor y registra el movimiento.
  7. El sistema marca los asientos como ocupados, emite la factura con un consecutivo único y acumula los puntos.
- **Excepciones:** asiento ya ocupado (RN-002); tarjeta inactiva o saldo insuficiente (RN-003); edad no permitida (RN-012).

### CU-03 · Cancelar una compra
- **Actor:** Cliente.
- **RF:** RF-020. **RN:** RN-007, RN-008 y RN-009.
- **Flujo principal:**
  1. El cliente selecciona una compra de su historial y solicita la cancelación.
  2. El sistema calcula el tiempo que falta para la función y el valor que corresponde reembolsar.
  3. El sistema reembolsa a la misma tarjeta virtual, registra el movimiento y libera los asientos.
- **Excepciones:** la función ya inició o está fuera del plazo permitido; la compra ya fue reembolsada.

### CU-04 · Recargar una tarjeta virtual
- **Actor:** Administrador.
- **RF:** RF-018. **RN:** RN-005 y RN-006.
- **Flujo principal:**
  1. El administrador busca al cliente.
  2. El administrador ingresa el valor de la recarga.
  3. El sistema suma el valor al saldo y registra el movimiento de recarga.
- **Excepciones:** valor inválido; quien intenta recargar no es administrador.

### CU-05 · Gestionar la cartelera
- **Actor:** Administrador.
- **RF:** RF-004, RF-023. **RN:** RN-001.
- **Flujo principal:**
  1. El administrador registra películas y salas.
  2. El administrador crea funciones con película, sala, fecha y hora.
  3. Cada función recibe una copia propia del mapa de asientos de la sala (patrón Prototype).
- **Excepciones:** falta algún dato obligatorio de la función.

### CU-06 · Cancelar una función
- **Actor:** Administrador.
- **RF:** RF-017. **RN:** RN-008.
- **Flujo principal:**
  1. El administrador selecciona la función y la cancela.
  2. El sistema reembolsa a cada cliente con compras de esa función, en su tarjeta virtual.
- **Excepciones:** quien intenta cancelar no es administrador.

### CU-07 · Generar reportes
- **Actor:** Administrador.
- **RF:** RF-019.
- **Flujo principal:**
  1. El administrador elige el tipo de reporte y el rango de fechas.
  2. El sistema genera el reporte en el formato seleccionado (visual o exportable).
- **Excepciones:** rango de fechas sin datos.

## 7. Agrupación en módulos

| Módulo | Clases | Requisitos |
|---|---|---|
| Registrar | Cliente, TarjetaVirtual, Administrador | RF-001, RF-002, RF-003 |
| Confitería | Producto, Combo | RF-005, RF-006, RF-010 |
| Comprar | Compra, Entrada, TarjetaVirtual, Promoción | RF-009, RF-010, RF-011, RF-012 |
| Emitir | Factura, ConsecutivoFactura, Reembolso | RF-020, RF-025 |
| Administrar | Funcion, Sala, Asiento, Pelicula, Administrador | RF-004, RF-017, RF-023, RF-024 |
| Consultar | Pelicula, Funcion, TarjetaVirtual, Cliente, Compra | RF-007, RF-008, RF-013, RF-014, RF-015, RF-016, RF-021, RF-022 |
| Recargar | SolicitudRecarga, TarjetaVirtual | RF-018 |
| Reportar | Reporte, TipoReporte | RF-019 |

## 8. Casos de prueba

| Código | Módulo | Regla | Caso | Resultado esperado |
|----|------|----|-------------|-------------|
| CP-01 | Funciones | RN-001 | Crear función sin sala | Se rechaza la creación y la función no se registra |
| CP-02 | Asientos | RN-002 | El asiento D4 de la misma función en 2 entradas | Error |
| CP-03 | Pago | RN-003 | Compra de $50.000 con saldo de $45.000 | Pago rechazado: el saldo no se modifica |
| CP-04 | Compra | RN-004 | Compra confirmada de $50.000 con saldo de $60.000 | El saldo final queda en $10.000 y se registra un movimiento |
| CP-05 | Cancelación | RF-017 | El cliente intenta cancelar una función | "Solo el administrador puede cancelar funciones" |
| CP-06 | Cancelación | RN-007 | Función a las 19:00 y cancelación a las 20:00 | "Las cancelaciones son antes del inicio de cada función" |
| CP-07 | Reembolso | RN-008 | Reembolso en efectivo | "Los reembolsos se abonan al saldo virtual" |
| CP-08 | Puntos | RN-010 | 300 puntos obtenidos el 30/09/2026 y redimidos el 01/10/2027 | Error: puntos vencidos |
| CP-09 | Puntos | RN-011 | Se intenta descontar primero los puntos más recientes | Error |
| CP-10 | Clasificación | RN-012 | Película +16 y cliente de 13 años | "Debe ingresar a películas de acuerdo con la clasificación" |
| CP-11 | Factura | RN-014 | Dos facturas con el consecutivo 01 | Error |
| CP-12 | Puntos | RN-015 | Compra de $10.000 que otorga 3.000 puntos | Error: los puntos equivalen al 10 % del total (1.000) |

Casos ya automatizados con JUnit en `src/test`:

- CP-01, en `FuncionBuilderTest`;
- CP-02, en `SalaPrototypeTest`;
- CP-11, en `ConsecutivoFacturaTest`.

## 9. Patrones creacionales

| Problema | Patrón | Clases | Ficha |
|------------------|-----|-------|--------|
| Debe existir un único generador del consecutivo de factura, para que no se repitan (RN-014) | Singleton | `ConsecutivoFactura` | `docs/Patron Singleton.md` |
| Una función reúne varios datos obligatorios que deben validarse antes de crearla, y debe quedar inmutable (RN-001) | Builder | `Funcion`, `Funcion.Builder` | `docs/Patron Builder.md` |
| Cada función necesita su propia copia de los asientos de la sala, con disponibilidad independiente (RN-002) | Prototype | `Clonable<T>`, `Sala`, `Asiento` | `docs/Patron Prototype.md` |

Cada ficha incluye los seis elementos solicitados: problema, patrón, justificación, clases involucradas, diagrama y evidencia.

## 10. Principios SOLID

| Principio | Evidencia en el código actual |
|--------|---------------------|
| **S** · Responsabilidad única | Cada clase tiene una sola razón para cambiar. `ConsecutivoFactura` solo genera consecutivos. `Funcion.Builder` solo arma y valida una `Funcion`. `Asiento` controla únicamente su propio estado (ocupar/liberar). `Sala` administra su colección de asientos. |
| **O** · Abierto/cerrado | Cualquier clase nueva que necesite copiarse implementa `Clonable<T>` sin modificar las existentes; así se agregaron `Sala` y `Asiento`. Los objetos `Funcion` no se modifican después de creados (atributos `final`): para otra configuración se construye una nueva con el Builder. |
| **L** · Sustitución de Liskov | Todo código que trabaje con un `Clonable<T>` funciona igual con una `Sala` o un `Asiento`: ambos cumplen el contrato de devolver una copia independiente de sí mismos. |
| **I** · Segregación de interfaces | `Clonable<T>` tiene un único método, `clonar()`. Las clases que la implementan no quedan obligadas a métodos que no usan. |
| **D** · Inversión de dependencias | El contrato del Prototype depende de la abstracción `Clonable<T>` y no de una clase concreta. En la siguiente entrega se extenderá a la capa de servicios: los controladores dependerán de interfaces de servicio y no de implementaciones. |
