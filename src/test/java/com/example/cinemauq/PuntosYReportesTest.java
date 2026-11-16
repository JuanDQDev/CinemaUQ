package com.example.cinemauq;

import com.example.cinemauq.exception.PuntosInsuficientesException;
import com.example.cinemauq.model.Cliente;
import com.example.cinemauq.model.Compra;
import com.example.cinemauq.model.EstadisticasCine;
import com.example.cinemauq.model.Funcion;
import com.example.cinemauq.model.LotePuntos;
import com.example.cinemauq.model.MontoPuntos;
import com.example.cinemauq.model.MovimientoTarjeta;
import com.example.cinemauq.model.ResumenEstadistico;
import com.example.cinemauq.service.CinemaFacade;
import com.example.cinemauq.service.ExportadorReporte;
import com.example.cinemauq.service.ExportadorReporteTxt;
import com.example.cinemauq.service.Reporte;
import com.example.cinemauq.service.ServicioReportes;
import com.example.cinemauq.service.SistemaCinema;
import com.example.cinemauq.service.TipoReporte;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Entrega 3: redención y vigencia de puntos, estadísticas, reportes exportables
 * y datos iniciales.
 */
class PuntosYReportesTest {
    private final SistemaCinema sistema = new SistemaCinema();
    private final Cliente ana = sistema.registrar("ana", "Ana", 22, "Clave123");

    private CinemaFacade entrarAna() {
        sistema.iniciarSesion("ana", "Clave123");
        return new CinemaFacade(sistema);
    }

    private void admin() { sistema.iniciarSesion("A01", "Admin123"); }

    private Funcion funcion(CinemaFacade f) { return f.getFunciones(f.getCartelera().get(0)).get(0); }

    // ------------------------------------------------------------------ Puntos

    @Test
    void seRedimenPrimeroLosPuntosMasProximosAVencer() {
        MontoPuntos cuenta = new MontoPuntos();
        LocalDateTime hoy = LocalDateTime.now();
        cuenta.acumularPuntos(100000, "FAC-NUEVA", hoy.minusMonths(1));   // 10.000 puntos, vencen en 11 meses
        cuenta.acumularPuntos(50000, "FAC-VIEJA", hoy.minusMonths(10));   // 5.000 puntos, vencen en 2 meses

        cuenta.redimir(6000, "FAC-X", hoy);

        LotePuntos nuevo = cuenta.getLotes().get(0);
        LotePuntos viejo = cuenta.getLotes().get(1);
        assertEquals(0, viejo.getDisponible(), 0.01);       // el que vence antes se agota primero
        assertEquals(9000, nuevo.getDisponible(), 0.01);
        assertEquals(9000, cuenta.getPuntosRedimibles(hoy));
        assertEquals(1, cuenta.getRedenciones().size());
    }

    @Test
    void losPuntosVencidosNoSePuedenUsar() {
        MontoPuntos cuenta = new MontoPuntos();
        LocalDateTime hoy = LocalDateTime.now();
        cuenta.acumularPuntos(100000, "FAC-1", hoy.minusMonths(13));      // vencidos
        cuenta.acumularPuntos(20000, "FAC-2", hoy.minusDays(1));          // 2.000 vigentes

        assertEquals(2000, cuenta.getPuntosRedimibles(hoy));
        assertThrows(PuntosInsuficientesException.class, () -> cuenta.redimir(2001, "FAC-X", hoy));
        assertEquals(2000, cuenta.getPuntosRedimibles(hoy));              // un intento fallido no descuenta nada
    }

    @Test
    void pagarUnaEntradaConPuntosDescuentaPuntosYCobraElResto() {
        CinemaFacade f = entrarAna();
        ana.getMontoPuntos().acumularPuntos(400000, "FAC-ANTERIOR");      // 40.000 puntos
        int saldo = f.getTarjeta().getSaldo();

        // Dos entradas IMAX 3D de $29.000: una se paga con puntos.
        Compra compra = f.confirmarCompra(funcion(f), List.of("A1", "A2"), List.of(), 1, List.of());

        assertEquals(29000, compra.getTotal());                           // lo pagado con la tarjeta
        assertEquals(29000, compra.getPuntosRedimidos());
        assertEquals(saldo - 29000, f.getTarjeta().getSaldo());
        // 40.000 - 29.000 redimidos + 2.900 ganados (10 % de lo pagado con tarjeta)
        assertEquals(13900, f.getPuntosDisponibles());
    }

