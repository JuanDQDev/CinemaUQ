package com.example.cinemauq.service;

import com.example.cinemauq.model.Asiento;
import com.example.cinemauq.model.Cliente;
import com.example.cinemauq.model.Compra;
import com.example.cinemauq.model.ElementoProducto;
import com.example.cinemauq.model.Funcion;
import com.example.cinemauq.model.MovimientoTarjeta;
import com.example.cinemauq.model.Pelicula;
import com.example.cinemauq.model.RestriccionPelicula;
import com.example.cinemauq.model.Sala;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Datos iniciales de la aplicación (enunciado: "datos iniciales suficientes").
 *
 * Sobre el catálogo base de SistemaCinema agrega películas, una sala, clientes de
 * prueba y la operación del último mes: compras con fecha real, recargas, compras
 * pagadas con puntos, cancelaciones del cliente (100 % y 80 %), una función
 * cancelada por el cine y solicitudes de recarga. También registra compras de hace
 * más de un año, para que se vean puntos vencidos.
 *
 * Todo pasa por los mismos servicios que usa la aplicación, así que respeta las
 * reglas de negocio. Las compras se generan con una semilla fija: siempre salen iguales.
 *
 * USUARIOS DE PRUEBA
 *   Administrador:  A01 (o "admin")  /  Admin123
 *   Clientes:       1001 / Demo123      laura / Laura123     mateo / Mateo123
 *                   sara / Sara1234     valentina / Valentina123
 *                   camilo / Camilo123  daniela / Daniela123  gloria / Gloria123
 */
final class DatosDemostracion {
    static final String ADMIN_ID = "A01";
    static final String ADMIN_CLAVE = "Admin123";
    private static final long SEMILLA = 2026L;
    private static final int SALDO_DEMOSTRACION = 200000;

    private DatosDemostracion() {
    }

