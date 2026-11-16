package com.example.cinemauq.service;

import com.example.cinemauq.model.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Reportes del administrador. Arma cada reporte con los datos del sistema;
 * el formato del archivo lo decide un ExportadorReporte.
 */
public class ServicioReportes {
    
    public String generar(SistemaCinema sistema, LocalDate desde, LocalDate hasta) {
        sistema.exigirAdministrador();
        validarFechas(desde, hasta);
        List<Compra> compras = sistema.getTodasCompras();
        long ventas = 0, devoluciones = 0, recargas = 0;
        int cantidad = 0, cancelaciones = 0;
        StringBuilder detalle = new StringBuilder();
        for (Compra c : compras) {
            if (enRango(c.getFechaCompra().toLocalDate(), desde, hasta)) {
                ventas += c.getTotal(); cantidad++;
                detalle.append(c.getFactura()).append(" | ").append(c.getCliente().getId())
                        .append(" | ").append(c.getNombrePelicula()).append(" | ")
                        .append(c.getEstado()).append(" | pagado $").append(c.getTotal())
                        .append(" | devuelto acumulado $").append(c.getReembolso()).append("\n");
            }
            if (c.getFechaCancelacion() != null && enRango(c.getFechaCancelacion().toLocalDate(), desde, hasta)) {
                devoluciones += c.getReembolso(); cancelaciones++;
            }
        }
        for (Cliente cliente : sistema.getClientes()) {
            for (MovimientoTarjeta m : cliente.getTarjeta().getMovimientos()) {
                if (m.getTipo() == TipoMovimiento.RECARGA && enRango(m.getFechaReal().toLocalDate(), desde, hasta)) recargas += m.getMonto();
            }
        }
        StringBuilder texto = new StringBuilder("CINEMAUQ - REPORTE\n");
        texto.append(desde).append(" a ").append(hasta).append("\n\n")
                .append("Compras del periodo: ").append(cantidad).append("\n")
                .append("Ventas brutas: $").append(ventas).append("\n")
                .append("Reembolsos realizados en el periodo: $").append(devoluciones).append("\n")
                .append("Ventas menos reembolsos del periodo: $").append(ventas - devoluciones).append("\n")
                .append("Cancelaciones realizadas: ").append(cancelaciones).append("\n")
                .append("Recargas aprobadas (sin bonos): $").append(recargas).append("\n\nCOMPRAS\n")
                .append(detalle).append("\nFUNCIONES Y OCUPACION ACTUAL (fecha de función dentro del periodo)\n");
        for (Funcion f : sistema.getFuncionesAdministracion()) {
            if (enRango(f.getFechaHoraFuncion().toLocalDate(), desde, hasta)) {
                Sala sala = f.getSalaFuncion();
                texto.append(f).append(" | ocupados ").append(sala.getCapacidad() - sala.contarDisponibles())
                        .append("/").append(sala.getCapacidad()).append("\n");
            }
        }
        texto.append("\nPRODUCTOS Y COMBOS (unidades de compras del periodo que siguen confirmadas)\n");
        List<String> nombres = new ArrayList<>();
        for (Compra c : compras) if (!c.estaCancelada() && enRango(c.getFechaCompra().toLocalDate(), desde, hasta)) {
            for (ElementoProducto p : c.getConfiteria()) if (!nombres.contains(p.getNombre())) nombres.add(p.getNombre());
        }
        for (String nombre : nombres) {
            int unidades = 0;
            for (Compra c : compras) if (!c.estaCancelada() && enRango(c.getFechaCompra().toLocalDate(), desde, hasta)) {
                for (ElementoProducto p : c.getConfiteria()) if (nombre.equals(p.getNombre())) unidades++;
            }
            texto.append(nombre).append(": ").append(unidades).append("\n");
        }
        texto.append("\nCLIENTES - SALDOS Y PUNTOS VIGENTES ACTUALES\n");
        for (Cliente c : sistema.getClientes()) {
            texto.append(c).append(" | saldo $").append(c.getTarjeta().getSaldo())
                    .append(" | puntos ").append(c.getMontoPuntos().getTotalPuntosVigentes()).append("\n");
        }
        return texto.toString();
    }

