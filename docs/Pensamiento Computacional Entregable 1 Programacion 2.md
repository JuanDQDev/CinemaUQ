Juan Diego Quitian º Juan Carlos Polonia º Andrés David Santafé 

# **Pensamiento Computacional Entregable 1 Programación 2** 

# **Abstracción** 

# **¿Que se solicita finalmente?** 

El CinemaUQ necesita un sistema que le permita a sus clientes **consultar** películas, funciones, su tarjeta virtual y movimientos, **comprar** entradas, combos o productos, además de administrar sus puntos obtenidos. Sus administradores necesitan gestionar cada apartado del sistema como registrar películas, funciones, salas y **cancelar** funciones, emitir la factura luego de cada compra y generar reportes. 

Además de esto profundizaremos en dos actores principales: 

**Cliente:** Él puede consultar las películas y funciones, puede seleccionar asientos, compra sus entradas y productos, puede pagar con la tarjeta virtual y redimir puntos, además de consultar sus movimientos y solicitar recargas o cancelaciones. 

**Administrador:** Este puede gestionar tanto películas, salas, productos, promociones, combos y funciones, además de realizar recargar, generar reportes, estadísticas y cancelar funciones. 

# **¿Qué información es relevante?** 

**Cliente:** Entrada, datos de acceso (nombre, cedula, etc.…), tarjeta virtual, cuenta virtual de puntos e historial de compras. 

**Administrador:** Credenciales, permisos para administrar las operaciones. 

**Función:** Película, sala, fecha, hora, disponibilidad de asientos. 

**Películas:** Clasificación, límite de edad, genero. 

**Salas:** Formato de la sala, número de asientos 

**Reporte:** Formato visual o exportable 

**Tarjeta virtual:** Saldo, estado, movimientos de compras, recarga y reembolso. 

**Combos:** Agrupación de productos con sus descuentos (Si tienen). 

**Productos:** Nombre, precio, cantidad **Entrada:** Valor, función, sala, tipo de entrada (niño, adulto, mayor). **Factura:** Numero único de identificación de la compra 

**Cinema:** NIT, nombre comercial, ubicación, correo. 


# **¿Cómo se agrupa la información relevante?** 

|grupo|Clase|Atributos|
|---|---|---|
|Usuarios|Cliente|Nombre,edad,|
|pago|TarjetaVirtual, puntos||
|entrada|Función, Película, sala,<br>tipo de entrada||
|Facturas|Factura||
|Reporte|Reporteysus formatos||
|Negocio|Cinema|NIT, nombre comercial,<br>ubicación,correo|



# **¿Qué funcionalidades se solicitan finalmente?** 

# **Registro e inicio de sesión** 

RF: Registrar cliente RF: Iniciar sesión cliente RF: Iniciar sesión Administrador RF: Ingresar películas RF: Registrar productos e inventario RF: Registrar combos 

RF: Consultar películas disponibles RF: Consultar funciones de las películas RF: Seleccionar asientos disponibles 

RF: Seleccionar productos y combos 

RF: Pagar con tarjeta virtual RF: Redimir puntos obtenidos RF: Consultar de pagos RF: Consultar puntos obtenidos 

# **Administrador** 


RF: Consultar lista de clientes 

RF: Consultar compras RF: Cancelar funciones RF: Recargar tarjetas virtuales 

RF: Generar reportes 

|**Consultar**<br>**funciones**|Cada función estará asociada a una película,<br>una sala,una fechayuna hora.|RN: La función debe llevar<br>película,sala,fechayhora.|
|---|---|---|
|**Seleccionar**<br>**asientos**|Los asientos deberán mostrar su disponibilidad y<br>un mismo asiento no podrá venderse dos veces<br>para una misma función.<br>i|RN: Los asientos no deben ser<br>asignados a la misma persona<br>en una misma función.|
|**Validación**<br>**estado tarjeta**|El sistema deberá verificar que la tarjeta se<br>encuentre activa y tenga saldo suficiente.|RN: No se debe realizar<br>ninguna comprar si el saldo<br>de la tarjeta virtual no es<br>suficiente.<br>i|
|**Comprar**|El valor de la compra se descontará<br>automáticamente y deberá registrarse el<br>movimiento correspondiente.|RN: Cada compra confirmada<br>será descontada del saldo<br>virtual y se registrará en los<br>movimientos.|
|**Recarga tarjetas**|Únicamente el administrador podrá realizar la<br>recarga de la tarjeta.|RN: El administrador es el<br>único en aprobar las recargas<br>de los clientes.<br>i|
|**Consultar Tarjeta**|Cada recarga deberá quedar registrada dentro<br>de su historial de movimientos.|RN: Las recargas confirmadas<br>deberán ser registradas en los<br>movimientos de la tarjeta.|
|**Cancelar compra**|Las compras podrán cancelarse de acuerdo con<br>el tiempo restante para la función|RN: Las solicitudes de<br>cancelación se determinan<br>según la fecha de la función.|
|**Reembolso**|Todo reembolso deberá regresar a la tarjeta<br>virtual utilizada para la compra.|RN: Ningún reembolso se<br>realiza fuera del saldo virtual.|
|**Evitar reembolso**<br>**duplicado**|Una misma compra no puede devolverse dos<br>veces.|RN: Una compra se<br>reembolsa como máximo una<br>vez|
|**Vigencia de**<br>**puntos**|Los puntos tendrán una vigencia de un año<br>contado desde la fecha en que fueron obtenidos.|RN: Los puntos se pueden<br>redimir en un máximo de un<br>año|
|**Validación de**<br>**fechas puntos**|Los puntos obtenidos en diferentes fechas<br>deberán utilizarse primero aquellos que estén<br>próximos a vencer.|RN: Deben redimirse los<br>puntos próximos a vender|