    static void cargar(SistemaCinema sistema, LocalDateTime ahora) {
        Random azar = new Random(SEMILLA);
        LocalDateTime hoy = ahora.toLocalDate().atStartOfDay();
        ServicioCompra servicio = sistema.getServicio();

        // ---------- Clientes de prueba (registrados hace 14 meses) ----------
        LocalDateTime registro = hoy.minusMonths(14).withHour(9);
        List<Cliente> clientes = new ArrayList<>();
        clientes.add(sistema.registrarConFecha("1001", "Cliente de demostración", 22, "Demo123", registro));
        clientes.add(sistema.registrarConFecha("laura", "Laura Gómez", 28, "Laura123", registro.plusDays(1)));
        clientes.add(sistema.registrarConFecha("mateo", "Mateo Ríos", 16, "Mateo123", registro.plusDays(2)));
        clientes.add(sistema.registrarConFecha("sara", "Sara Cardona", 34, "Sara1234", registro.plusDays(3)));
        clientes.add(sistema.registrarConFecha("valentina", "Valentina Ospina", 19, "Valentina123", registro.plusDays(4)));
        clientes.add(sistema.registrarConFecha("camilo", "Camilo Herrera", 41, "Camilo123", registro.plusDays(5)));
        clientes.add(sistema.registrarConFecha("daniela", "Daniela Pérez", 25, "Daniela123", registro.plusDays(6)));
        clientes.add(sistema.registrarConFecha("gloria", "Gloria Marín", 67, "Gloria123", registro.plusDays(7)));
        Cliente demo = clientes.get(0), laura = clientes.get(1), mateo = clientes.get(2), sara = clientes.get(3);

        // A partir de aquí actúa el administrador: catálogo, funciones y recargas.
        sistema.iniciarSesion(ADMIN_ID, ADMIN_CLAVE);

        // ---------- Catálogo: 3 películas más (las de los pósters del enunciado) y otra sala ----------
        sistema.guardarPelicula(null, "Beyond Horizons", "Una exploradora cruza un portal luminoso al borde del sistema solar y descubre que el viaje de regreso depende de lo que deje atrás.", RestriccionPelicula.SIETE);
        sistema.guardarPelicula(null, "Echoes", "Thriller psicológico: una ingeniera de sonido empieza a escuchar en sus grabaciones voces de una noche que nadie recuerda.", RestriccionPelicula.DIEZ_Y_OCHO);
        sistema.guardarPelicula(null, "Future We Build", "En una ciudad que se reconstruye sola, un grupo de jóvenes decide quién tendrá derecho a vivir en ella.", RestriccionPelicula.DOCE);
        sistema.guardarSala(null, 4, "2D", 5, 8);
        sistema.guardarProducto(null, "Agua", 4000);
        List<Pelicula> peliculas = sistema.getPeliculas();
        List<Sala> salas = sistema.getSalas();
        List<ElementoProducto> confiteria = sistema.getConfiteria();

        // Promoción de ejemplo, inactiva: el administrador la puede activar en la demostración.
        LocalDate dia = ahora.toLocalDate();
        sistema.guardarPromocion(null, "Semana del cine", 20, dia, dia.plusDays(7), false);

        // ---------- Funciones ----------
        // En cartelera: F-001 a F-004 (catálogo base, sin ventas) y tres estrenos con ventas.
        LocalDateTime base = ahora.plusDays(2).withHour(18).withMinute(0).withSecond(0).withNano(0);
        Funcion estreno1 = sistema.guardarFuncion(null, peliculas.get(3), salas.get(3), base.plusHours(1), "Doblada");
        Funcion estreno2 = sistema.guardarFuncion(null, peliculas.get(4), salas.get(3), base.plusDays(1).plusHours(4), "Subtitulada");
        Funcion paraCancelar = sistema.guardarFuncion(null, peliculas.get(5), salas.get(1), base.plusDays(2), "Doblada");
        // Función de mañana: cancelar una compra aquí devuelve el 80 % (faltan entre 2 y 24 horas).
        Funcion cercana = sistema.guardarFuncion(null, peliculas.get(0), salas.get(3), ahora.plusHours(20), "Subtitulada");

        // Historial: una función cada dos días durante el último mes.
        String[] idiomas = {"Doblada", "Subtitulada", "Español"};
        int[] horas = {15, 18, 20, 21};
        List<Funcion> pasadas = new ArrayList<>();
        for (int atras = 29, i = 0; atras >= 1; atras -= 2, i++) {
            pasadas.add(sistema.agregarFuncionHistorica(peliculas.get(i % peliculas.size()),
                    salas.get((i + atras) % salas.size()),
                    hoy.minusDays(atras).withHour(horas[i % horas.length]).withMinute(i % 2 == 0 ? 0 : 30),
                    idiomas[i % idiomas.length]));
        }
        // Funciones de hace un año: sus puntos ya vencieron o están por vencer.
        Funcion antigua1 = sistema.agregarFuncionHistorica(peliculas.get(0), salas.get(0), hoy.minusMonths(13).withHour(19), "Doblada");
        Funcion antigua2 = sistema.agregarFuncionHistorica(peliculas.get(2), salas.get(1), hoy.minusMonths(11).withHour(20), "Subtitulada");

        // ---------- Recargas iniciales (solo el administrador recarga) ----------
        for (int i = 0; i < clientes.size(); i++) {
            sistema.recargarConFecha(clientes.get(i), 250000, registro.plusDays(10 + i));
        }

        // ---------- Compras planeadas, en orden cronológico ----------
        List<Funcion> funcionesPlan = new ArrayList<>();
        List<Cliente> clientesPlan = new ArrayList<>();
        List<LocalDateTime> fechasPlan = new ArrayList<>();
        planear(funcionesPlan, clientesPlan, fechasPlan, antigua1, laura, antigua1.getFechaHoraFuncion().minusDays(2));
        planear(funcionesPlan, clientesPlan, fechasPlan, antigua1, clientes.get(5), antigua1.getFechaHoraFuncion().minusDays(1));
        planear(funcionesPlan, clientesPlan, fechasPlan, antigua2, sara, antigua2.getFechaHoraFuncion().minusDays(3));
        planear(funcionesPlan, clientesPlan, fechasPlan, antigua2, demo, antigua2.getFechaHoraFuncion().minusDays(1));
        for (Funcion funcion : pasadas) {
            int cantidad = 3 + azar.nextInt(3);
            for (int c = 0; c < cantidad; c++) {
                planear(funcionesPlan, clientesPlan, fechasPlan, funcion, clientes.get(azar.nextInt(clientes.size())),
                        funcion.getFechaHoraFuncion().minusDays(1 + azar.nextInt(5)).minusHours(azar.nextInt(6)));
            }
        }
        Funcion[] conVentas = {estreno1, estreno2, paraCancelar, cercana};
        for (Funcion funcion : conVentas) {
            for (int c = 0; c < 3; c++) {
                planear(funcionesPlan, clientesPlan, fechasPlan, funcion, clientes.get(azar.nextInt(clientes.size())),
                        ahora.minusHours(30 - c * 9L).minusMinutes(azar.nextInt(50)));
            }
        }
        ordenarPorFecha(funcionesPlan, clientesPlan, fechasPlan);

        List<Compra> recientes = new ArrayList<>();
        for (int i = 0; i < funcionesPlan.size(); i++) {
            Funcion funcion = funcionesPlan.get(i);
            LocalDateTime fecha = fechasPlan.get(i);
            Cliente comprador = compradorPermitido(clientesPlan.get(i), funcion, clientes);
            List<String> asientos = asientosLibres(funcion, 1 + azar.nextInt(4), azar);
            if (asientos.isEmpty()) continue;
            List<ElementoProducto> productos = new ArrayList<>();
            int extras = azar.nextInt(3);
            for (int p = 0; p < extras; p++) productos.add(confiteria.get(azar.nextInt(confiteria.size())));

            // Una de cada cuatro compras paga una entrada con puntos, si el cliente tiene suficientes.
            int entradasConPuntos = 0;
            if (azar.nextInt(4) == 0 && servicio.calcularValorPuntos(funcion, 1, new ArrayList<>())
                    <= comprador.getMontoPuntos().getPuntosRedimibles(fecha)) {
                entradasConPuntos = 1;
            }
            int porPagar = servicio.calcularTotal(funcion, asientos, productos)
                    - servicio.calcularValorPuntos(funcion, entradasConPuntos, new ArrayList<>());
            if (comprador.getTarjeta().getSaldo() < porPagar) {
                sistema.recargarConFecha(comprador, 150000 + porPagar, antesDe(fecha, comprador));
            }
            Compra compra = servicio.comprar(comprador, funcion, asientos, productos,
                    new AdaptadorTarjetaVirtual(comprador.getTarjeta(), fecha), entradasConPuntos, new ArrayList<>(), fecha);
            sistema.agregarCompra(compra);
            if (funcion == estreno1 || funcion == estreno2 || funcion == cercana) recientes.add(compra);
        }

        // ---------- Cancelaciones ----------
        // Del cliente: más de 24 horas antes devuelve el 100 %; entre 2 y 24 horas, el 80 %.
        boolean total = false, parcial = false;
        for (Compra compra : recientes) {
            if (compra.getTotal() == 0) continue;
            if (!parcial && compra.getFuncion() == cercana) {
                servicio.cancelarCompra(compra, ahora.minusMinutes(20));
                parcial = true;
            } else if (!total && compra.getFuncion() != cercana) {
                servicio.cancelarCompra(compra, ahora.minusMinutes(40));
                total = true;
            }
        }
        // Del cine: se cancela la función y cada comprador recibe el 100 %.
        sistema.cancelarFuncion(paraCancelar);

        // ---------- Solicitudes de recarga ----------
        sistema.solicitarRecarga(sara, 80000);
        sistema.resolverSolicitud(sistema.getSolicitudes().get(0), true);
        sistema.solicitarRecarga(laura, 500000);
        sistema.resolverSolicitud(sistema.getSolicitudes().get(1), false);
        sistema.solicitarRecarga(mateo, 40000); // queda pendiente para la demostración

        // El cliente de demostración arranca con saldo suficiente para comprar.
        if (demo.getTarjeta().getSaldo() < SALDO_DEMOSTRACION) {
            sistema.recargarCliente(demo, SALDO_DEMOSTRACION - demo.getTarjeta().getSaldo());
        }
        sistema.cerrarSesion();
    }