    public List<VentaPelicula> ventasPorPelicula(SistemaCinema sistema, LocalDate desde, LocalDate hasta) {
        sistema.exigirAdministrador();
        validarFechas(desde, hasta);
        List<VentaPelicula> ventas = new ArrayList<>();
        for (Compra compra : sistema.getTodasCompras()) {
            VentaPelicula venta = buscarVenta(ventas, compra.getNombrePelicula());
            if (enRango(compra.getFechaCompra().toLocalDate(), desde, hasta)) {
                venta.total += compra.getTotal();
            }
        }
        return ventas;
    }

    private VentaPelicula buscarVenta(List<VentaPelicula> ventas, String titulo) {
        for (VentaPelicula venta : ventas) {
            if (venta.titulo.equals(titulo)) return venta;
        }
        VentaPelicula nueva = new VentaPelicula(titulo);
        ventas.add(nueva);
        return nueva;
    }

    // =====================================================================
    // Reportes por tipo (enunciado, sección 7) y estadísticas para los gráficos
    // =====================================================================

    /** Estadísticas del periodo. Las calcula el modelo; la vista solo dibuja los Map. */
    public ResumenEstadistico estadisticas(SistemaCinema sistema, LocalDate desde, LocalDate hasta) {
        sistema.exigirAdministrador();
        validarFechas(desde, hasta);
        return new EstadisticasCine(sistema.getTodasCompras(), sistema.getFuncionesAdministracion(),
                sistema.getClientes()).calcular(desde, hasta);
    }

    /** Genera el reporte elegido por el administrador para el rango de fechas. */
    public Reporte crear(SistemaCinema sistema, TipoReporte tipo, LocalDate desde, LocalDate hasta) {
        sistema.exigirAdministrador();
        validarFechas(desde, hasta);
        if (tipo == null) throw new IllegalArgumentException("Selecciona el tipo de reporte");
        switch (tipo) {
            case VENTAS: return ventas(sistema, desde, hasta);
            case ENTRADAS: return entradas(sistema, desde, hasta);
            case OCUPACION: return ocupacion(sistema, desde, hasta);
            case CONFITERIA: return confiteria(sistema, desde, hasta);
            case RECARGAS: return recargas(sistema, desde, hasta);
            case CANCELACIONES: return cancelaciones(sistema, desde, hasta);
            case PUNTOS: return puntos(sistema, desde, hasta);
            default: return resumen(sistema, desde, hasta);
        }
    }

    /** Resumen planeado en el taller: entradas por película, ingresos brutos y ocupación por función. */
    private Reporte resumen(SistemaCinema sistema, LocalDate desde, LocalDate hasta) {
        ResumenEstadistico datos = estadisticas(sistema, desde, hasta);
        Reporte reporte = new Reporte(TipoReporte.RESUMEN.getTitulo(), desde, hasta, List.of("Indicador", "Detalle", "Valor"));
        for (java.util.Map.Entry<String, Integer> pelicula : datos.getEntradasPorPelicula().entrySet()) {
            reporte.agregarFila("Entradas vendidas", pelicula.getKey(), String.valueOf(pelicula.getValue()));
        }
        for (java.util.Map.Entry<String, Long> pelicula : datos.getIngresosPorPelicula().entrySet()) {
            reporte.agregarFila("Ingresos brutos", pelicula.getKey(), moneda(pelicula.getValue()));
        }
        for (java.util.Map.Entry<String, Double> funcion : datos.getOcupacionPorFuncion().entrySet()) {
            reporte.agregarFila("Ocupación", funcion.getKey(), porcentaje(funcion.getValue()));
        }
        reporte.agregarTotal("Compras del periodo", String.valueOf(datos.getCompras()));
        reporte.agregarTotal("Entradas vendidas", String.valueOf(datos.getEntradasVendidas()));
        reporte.agregarTotal("Ingresos brutos", moneda(datos.getIngresosBrutos()));
        reporte.agregarTotal("Reembolsos", moneda(datos.getReembolsos()));
        reporte.agregarTotal("Ingresos menos reembolsos", moneda(datos.getIngresosNetos()));
        reporte.agregarTotal("Ocupación promedio", porcentaje(datos.getOcupacionPromedio()));
        return reporte;
    }

