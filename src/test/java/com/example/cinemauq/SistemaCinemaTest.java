package com.example.cinemauq;

import com.example.cinemauq.model.*;
import com.example.cinemauq.service.CinemaFacade;
import com.example.cinemauq.service.ServicioCompra;
import com.example.cinemauq.service.ServicioReportes;
import com.example.cinemauq.service.SistemaCinema;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SistemaCinemaTest {
    private final SistemaCinema sistema = new SistemaCinema();
    private final Cliente ana = sistema.registrar("ana", "Ana", 22, "Clave123");
    private final Cliente bob = sistema.registrar("bob", "Bob", 23, "Clave456");

    private CinemaFacade entrar(String id) {
        sistema.iniciarSesion(id, id.equals("ana") ? "Clave123" : "Clave456");
        return new CinemaFacade(sistema);
    }
    private void admin() { sistema.iniciarSesion("A01", "Admin123"); }
    private Funcion funcion(CinemaFacade f) { return f.getFunciones(f.getCartelera().get(0)).get(0); }
    private Compra comprar(CinemaFacade f, String asiento) { return f.confirmarCompra(funcion(f), List.of(asiento), List.of()); }

    @Test void cadaCuentaRecibeSuBonoUnaVez() {
        assertNotSame(ana.getTarjeta(), bob.getTarjeta());
        assertEquals(50000, ana.getTarjeta().getSaldo()); assertEquals(50000, bob.getTarjeta().getSaldo());
        assertEquals(TipoMovimiento.BONO_BIENVENIDA, ana.getTarjeta().getMovimientos().get(0).getTipo());
        assertThrows(RuntimeException.class, () -> ana.getTarjeta().otorgarBonoBienvenida());
        assertThrows(RuntimeException.class, () -> sistema.registrar("ANA", "Otra", 22, "Clave123"));
        assertThrows(RuntimeException.class, () -> sistema.registrar("admin", "Admin", 22, "Clave123"));
        assertThrows(RuntimeException.class, () -> sistema.registrar("nue", "Nueva", 22, "123"));
        admin(); assertEquals(2, sistema.getClientes().size());
    }

    @Test void credencialesInvalidasNoCreanSesion() {
        assertThrows(RuntimeException.class, () -> sistema.iniciarSesion("ana", "erronea"));
        assertFalse(sistema.hayCliente()); assertFalse(sistema.esAdministrador());
        assertThrows(SecurityException.class, () -> new CinemaFacade(sistema));
        assertThrows(RuntimeException.class, () -> sistema.iniciarSesion("A01", "incorrecta"));
        assertFalse(sistema.esAdministrador());
    }

    @Test void cerrarSesionRevocaFachadaYNoBorraDatos() {
        CinemaFacade f = entrar("ana"); Compra compra = comprar(f, "A1"); int saldo = f.getTarjeta().getSaldo();
        sistema.cerrarSesion(); assertThrows(SecurityException.class, f::getCompras);
        CinemaFacade nuevo = entrar("ana");
        assertThrows(SecurityException.class, f::getTarjeta);
        assertEquals(saldo, nuevo.getTarjeta().getSaldo()); assertEquals(List.of(compra), nuevo.getCompras());
    }

    @Test void comprasSaldoPuntosYMovimientosNoSeComparten() {
        CinemaFacade f = entrar("ana"); Compra compra = comprar(f, "A1");
        CinemaFacade otro = entrar("bob");
        assertTrue(otro.getCompras().isEmpty()); assertEquals(50000, otro.getTarjeta().getSaldo());
        assertEquals(1, otro.getTarjeta().getMovimientos().size()); assertEquals(0, bob.getMontoPuntos().getTotalPuntosVigentes());
        assertThrows(SecurityException.class, () -> otro.calcularReembolso(compra));
        assertThrows(SecurityException.class, () -> otro.cancelarCompra(compra, compra.getTotal()));
        assertThrows(SecurityException.class, f::getCompras);
        assertTrue(ana.getMontoPuntos().getTotalPuntosVigentes() > 0);
    }

    @Test void asientoSeComparteParaImpedirDobleVenta() {
        CinemaFacade f = entrar("ana"); comprar(f, "A1"); CinemaFacade otro = entrar("bob");
        assertThrows(RuntimeException.class, () -> comprar(otro, "A1")); assertEquals(50000, otro.getTarjeta().getSaldo());
        assertTrue(otro.getCompras().isEmpty());
    }

    @Test void clienteNoPuedeEjecutarOperacionesAdministrativas() {
        CinemaFacade f = entrar("ana");
        assertThrows(SecurityException.class, sistema::getClientes);
        assertThrows(SecurityException.class, sistema::getTodasCompras);
        assertThrows(SecurityException.class, () -> sistema.cambiarTarifas(1,0,0));
        assertThrows(SecurityException.class, () -> sistema.recargarCliente(ana, 1000));
        assertThrows(SecurityException.class, () -> sistema.cancelarFuncion(funcion(f)));
        assertThrows(SecurityException.class, () -> sistema.guardarProducto(null,"Gratis",1));
        assertThrows(SecurityException.class, () -> sistema.guardarPromocion(null,"P",100,LocalDate.now(),LocalDate.now(),true));
    }

    @Test void cancelacionReembolsaUnaVezLiberaAsientosYAnulaPuntos() {
        CinemaFacade f = entrar("ana"); Compra c = comprar(f, "A1");
        assertEquals(c.getTotal(), f.calcularReembolso(c));
        f.cancelarCompra(c, c.getTotal());
        assertEquals(50000, f.getTarjeta().getSaldo()); assertTrue(c.estaCancelada());
        assertEquals(c.getTotal(), c.getReembolso()); assertEquals(0, ana.getMontoPuntos().getTotalPuntosVigentes());
        assertFalse(c.getFuncion().getSalaFuncion().buscarAsiento("A1").isOcupado());
        assertThrows(RuntimeException.class, () -> f.cancelarCompra(c, c.getTotal()));
        assertThrows(RuntimeException.class, () -> f.getTarjeta().reembolsar(c.getTotal(), c.getFactura()));
        assertEquals(3, f.getTarjeta().getMovimientos().size());
        assertNotNull(c.getFechaCancelacion());
    }

    @Test void tarjetaBloqueadaSiRecibeSuReembolso() {
        CinemaFacade f = entrar("ana"); Compra c = comprar(f,"A1"); f.getTarjeta().setActiva(false);
        f.cancelarCompra(c,c.getTotal()); assertEquals(50000,f.getTarjeta().getSaldo()); assertFalse(f.getTarjeta().isActiva());
    }

    @Test void plazosExactosUsanStateYStrategy() {
        CinemaFacade f = entrar("ana"); Compra c = comprar(f,"A1"); ServicioCompra servicio = sistema.getServicio();
        LocalDateTime fecha = c.getFuncion().getFechaHoraFuncion();
        assertEquals(c.getTotal(), servicio.procesarReembolso(c,fecha.minusHours(24).minusNanos(1)),0.01);
        assertEquals(c.getTotal()*0.8,servicio.procesarReembolso(c,fecha.minusHours(24)),0.01);
        assertEquals(c.getTotal()*0.8,servicio.procesarReembolso(c,fecha.minusHours(2)),0.01);
        int saldo = f.getTarjeta().getSaldo();
        assertThrows(RuntimeException.class,()->servicio.cancelarCompra(c,fecha.minusHours(2).plusNanos(1)));
        assertEquals(saldo,f.getTarjeta().getSaldo()); assertFalse(c.estaCancelada());
        int devuelto = servicio.cancelarCompra(c,fecha.minusHours(2));
        assertEquals((int)Math.round(c.getTotal()*0.8),devuelto);
        assertEquals(50000-c.getTotal()+devuelto,f.getTarjeta().getSaldo());
    }

    @Test void noCancelaSiElMontoConfirmadoNoCoincide() {
        CinemaFacade f = entrar("ana"); Compra c = comprar(f,"A1");
        assertThrows(RuntimeException.class,()->f.cancelarCompra(c,c.getTotal()-1));
        assertFalse(c.estaCancelada()); assertEquals(50000-c.getTotal(),f.getTarjeta().getSaldo());
    }

    @Test void cineCancelaFuncionAlCienInclusoDentroDeDosHoras() {
        admin(); Funcion cercana = sistema.guardarFuncion(null,sistema.getPeliculas().get(0),sistema.getSalas().get(0),LocalDateTime.now().plusHours(1),"Doblada");
        CinemaFacade fa=entrar("ana"); Compra a=fa.confirmarCompra(cercana,List.of("A1"),List.of());
        CinemaFacade fb=entrar("bob"); Compra b=fb.confirmarCompra(cercana,List.of("A2"),List.of());
        assertThrows(RuntimeException.class,()->fb.calcularReembolso(b));
        admin(); assertEquals(2,sistema.cancelarFuncion(cercana));
        assertEquals(50000,ana.getTarjeta().getSaldo()); assertEquals(50000,bob.getTarjeta().getSaldo());
        assertTrue(a.estaCancelada()); assertTrue(b.estaCancelada()); assertTrue(cercana.isCancelada());
        assertThrows(RuntimeException.class,()->sistema.cancelarFuncion(cercana));
        CinemaFacade f=entrar("ana"); assertFalse(f.getFunciones(cercana.getPeliculaFuncion()).contains(cercana));
        assertThrows(RuntimeException.class,()->f.confirmarCompra(cercana,List.of("A1"),List.of()));
    }

    @Test void cancelarFuncionNoDuplicaReembolsoDeCompraYaCancelada() {
        CinemaFacade fa=entrar("ana"); Compra a=comprar(fa,"A1"); fa.cancelarCompra(a,a.getTotal());
        CinemaFacade fb=entrar("bob"); comprar(fb,"A2");
        admin(); assertEquals(1,sistema.cancelarFuncion(a.getFuncion()));
        assertEquals(50000,ana.getTarjeta().getSaldo()); assertEquals(50000,bob.getTarjeta().getSaldo());
        assertEquals(3,ana.getTarjeta().getMovimientos().size());
    }

    @Test void solicitudesSonPropiasYSeApruebanUnaSolaVez() {
        CinemaFacade fa=entrar("ana"); fa.solicitarRecarga(20000); assertEquals(50000,fa.getTarjeta().getSaldo());
        CinemaFacade fb=entrar("bob"); assertTrue(fb.getSolicitudes().isEmpty());
        admin(); SolicitudRecarga solicitud=sistema.getSolicitudes().get(0); sistema.resolverSolicitud(solicitud,true);
        assertEquals(70000,ana.getTarjeta().getSaldo()); assertEquals(50000,bob.getTarjeta().getSaldo());
        assertThrows(RuntimeException.class,()->sistema.resolverSolicitud(solicitud,true));
        fa=entrar("ana"); fa.solicitarRecarga(10000); admin(); sistema.resolverSolicitud(sistema.getSolicitudes().get(1),false);
        assertEquals(70000,ana.getTarjeta().getSaldo());
    }

    @Test void editarTarifasYProductosNoCambiaComprasAnteriores() {
        CinemaFacade f=entrar("ana"); Producto producto=(Producto)f.getConfiteria().get(0);
        Compra c=f.confirmarCompra(funcion(f),List.of("A1"),List.of(producto)); int total=c.getTotal(); double precio=c.getConfiteria().get(0).calcularPrecio();
        admin(); sistema.cambiarTarifas(25000,5000,8000); sistema.guardarProducto(producto,"Nuevo nombre",15000);
        assertEquals(total,c.getTotal()); assertEquals(precio,c.getConfiteria().get(0).calcularPrecio());
        assertEquals("Palomitas grandes",c.getConfiteria().get(0).getNombre());
        CinemaFacade nuevo=entrar("ana"); assertEquals(38000,nuevo.calcularTotal(funcion(nuevo),List.of("A2"),List.of()));
        assertEquals(total,nuevo.calcularReembolso(c));
    }

    @Test void promoMayorVigenteSeAplicaYCompraGratisSeCancela() {
        admin(); LocalDate hoy=LocalDate.now();
        sistema.guardarPromocion(null,"Vencida",90,hoy.minusDays(2),hoy.minusDays(1),true);
        sistema.guardarPromocion(null,"Inactiva",99,hoy,hoy.plusDays(1),false);
        sistema.guardarPromocion(null,"Diez",10,hoy,hoy,true);
        sistema.guardarPromocion(null,"Veinte",20,hoy,hoy,true);
        CinemaFacade f=entrar("ana"); assertEquals(23200,f.calcularTotal(funcion(f),List.of("A1"),List.of()));
        admin(); sistema.guardarPromocion(null,"Gratis",100,hoy,hoy,true);
        f=entrar("ana"); Compra c=comprar(f,"A1"); assertEquals(0,c.getTotal()); f.cancelarCompra(c,0);
        assertTrue(c.estaCancelada()); assertEquals(50000,f.getTarjeta().getSaldo());
    }

    @Test void administrarCatalogoYDistribucionPreservaFuncionesVendidas() {
        CinemaFacade f=entrar("ana"); Compra c=comprar(f,"A1"); admin();
        Sala original=sistema.getSalas().get(2);
        Sala nueva=sistema.guardarSala(original,original.getNumero(),"IMAX",4,6);
        assertEquals(24,nueva.getCapacidad()); assertEquals(40,c.getFuncion().getSalaFuncion().getCapacidad());
        assertThrows(RuntimeException.class,()->sistema.guardarFuncion(c.getFuncion(),c.getFuncion().getPeliculaFuncion(),nueva,LocalDateTime.now().plusDays(5),"Doblada"));
        assertThrows(RuntimeException.class,()->sistema.eliminarFuncion(c.getFuncion()));
        Producto producto=sistema.guardarProducto(null,"Helado",6000);
        Combo combo=sistema.guardarCombo(null,"Combo helado",10,List.of(producto));
        assertEquals(5400,combo.calcularPrecio());
        assertThrows(RuntimeException.class,()->sistema.eliminarConfiteria(producto));
        sistema.eliminarConfiteria(combo); sistema.eliminarConfiteria(producto);
        assertFalse(sistema.getConfiteria().contains(producto));
        Pelicula p=sistema.guardarPelicula(null,"Película nueva","Sinopsis",RestriccionPelicula.DOCE);
        sistema.eliminarPelicula(p); assertFalse(sistema.getPeliculas().contains(p));
    }

    @Test void reporteSeparaRecargaBonoVentaYReembolso() {
        CinemaFacade f=entrar("ana"); Compra c=comprar(f,"A1"); f.cancelarCompra(c,c.getTotal());
        admin(); sistema.recargarCliente(ana,10000);
        ServicioReportes reportes = new ServicioReportes();
        LocalDate hoy = LocalDate.now();
        String reporte = reportes.generar(sistema, hoy, hoy);
        assertEquals(29000, reportes.ventasPorPelicula(sistema, hoy, hoy).get(0).getTotal());
        assertEquals(0, reportes.ventasPorPelicula(sistema, hoy.minusDays(2), hoy.minusDays(1)).get(0).getTotal());
        assertTrue(reporte.contains("Ventas brutas: $29000"));
        assertTrue(reporte.contains("Reembolsos realizados en el periodo: $29000"));
        assertTrue(reporte.contains("Recargas aprobadas (sin bonos): $10000"));
        assertTrue(reporte.contains("Cancelaciones realizadas: 1"));
    }

    @Test void nuevaInstanciaNoConservaCuentas() {
        SistemaCinema nuevo=new SistemaCinema();
        assertThrows(RuntimeException.class,()->nuevo.iniciarSesion("ana","Clave123"));
        nuevo.iniciarSesion("A01","Admin123"); assertTrue(nuevo.getClientes().isEmpty());
    }
}