|**Clasificación de**<br>**edad**|La venta de las entradas depende de la<br>clasificación de las películas.|RN: Se deben vender las<br>entradas dependiendo de la<br>clasificación de edad de la<br>película|
|---|---|---|
|**Promociones**|Una promoción debe estar disponible al<br>momento de la compra.<br>i|RN: La promoción solo aplica<br>si está activa y vigente en la<br>fecha de compra, máximo<br>unapromociónpor compra.|
|**Factura**|Cada compra confirmada tiene un consecutivo<br>único.|RN: No puede haber ninguna<br>factura repetido|
|**Puntos**|Los puntos generados por una compra deben de<br>ajustarse para beneficiar el cliente.|RN: El sistema deberá<br>regresar el 10% del total de la<br>compra en puntos para<br>redimir|



# **¿Cómo se agrupa la información relevante?** 

|**Modulo**|**Clases**|**Requisitos**|
|---|---|---|
|Registrar:<br>i|Cliente, TarjetaVirtual,<br>Administrador|RF-001/2/3|
|Confitería|Producto,Combo||
|Comprar|Compra, Entrada,<br>TarjetaVirtual,<br>Promoción||
|Emitir|Factura, Reembolso,<br>GenerarFactura||
|Administrar|Función, sala, película,<br>Administrador||
|Consultar|Película, Función,<br>TarjetaVirtual, Cliente,<br>Compra||
|Recargar|SolicitudRecarga,<br>TarjetaVirtual||
|Reportar|Reporte,TipoReporte||



# **¿Qué debo hacer para probar las funcionalidades?** 

|**Modulo**|**Regla**|**Caso**|**Resultado**|
|---|---|---|---|
|Funciones|RN|Crear función sin sala|Se rechaza la<br>creación y la<br>función no se<br>registra|
|Asientos|RN|El asiento D4 de la misma<br>sala en 2 entradas|error|




|Pago|RN|**Compra**=50.000 con<br>**saldo**=45.000<br>i|Pago<br>rechazado: el<br>saldo no se<br>modifica<br>i|
|---|---|---|---|
|Compra|RN|Compra confirmada de<br>$50.000 con saldo de<br>$60.000|Saldo final se<br>vuelve $10.000<br>y se registra un<br>movimiento|
|Cancelación|RN|El cliente cancela una<br>función|"Solo el<br>administrador<br>puede<br>cancelar<br>funciones"|
|Cancelación|RN|Función: 19:00<br>Cancelar: 20:00|"las<br>cancelaciones<br>son antes del<br>inicio de cada<br>función"|
|Reembolso|RN|Reembolso en efectivo|"los<br>reembolsos<br>son anexados<br>al saldo<br>virtual"|
|Puntos|RN|30/09/2026:300puntos<br>31/09/2027: redimir 300<br>puntos|error|
|Puntos<br>i|RN|Descuento de puntos<br>recientes|error|
|Clasificación<br>edad|RN|Película +16<br>Edad 13|"debe ingresar<br>a películas de<br>acuerdo a la<br>clasificación"|
|Factura|RN|Consecutivo Factura: 01<br>Consecutivo Factura: 01|Error|
|Puntos|RN|Compra: 10.000<br>Puntos: 3.000|"los puntos<br>equivalen al<br>10% del total"|



# **¿Qué puedo reutilizar de la solución?** 

|**Frase del**|**Problema**|**Patrón**|**Clases que ya**|**Clases que**|
|---|---|---|---|---|
|**enunciado**|||**existían**|**nacen aquí**|
|Deducción|Debe existir<br>solo un|Singleton|ConsecutivoFactura|-|




||único<br>consecutivo<br>de Factura||||
|---|---|---|---|---|
|Deducción|Contiene<br>muchas<br>validaciones|Builder|Compra|Compra.Builder|
|Deducción|El mismo<br>proceso que<br>se puede<br>copiar|Prototype|Sala|Clonable|



