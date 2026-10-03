Juan Diego Quitián · Juan Carlos Polanía · Andrés David Santafé

**Pensamiento Computacional Entregable 1 Programación 2**

**Abstracción**

**¿Que se solicita finalmente?**

El CinemaUQ necesita un sistema que le permita a sus clientes **consultar** películas, funciones, su tarjeta virtual y movimientos, **comprar** entradas, combos o productos, además de administrar sus puntos obtenidos. Sus administradores necesitan gestionar cada apartado del sistema como registrar películas, funciones, salas y **cancelar** funciones, emitir la factura luego de cada compra y generar reportes.

Además de esto profundizaremos en dos actores principales:

**Cliente:** Él puede consultar las películas y funciones, puede seleccionar asientos, compra sus entradas y productos, puede pagar con la tarjeta virtual y redimir puntos, además de consultar sus movimientos y solicitar recargas o cancelaciones.

**Administrador:** Este puede gestionar tanto películas, salas, productos, promociones, combos y funciones, además de realizar recargar, generar reportes, estadísticas y cancelar funciones.

**¿Qué información es relevante?**

**Cliente:** Entrada, datos de acceso (nombre, cedula, etc.…), tarjeta virtual, cuenta virtual de puntos e historial de compras.

**Administrador:** Credenciales, permisos para administrar las operaciones.

**Función:** Película, sala, fecha, hora, disponibilidad de asientos.

**Películas:** Clasificación, límite de edad, genero.

**Salas:** Formato de la sala, número de asientos

**Reporte:** Formato visual o exportable

**Tarjeta virtual:** Saldo, estado, movimientos de compras, recarga y reembolso.

**Combos:** Agrupación de productos con sus descuentos (Si tienen).

**Productos:** Nombre, precio, cantidad

**Entrada:** Valor, función, sala, tipo de entrada (niño, adulto, mayor).

**Factura:** Numero único de identificación de la compra

**Cinema:** NIT, nombre comercial, ubicación, correo.

**¿Cómo se agrupa la información relevante?**

| grupo | Clase | Atributos |
| :---- | :---- | :---- |
| Usuarios | Cliente | Nombre, edad,  |
| pago | TarjetaVirtual, puntos |  |
| entrada | Función, Película, sala, tipo de entrada |  |
| Facturas | Factura |  |
| Reporte | Reporte y sus formatos |  |
| Negocio | Cinema | NIT, nombre comercial, ubicación, correo |

**¿Qué funcionalidades se solicitan finalmente?**

**Registro e inicio de sesión**

RF: Registrar cliente

RF: Iniciar sesión cliente

RF: Iniciar sesión Administrador

RF: Ingresar películas

RF: Registrar productos e inventario

RF: Registrar combos

RF: Consultar películas disponibles

RF: Consultar funciones de las películas

RF: Seleccionar asientos disponibles

RF: Seleccionar productos y combos 

RF: Pagar con tarjeta virtual 

RF: Redimir puntos obtenidos

RF: Consultar de pagos

RF: Consultar puntos obtenidos

**Administrador** 

RF: Consultar lista de clientes

RF: Consultar compras

RF: Cancelar funciones

RF: Recargar tarjetas virtuales

RF: Generar reportes 