    @Test
    void sinPuntosSuficientesNoSeCompraNiSeCobra() {
        CinemaFacade f = entrarAna();
        int saldo = f.getTarjeta().getSaldo();

        assertThrows(PuntosInsuficientesException.class,
                () -> f.confirmarCompra(funcion(f), List.of("A1"), List.of(), 1, List.of()));

        assertEquals(saldo, f.getTarjeta().getSaldo());
        assertTrue(f.getCompras().isEmpty());
        assertFalse(funcion(f).getSalaFuncion().buscarAsiento("A1").isOcupado());
    }

    @Test
    void soloSeRedimeLoQueEstaEnElCarrito() {
        CinemaFacade f = entrarAna();
        ana.getMontoPuntos().acumularPuntos(900000, "FAC-ANTERIOR");

        assertThrows(RuntimeException.class,
                () -> f.confirmarCompra(funcion(f), List.of("A1"), List.of(), 2, List.of()));
        assertThrows(RuntimeException.class,
                () -> f.confirmarCompra(funcion(f), List.of("A1"), List.of(), 0, List.of(f.getConfiteria().get(0))));
    }

    @Test
    void cancelarDevuelveLosPuntosUsadosYAnulaLosGanados() {
        CinemaFacade f = entrarAna();
        ana.getMontoPuntos().acumularPuntos(400000, "FAC-ANTERIOR");      // 40.000 puntos
        Compra compra = f.confirmarCompra(funcion(f), List.of("A1", "A2"), List.of(), 1, List.of());

        f.cancelarCompra(compra, f.calcularReembolso(compra));

        assertEquals(40000, f.getPuntosDisponibles());
        assertTrue(ana.getMontoPuntos().getRedenciones().get(0).isDevuelta());
    }

    // ------------------------------------------------------- Errores corregidos

    @Test
    void laPromocionValidaSeGuardaYLaInvalidaSeRechaza() {
        admin();
        LocalDate hoy = LocalDate.now();
        assertEquals(20, sistema.guardarPromocion(null, "Veinte", 20, hoy, hoy, true).getPorcentaje());
        assertThrows(IllegalArgumentException.class,
                () -> sistema.guardarPromocion(null, "Mal", 0, hoy, hoy, true));
        assertThrows(IllegalArgumentException.class,
                () -> sistema.guardarPromocion(null, "Fechas", 10, hoy, hoy.minusDays(1), true));
        assertEquals(1, sistema.getPromociones().size());
    }

    // ------------------------------------------------- Estadísticas y reportes

    @Test
    void lasEstadisticasSeCalculanEnElModeloYEntreganMapas() {
        CinemaFacade f = entrarAna();
        Compra compra = f.confirmarCompra(funcion(f), List.of("A1"), List.of(f.getConfiteria().get(0)));
        admin();
        LocalDate hoy = LocalDate.now();

        ResumenEstadistico resumen = new EstadisticasCine(sistema.getTodasCompras(),
                sistema.getFuncionesAdministracion(), sistema.getClientes()).calcular(hoy.minusDays(2), hoy.plusDays(5));

        assertEquals(1, resumen.getCompras());
        assertEquals(1, resumen.getEntradasVendidas());
        assertEquals(compra.getTotal(), resumen.getIngresosBrutos());
        assertEquals(1, resumen.getEntradasPorPelicula().get("Titanic"));
        assertEquals((long) compra.getTotal(), resumen.getIngresosPorPelicula().get("Titanic"));
        assertEquals(8, resumen.getVentasPorDia().size());                 // un punto por día, aunque no haya ventas
        assertEquals(4, resumen.getOcupacionPorFuncion().size());
        // F-001: 1 de 40 asientos ocupados
        assertEquals(2.5, resumen.getOcupacionPorFuncion().values().iterator().next(), 0.001);
    }

    @Test
    void cadaReporteTieneTituloFechaPeriodoRegistrosYTotales() {
        CinemaFacade f = entrarAna();
        f.confirmarCompra(funcion(f), List.of("A1"), List.of(f.getConfiteria().get(0)));
        admin();
        ServicioReportes reportes = new ServicioReportes();
        ExportadorReporte exportador = new ExportadorReporteTxt();
        LocalDate hoy = LocalDate.now();

        assertEquals(8, TipoReporte.values().length);                       // los 7 del enunciado y el resumen
        for (TipoReporte tipo : TipoReporte.values()) {
            Reporte reporte = reportes.crear(sistema, tipo, hoy.minusDays(1), hoy.plusDays(5));
            String texto = exportador.formatear(reporte);

            assertEquals(tipo.getTitulo(), reporte.getTitulo());
            assertFalse(reporte.getTotales().isEmpty(), tipo.name());
            assertTrue(texto.contains(tipo.getTitulo().toUpperCase()), tipo.name());
            assertTrue(texto.contains("Fecha de generación:"), tipo.name());
            assertTrue(texto.contains("Periodo consultado:"), tipo.name());
            assertTrue(texto.contains("Registros encontrados: " + reporte.getRegistrosEncontrados()), tipo.name());
            assertTrue(texto.contains("TOTALES"), tipo.name());
        }
        assertThrows(IllegalArgumentException.class,
                () -> reportes.crear(sistema, TipoReporte.VENTAS, hoy, hoy.minusDays(1)));
    }