    private Reporte ventas(SistemaCinema sistema, LocalDate desde, LocalDate hasta) {
        Reporte reporte = new Reporte(TipoReporte.VENTAS.getTitulo(), desde, hasta,
                List.of("Fecha", "Factura", "Cliente", "Película", "Entradas", "Estado", "Pagado", "Puntos usados"));
        long ventas = 0, devuelto = 0;
        int cantidad = 0, puntos = 0;
        for (Compra c : sistema.getTodasCompras()) {
            if (!enRango(c.getFechaCompra().toLocalDate(), desde, hasta)) continue;
            reporte.agregarFila(c.getFecha(), c.getFactura(), c.getCliente().getNombre(), c.getNombrePelicula(),
                    String.valueOf(c.getAsientos().size()), c.getEstado(), moneda(c.getTotal()),
                    String.valueOf(c.getPuntosRedimidos()));
            ventas += c.getTotal(); devuelto += c.getReembolso(); cantidad++; puntos += c.getPuntosRedimidos();
        }
        reporte.agregarTotal("Compras", String.valueOf(cantidad));
        reporte.agregarTotal("Ventas brutas", moneda(ventas));
        reporte.agregarTotal("Reembolsado de esas compras", moneda(devuelto));
        reporte.agregarTotal("Ventas menos reembolsos", moneda(ventas - devuelto));
        reporte.agregarTotal("Pagado con puntos", String.valueOf(puntos) + " puntos");
        return reporte;
    }

    private Reporte entradas(SistemaCinema sistema, LocalDate desde, LocalDate hasta) {
        Reporte reporte = new Reporte(TipoReporte.ENTRADAS.getTitulo(), desde, hasta,
                List.of("Película", "Función", "Fecha y sala", "Entradas", "Compras"));
        int total = 0;
        List<String> peliculas = new ArrayList<>();
        List<Integer> porPelicula = new ArrayList<>();
        for (Funcion f : sistema.getFuncionesAdministracion()) {
            int entradas = 0, compras = 0;
            for (Compra c : sistema.getTodasCompras()) {
                if (c.getFuncion() == f && !c.estaCancelada() && enRango(c.getFechaCompra().toLocalDate(), desde, hasta)) {
                    entradas += c.getAsientos().size(); compras++;
                }
            }
            if (compras == 0) continue;
            String titulo = f.getPeliculaFuncion().getNombrePelicula();
            reporte.agregarFila(titulo, f.getIdFuncion(), fechaHora(f.getFechaHoraFuncion()) + " · Sala "
                    + f.getSalaFuncion().getNumero() + " (" + f.getSalaFuncion().getFormato() + ")",
                    String.valueOf(entradas), String.valueOf(compras));
            total += entradas;
            int posicion = peliculas.indexOf(titulo);
            if (posicion < 0) { peliculas.add(titulo); porPelicula.add(entradas); }
            else porPelicula.set(posicion, porPelicula.get(posicion) + entradas);
        }
        for (int i = 0; i < peliculas.size(); i++) {
            reporte.agregarTotal("Entradas de " + peliculas.get(i), String.valueOf(porPelicula.get(i)));
        }
        reporte.agregarTotal("Total de entradas", String.valueOf(total));
        return reporte;
    }