    private static void planear(List<Funcion> funciones, List<Cliente> clientes, List<LocalDateTime> fechas,
                                Funcion funcion, Cliente cliente, LocalDateTime fecha) {
        funciones.add(funcion);
        clientes.add(cliente);
        fechas.add(fecha);
    }

    /** Ordena las tres listas por fecha (inserción), para que los saldos queden en orden. */
    private static void ordenarPorFecha(List<Funcion> funciones, List<Cliente> clientes, List<LocalDateTime> fechas) {
        for (int i = 1; i < fechas.size(); i++) {
            for (int j = i; j > 0 && fechas.get(j).isBefore(fechas.get(j - 1)); j--) {
                intercambiar(funciones, j);
                intercambiar(clientes, j);
                intercambiar(fechas, j);
            }
        }
    }

    private static <T> void intercambiar(List<T> lista, int j) {
        T temporal = lista.get(j);
        lista.set(j, lista.get(j - 1));
        lista.set(j - 1, temporal);
    }

    /** Si el cliente elegido no tiene la edad mínima, compra el primero que sí la tenga. */
    private static Cliente compradorPermitido(Cliente elegido, Funcion funcion, List<Cliente> clientes) {
        RestriccionPelicula restriccion = funcion.getPeliculaFuncion().getRestriccionPelicula();
        if (restriccion == null || elegido.getEdad() >= restriccion.getEdadMinima()) return elegido;
        for (Cliente cliente : clientes) {
            if (cliente.getEdad() >= restriccion.getEdadMinima()) return cliente;
        }
        return elegido;
    }

    /** Hasta {@code cantidad} asientos libres seguidos, empezando en uno al azar. */
    private static List<String> asientosLibres(Funcion funcion, int cantidad, Random azar) {
        List<Asiento> asientos = funcion.getSalaFuncion().getAsientos();
        List<String> elegidos = new ArrayList<>();
        int inicio = azar.nextInt(asientos.size());
        for (int i = 0; i < asientos.size() && elegidos.size() < cantidad; i++) {
            Asiento asiento = asientos.get((inicio + i) % asientos.size());
            if (!asiento.isOcupado()) elegidos.add(asiento.getCodigo());
        }
        return elegidos;
    }

    /** 30 minutos antes de la compra, pero nunca antes del último movimiento de la tarjeta. */
    private static LocalDateTime antesDe(LocalDateTime fechaCompra, Cliente cliente) {
        LocalDateTime fecha = fechaCompra.minusMinutes(30);
        List<MovimientoTarjeta> movimientos = cliente.getTarjeta().getMovimientos();
        LocalDateTime ultimo = movimientos.get(movimientos.size() - 1).getFechaReal();
        return fecha.isBefore(ultimo) ? ultimo : fecha;
    }
}