    @Test
    void elExportadorEscribeElArchivoDeTexto(@TempDir Path carpeta) throws IOException {
        CinemaFacade f = entrarAna();
        f.confirmarCompra(funcion(f), List.of("A1"), List.of());
        admin();
        LocalDate hoy = LocalDate.now();
        Reporte ventas = new ServicioReportes().crear(sistema, TipoReporte.VENTAS, hoy, hoy);
        Path archivo = carpeta.resolve("ventas.txt");

        new ExportadorReporteTxt().exportar(ventas, archivo);

        String contenido = Files.readString(archivo);
        assertTrue(contenido.contains("VENTAS REALIZADAS POR PERIODO"));
        assertTrue(contenido.contains("Ventas brutas: $29.000"));
    }

    @Test
    void losReportesSonSoloParaElAdministrador() {
        entrarAna();
        LocalDate hoy = LocalDate.now();
        assertThrows(SecurityException.class,
                () -> new ServicioReportes().crear(sistema, TipoReporte.VENTAS, hoy, hoy));
    }

    // -------------------------------------------------------- Datos iniciales

    @Test
    void losDatosDeDemostracionSonSuficientesParaCadaReporte() {
        SistemaCinema demo = SistemaCinema.conDatosDemostracion();
        assertFalse(demo.esAdministrador());                                // la carga no deja sesiones abiertas
        assertFalse(demo.hayCliente());
        demo.iniciarSesion("admin", "Admin123");

        assertEquals(6, demo.getPeliculas().size());
        assertEquals(4, demo.getSalas().size());
        assertEquals(8, demo.getClientes().size());
        assertTrue(demo.getFuncionesAdministracion().size() >= 20);
        assertTrue(demo.getTodasCompras().size() >= 50);

        LocalDate hoy = LocalDate.now();
        ServicioReportes reportes = new ServicioReportes();
        for (TipoReporte tipo : TipoReporte.values()) {
            assertTrue(reportes.crear(demo, tipo, hoy.minusDays(30), hoy).getRegistrosEncontrados() > 0, tipo.name());
        }

        boolean vencidos = false, redimidos = false, parcial = false, porCine = false;
        for (Cliente cliente : demo.getClientes()) {
            for (LotePuntos lote : cliente.getMontoPuntos().getLotes()) vencidos |= lote.estaVencido();
            redimidos |= !cliente.getMontoPuntos().getRedenciones().isEmpty();
            List<MovimientoTarjeta> movimientos = cliente.getTarjeta().getMovimientos();
            for (int i = 1; i < movimientos.size(); i++) {
                assertFalse(movimientos.get(i).getFechaReal().isBefore(movimientos.get(i - 1).getFechaReal()),
                        "Movimientos en orden: " + cliente.getNombre());
            }
        }
        for (Compra compra : demo.getTodasCompras()) {
            if (!compra.estaCancelada()) continue;
            parcial |= compra.getReembolso() < compra.getTotal();
            porCine |= compra.getMotivoCancelacion().contains("cine");
        }
        assertTrue(vencidos, "Debe haber puntos vencidos para mostrar la vigencia");
        assertTrue(redimidos, "Debe haber compras pagadas con puntos");
        assertTrue(parcial, "Debe haber una cancelación con reembolso del 80 %");
        assertTrue(porCine, "Debe haber una función cancelada por el cine");
    }

    @Test
    void losUsuariosDePruebaDocumentadosPuedenEntrar() {
        SistemaCinema demo = SistemaCinema.conDatosDemostracion();
        String[][] usuarios = {{"1001", "Demo123"}, {"laura", "Laura123"}, {"mateo", "Mateo123"}, {"sara", "Sara1234"},
                {"valentina", "Valentina123"}, {"camilo", "Camilo123"}, {"daniela", "Daniela123"}, {"gloria", "Gloria123"}};
        for (String[] usuario : usuarios) {
            demo.iniciarSesion(usuario[0], usuario[1]);
            assertTrue(demo.hayCliente(), usuario[0]);
        }
        demo.iniciarSesion("A01", "Admin123");
        assertTrue(demo.esAdministrador());
        demo.iniciarSesion("1001", "Demo123");
        assertTrue(new CinemaFacade(demo).getTarjeta().getSaldo() >= 200000);
    }
}