    private Reporte ocupacion(SistemaCinema sistema, LocalDate desde, LocalDate hasta) {
        Reporte reporte = new Reporte(TipoReporte.OCUPACION.getTitulo(), desde, hasta,
                List.of("Función", "Película", "Fecha", "Sala", "Ocupados", "Capacidad", "Ocupación"));
        long ocupados = 0, capacidad = 0;
        for (Funcion f : sistema.getFuncionesAdministracion()) {
            if (f.isCancelada() || !enRango(f.getFechaHoraFuncion().toLocalDate(), desde, hasta)) continue;
            Sala sala = f.getSalaFuncion();
            int usados = sala.getCapacidad() - sala.contarDisponibles();
            reporte.agregarFila(f.getIdFuncion(), f.getPeliculaFuncion().getNombrePelicula(),
                    fechaHora(f.getFechaHoraFuncion()), "Sala " + sala.getNumero() + " (" + sala.getFormato() + ")",
                    String.valueOf(usados), String.valueOf(sala.getCapacidad()),
                    porcentaje(100.0 * usados / sala.getCapacidad()));
            ocupados += usados; capacidad += sala.getCapacidad();
        }
        reporte.agregarTotal("Funciones", String.valueOf(reporte.getRegistrosEncontrados()));
        reporte.agregarTotal("Asientos ocupados", ocupados + " de " + capacidad);
        reporte.agregarTotal("Ocupación promedio", porcentaje(capacidad == 0 ? 0 : 100.0 * ocupados / capacidad));
        return reporte;
    }

    private Reporte confiteria(SistemaCinema sistema, LocalDate desde, LocalDate hasta) {
        Reporte reporte = new Reporte(TipoReporte.CONFITERIA.getTitulo(), desde, hasta,
                List.of("Producto o combo", "Unidades", "Valor"));
        List<String> nombres = new ArrayList<>();
        List<Integer> unidades = new ArrayList<>();
        List<Long> valores = new ArrayList<>();
        for (Compra c : sistema.getTodasCompras()) {
            if (c.estaCancelada() || !enRango(c.getFechaCompra().toLocalDate(), desde, hasta)) continue;
            for (ElementoProducto p : c.getConfiteria()) {
                int posicion = nombres.indexOf(p.getNombre());
                if (posicion < 0) { nombres.add(p.getNombre()); unidades.add(0); valores.add(0L); posicion = nombres.size() - 1; }
                unidades.set(posicion, unidades.get(posicion) + 1);
                valores.set(posicion, valores.get(posicion) + Math.round(p.calcularPrecio()));
            }
        }
        int totalUnidades = 0; long totalValor = 0;
        for (int i = 0; i < nombres.size(); i++) {
            reporte.agregarFila(nombres.get(i), String.valueOf(unidades.get(i)), moneda(valores.get(i)));
            totalUnidades += unidades.get(i); totalValor += valores.get(i);
        }
        reporte.agregarTotal("Unidades vendidas", String.valueOf(totalUnidades));
        reporte.agregarTotal("Valor de lista (antes de promociones)", moneda(totalValor));
        return reporte;
    }

    private Reporte recargas(SistemaCinema sistema, LocalDate desde, LocalDate hasta) {
        Reporte reporte = new Reporte(TipoReporte.RECARGAS.getTitulo(), desde, hasta,
                List.of("Fecha", "Cliente", "Tarjeta", "Detalle", "Monto"));
        long total = 0;
        for (Cliente cliente : sistema.getClientes()) {
            for (MovimientoTarjeta m : cliente.getTarjeta().getMovimientos()) {
                if (m.getTipo() == TipoMovimiento.RECARGA && enRango(m.getFechaReal().toLocalDate(), desde, hasta)) {
                    reporte.agregarFila(m.getFechaHora(), cliente.getNombre(), cliente.getTarjeta().getNumero(),
                            m.getDescripcion(), moneda(m.getMonto()));
                    total += m.getMonto();
                }
            }
        }
        int pendientes = 0;
        for (SolicitudRecarga solicitud : sistema.getSolicitudes()) if (solicitud.isPendiente()) pendientes++;
        reporte.agregarTotal("Recargas", String.valueOf(reporte.getRegistrosEncontrados()));
        reporte.agregarTotal("Total recargado (sin bonos)", moneda(total));
        reporte.agregarTotal("Solicitudes pendientes hoy", String.valueOf(pendientes));
        return reporte;
    }

