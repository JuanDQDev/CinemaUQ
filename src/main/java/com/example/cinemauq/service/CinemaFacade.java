package com.example.cinemauq.service;

import com.example.cinemauq.model.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//// aplicamos patron facade para ofrecerle a los controladores una
//// API mas sencilla para consultar y comprar
public class CinemaFacade {
    private final Cliente cliente;
    private final List<Pelicula> cartelera = new ArrayList<>();
    private final List<Funcion> funciones = new ArrayList<>();
    private final List<ElementoProducto> confiteria = new ArrayList<>();
    private final List<Compra> compras = new ArrayList<>();
    private final ServicioCompra servicioCompra = new ServicioCompra();

    public CinemaFacade(Cliente cliente) {
        if (cliente == null) throw new IllegalArgumentException("Se requiere un cliente");
        this.cliente = cliente;
    }

    public static CinemaFacade crearDemostracion() {
        Cliente cliente = new Cliente("1001", "Cliente de demostración", 22);
        CinemaFacade fachada = new CinemaFacade(cliente);
        Administrador admin = new Administrador("A01", "Administración CinemaUQ", "admin@cinemauq.demo");
        cliente.getTarjeta().recargar(admin, 150000);

        Pelicula p1 = new Pelicula("P01", "Titanic", "Drama romántico que narra la historia de amor entre Jack y Rose, dos jóvenes de distintas clases sociales que se conocen a bordo del trágico transatlántico en 1912.", RestriccionPelicula.DOCE);
        Pelicula p2 = new Pelicula("P02", "Spider-man: Brand New Day", "Peter Parker vive en el anonimato absoluto, completamente solo y dedicado a tiempo completo a ser un Spider-Man callejero.", RestriccionPelicula.QUINCE);
        Pelicula p3 = new Pelicula("P03", "Avengers Doomsday", "El argumento oficial sitúa a los superhéroes más queridos del planeta en un curso de colisión mortal. Tras las grietas interdimensionales abiertas en proyectos anteriores, héroes de tres universos distintos son arrastrados a una misma realidad debido a una colisión catastrófica.", RestriccionPelicula.QUINCE);
        fachada.cartelera.add(p1);
        fachada.cartelera.add(p2);
        fachada.cartelera.add(p3);
        Sala plantilla2d = new Sala(1, "2D", 6, 10);
        Sala plantilla3d = new Sala(2, "3D", 6, 10);
        Sala plantillaImax = new Sala(3, "IMAX 3D", 5, 8);
        LocalDateTime base = LocalDateTime.now().plusDays(2).withHour(18).withMinute(0).withSecond(0).withNano(0);
        fachada.funciones.add(new Funcion.Builder().conIdFuncion("F-001").conFechaHora(base)
                .conIdioma("Doblada").conPelicula(p1).conSala(plantillaImax.clonar()).build());
        fachada.funciones.add(new Funcion.Builder().conIdFuncion("F-002").conFechaHora(base.plusHours(3))
                .conIdioma("Subtitulada").conPelicula(p1).conSala(plantilla2d.clonar()).build());
        fachada.funciones.add(new Funcion.Builder().conIdFuncion("F-003").conFechaHora(base.plusDays(1))
                .conIdioma("Español").conPelicula(p2).conSala(plantilla3d.clonar()).build());
        fachada.funciones.add(new Funcion.Builder().conIdFuncion("F-004").conFechaHora(base.plusDays(1).plusHours(2))
                .conIdioma("Doblada").conPelicula(p3).conSala(plantilla2d.clonar()).build());
        Producto palomitas = new Producto("Palomitas grandes", 12000, 1);
        Producto gaseosa = new Producto("Gaseosa", 7000, 1);
        Producto nachos = new Producto("Nachos", 11000, 1);
        fachada.confiteria.add(palomitas);
        fachada.confiteria.add(gaseosa);
        fachada.confiteria.add(nachos);
        List<ElementoProducto> clasico = new ArrayList<>();
        clasico.add(palomitas);
        clasico.add(gaseosa);
        fachada.confiteria.add(new Combo("Combo clásico (-10 %)", 10, clasico));
        List<ElementoProducto> familiar = new ArrayList<>();
        familiar.add(palomitas);
        familiar.add(gaseosa);
        familiar.add(nachos);
        fachada.confiteria.add(new Combo("Combo familiar (-15 %)", 15, familiar));
        return fachada;
    }

    public Cliente getCliente() { return cliente; }
    public TarjetaVirtual getTarjeta() { return cliente.getTarjeta(); }
    public List<Pelicula> getCartelera() { return new ArrayList<>(cartelera); }
    public List<ElementoProducto> getConfiteria() { return new ArrayList<>(confiteria); }
    public List<Compra> getCompras() { return new ArrayList<>(compras); }

    public List<Funcion> getFunciones(Pelicula pelicula) {
        List<Funcion> resultado = new ArrayList<>();
        if (pelicula != null) for (Funcion funcion : funciones) {
            if (funcion.getPeliculaFuncion().getCodigo().equals(pelicula.getCodigo())){
                resultado.add(funcion)
            };
        }
        return resultado;
    }

    public Entrada getTipoEntrada(Funcion funcion) { return servicioCompra.crearEntrada(funcion); }

    public int calcularTotal(Funcion funcion, List<String> asientos, List<ElementoProducto> productos) {
        return servicioCompra.calcularTotal(funcion, asientos, productos);
    }

    public Compra confirmarCompra(Funcion funcion, List<String> asientos, List<ElementoProducto> productos) {
        Compra compra = servicioCompra.comprar(cliente, funcion, asientos, productos,
                new AdaptadorTarjetaVirtual(cliente.getTarjeta()));
        compras.add(compra);
        return compra;
    }
}