| Consultar funciones | Cada función estará asociada a una película, una sala, una fecha y una hora. | RN: La función debe llevar película, sala, fecha y hora. |
| :---- | :---- | :---- |
| **Seleccionar asientos**  | Los asientos deberán mostrar su disponibilidad y un mismo asiento no podrá venderse dos veces para una misma función. | RN: Los asientos no deben ser asignados a la misma persona en una misma función. |
| **Validación estado tarjeta** | El sistema deberá verificar que la tarjeta se encuentre activa y tenga saldo suficiente. | RN: No se debe realizar ninguna comprar si el saldo de la tarjeta virtual no es suficiente. |
| **Comprar** | El valor de la compra se descontará automáticamente y deberá registrarse el movimiento correspondiente. | RN: Cada compra confirmada será descontada del saldo virtual y se registrará en los movimientos. |
| **Recarga tarjetas** | Únicamente el administrador podrá realizar la recarga de la tarjeta. | RN: El administrador es el único en aprobar las recargas de los clientes. |
| **Consultar Tarjeta** | Cada recarga deberá quedar registrada dentro de su historial de movimientos. | RN: Las recargas confirmadas deberán ser registradas en los movimientos de la tarjeta. |
| **Cancelar compra** | Las compras podrán cancelarse de acuerdo con el tiempo restante para la función | RN: Las solicitudes de cancelación se determinan según la fecha de la función. |
| **Reembolso** | Todo reembolso deberá regresar a la tarjeta virtual utilizada para la compra. | RN: Ningún reembolso se realiza fuera del saldo virtual. |
| **Evitar reembolso duplicado** | Una misma compra no puede devolverse dos veces. | RN: Una compra se reembolsa como máximo una vez |
| **Vigencia de puntos** | Los puntos tendrán una vigencia de un año contado desde la fecha en que fueron obtenidos. | RN: Los puntos se pueden redimir en un máximo de un año |
| **Validación de fechas puntos** | Los puntos obtenidos en diferentes fechas deberán utilizarse primero aquellos que estén próximos a vencer. | RN: Deben redimirse los puntos próximos a vender |
| **Clasificación de edad** | La venta de las entradas depende de la clasificación de las películas. | RN: Se deben vender las entradas dependiendo de la clasificación de edad de la película |
| **Promociones** | Una promoción debe estar disponible al momento de la compra. | RN: La promoción solo aplica si está activa y vigente en la fecha de compra, máximo una promoción por compra. |
| **Factura** | Cada compra confirmada tiene un consecutivo único. | RN: No puede haber ninguna factura repetido |
| **Puntos** | Los puntos generados por una compra deben de ajustarse para beneficiar el cliente. | RN: El sistema deberá regresar el 10% del total de la compra en puntos para redimir |

**¿Cómo se agrupa la información relevante?**

| Modulo | Clases | Requisitos |
| :---- | :---- | :---- |
| Registrar: | Cliente, TarjetaVirtual, Administrador | RF-001/2/3 |
| Confitería | Producto, Combo |  |
| Comprar | Compra, Entrada, TarjetaVirtual, Promoción |  |
| Emitir | Factura, Reembolso, GenerarFactura |  |
| Administrar | Función, sala, película, Administrador |  |
| Consultar | Película, Función, TarjetaVirtual, Cliente, Compra |  |
| Recargar | SolicitudRecarga, TarjetaVirtual |  |
| Reportar | Reporte, TipoReporte |  |

**¿Qué debo hacer para probar las funcionalidades?**

| Modulo | Regla | Caso | Resultado |
| :---- | :---- | :---- | :---- |
| Funciones | RN | Crear función sin sala | Se rechaza la creación y la función no se registra  |
| Asientos | RN | El asiento D4 de la misma sala en 2 entradas | error |
| Pago | RN | **Compra**\=50.000 con **saldo**\=45.000 | Pago rechazado: el saldo no se modifica |
| Compra | RN | Compra confirmada de \$50.000 con saldo de \$60.000 | Saldo final se vuelve \$10.000 y se registra un movimiento |
| Cancelación | RN | El cliente cancela una función | "Solo el administrador puede cancelar funciones" |
| Cancelación | RN | Función: 19:00 Cancelar: 20:00 | "las cancelaciones son antes del inicio de cada función" |
| Reembolso | RN | Reembolso en efectivo | "los reembolsos son anexados al saldo virtual" |
| Puntos | RN | 30/09/2026:300puntos 31/09/2027: redimir 300 puntos  | error |
| Puntos | RN | Descuento de puntos recientes | error |
| Clasificación edad | RN | Película \+16 Edad 13 | "debe ingresar a películas de acuerdo a la clasificación" |
| Factura | RN | Consecutivo Factura: 01 Consecutivo Factura: 01  | Error |
| Puntos | RN | Compra: 10.000 Puntos: 3.000 | "los puntos equivalen al 10% del total" |

**¿Qué puedo reutilizar de la solución?**

| Frase del enunciado | Problema | Patrón | Clases que ya existían | Clases que nacen aquí |
| :---- | :---- | :---- | :---- | :---- |
| Deducción | Debe existir solo un único consecutivo de Factura  | Singleton | ConsecutivoFactura | \- |
| Deducción  | Contiene muchas validaciones  | Builder | Compra | Compra.Builder |
| Deducción  | El mismo proceso que se puede copiar | Prototype | Sala | Clonable |

# Diagrama de clases UML

![](/src/main/resources/images/UML.jpg)