    private Reporte cancelaciones(SistemaCinema sistema, LocalDate desde, LocalDate hasta) {
        Reporte reporte = new Reporte(TipoReporte.CANCELACIONES.getTitulo(), desde, hasta,
                List.of("Fecha cancelación", "Factura", "Cliente", "Película", "Motivo", "Pagado", "Reembolso", "% devuelto"));
        long pagado = 0, devuelto = 0;
        for (Compra c : sistema.getTodasCompras()) {
            if (c.getFechaCancelacion() == null || !enRango(c.getFechaCancelacion().toLocalDate(), desde, hasta)) continue;
            reporte.agregarFila(fechaHora(c.getFechaCancelacion()), c.getFactura(), c.getCliente().getNombre(),
                    c.getNombrePelicula(), c.getMotivoCancelacion(), moneda(c.getTotal()), moneda(c.getReembolso()),
                    porcentaje(c.getTotal() == 0 ? 100 : 100.0 * c.getReembolso() / c.getTotal()));
            pagado += c.getTotal(); devuelto += c.getReembolso();
        }
        reporte.agregarTotal("Cancelaciones", String.valueOf(reporte.getRegistrosEncontrados()));
        reporte.agregarTotal("Valor pagado", moneda(pagado));
        reporte.agregarTotal("Total reembolsado", moneda(devuelto));
        reporte.agregarTotal("Retenido por el cine", moneda(pagado - devuelto));
        return reporte;
    }

    private Reporte puntos(SistemaCinema sistema, LocalDate desde, LocalDate hasta) {
        Reporte reporte = new Reporte(TipoReporte.PUNTOS.getTitulo(), desde, hasta,
                List.of("Cliente", "Acumulados", "Redimidos", "Vigentes hoy", "Vencidos sin usar"));
        long acumulados = 0, redimidos = 0, vigentes = 0, vencidos = 0;
        for (Cliente cliente : sistema.getClientes()) {
            double ganados = 0, perdidos = 0;
            for (LotePuntos lote : cliente.getMontoPuntos().getLotes()) {
                if (!lote.isAnulado() && enRango(lote.getFechaObtencion().toLocalDate(), desde, hasta)) ganados += lote.getCantidad();
                if (!lote.isAnulado() && lote.estaVencido()) perdidos += lote.getDisponible();
            }
            int usados = 0;
            for (RedencionPuntos redencion : cliente.getMontoPuntos().getRedenciones()) {
                if (!redencion.isDevuelta() && enRango(redencion.getFecha().toLocalDate(), desde, hasta)) usados += redencion.getPuntos();
            }
            long saldo = Math.round(cliente.getMontoPuntos().getTotalPuntosVigentes());
            if (ganados == 0 && usados == 0 && saldo == 0 && perdidos == 0) continue;
            reporte.agregarFila(cliente.getNombre(), String.valueOf(Math.round(ganados)), String.valueOf(usados),
                    String.valueOf(saldo), String.valueOf(Math.round(perdidos)));
            acumulados += Math.round(ganados); redimidos += usados; vigentes += saldo; vencidos += Math.round(perdidos);
        }
        reporte.agregarTotal("Puntos acumulados en el periodo", String.valueOf(acumulados));
        reporte.agregarTotal("Puntos redimidos en el periodo", String.valueOf(redimidos));
        reporte.agregarTotal("Puntos vigentes hoy", String.valueOf(vigentes));
        reporte.agregarTotal("Puntos vencidos sin usar", String.valueOf(vencidos));
        reporte.agregarTotal("Regla", "10 % de lo pagado · 1 punto = $1 · vigencia de 1 año");
        return reporte;
    }

    private static String moneda(long valor) {
        return "$" + String.format(java.util.Locale.forLanguageTag("es-CO"), "%,d", valor);
    }

    private static String porcentaje(double valor) {
        return String.format(java.util.Locale.forLanguageTag("es-CO"), "%.1f %%", valor);
    }

    private static String fechaHora(java.time.LocalDateTime fecha) {
        return fecha.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    private void validarFechas(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null || hasta.isBefore(desde)) {
            throw new IllegalArgumentException("Revisa el intervalo de fechas");
        }
    }

    public static final class VentaPelicula {
        private final String titulo;
        private long total;

        private VentaPelicula(String titulo) {
            this.titulo = titulo;
        }

        public String getTitulo() { return titulo; }
        public long getTotal() { return total; }
    }

    private boolean enRango(LocalDate fecha, LocalDate desde, LocalDate hasta) {
        return !fecha.isBefore(desde) && !fecha.isAfter(hasta);
    }
}
