package com.example.cinemauq.service;

import com.example.cinemauq.model.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Datos compartidos del cine y sesión actual. Todo dura mientras la aplicación esté abierta. */
public class SistemaCinema {
    private final SesionesCinema sesiones = new SesionesCinema();
    private final List<Pelicula> peliculas = new ArrayList<>();
    private final List<Sala> salas = new ArrayList<>();
    private final List<Funcion> funciones = new ArrayList<>();
    private final List<ElementoProducto> confiteria = new ArrayList<>();
    private final List<Promocion> promociones = new ArrayList<>();
    private final List<Compra> compras = new ArrayList<>();
    private final List<SolicitudRecarga> solicitudes = new ArrayList<>();
    private final Tarifario tarifas = new Tarifario();
    private final ServicioCompra servicio = new ServicioCompra(tarifas, promociones);
    private int siguientePelicula = 4;
    private int siguienteFuncion = 5;
    private int siguientePromocion = 1;

    public SistemaCinema() {
        Pelicula p1 = new Pelicula("P01", "Titanic", "Drama romántico que narra la historia de amor entre Jack y Rose, dos jóvenes de distintas clases sociales que se conocen a bordo del trágico transatlántico en 1912.", RestriccionPelicula.DOCE);
        Pelicula p2 = new Pelicula("P02", "Spider-man: Brand New Day", "Peter Parker vive en el anonimato absoluto, completamente solo y dedicado a tiempo completo a ser un Spider-Man callejero.", RestriccionPelicula.QUINCE);
        Pelicula p3 = new Pelicula("P03", "Avengers Doomsday", "El argumento oficial sitúa a los superhéroes más queridos del planeta en un curso de colisión mortal. Tras las grietas interdimensionales abiertas en proyectos anteriores, héroes de tres universos distintos son arrastrados a una misma realidad debido a una colisión catastrófica.", RestriccionPelicula.QUINCE);
        peliculas.add(p1);
        peliculas.add(p2);
        peliculas.add(p3);
        Sala plantilla2d = new Sala(1, "2D", 6, 10);
        Sala plantilla3d = new Sala(2, "3D", 6, 10);
        Sala plantillaImax = new Sala(3, "IMAX 3D", 5, 8);
        salas.add(plantilla2d);
        salas.add(plantilla3d);
        salas.add(plantillaImax);
        LocalDateTime base = LocalDateTime.now().plusDays(2).withHour(18).withMinute(0).withSecond(0).withNano(0);
        funciones.add(new Funcion.Builder().conIdFuncion("F-001").conFechaHora(base)
                .conIdioma("Doblada").conPelicula(p1).conSala(plantillaImax.clonar()).build());
        funciones.add(new Funcion.Builder().conIdFuncion("F-002").conFechaHora(base.plusHours(3))
                .conIdioma("Subtitulada").conPelicula(p1).conSala(plantilla2d.clonar()).build());
        funciones.add(new Funcion.Builder().conIdFuncion("F-003").conFechaHora(base.plusDays(1))
                .conIdioma("Español").conPelicula(p2).conSala(plantilla3d.clonar()).build());
        funciones.add(new Funcion.Builder().conIdFuncion("F-004").conFechaHora(base.plusDays(1).plusHours(2))
                .conIdioma("Doblada").conPelicula(p3).conSala(plantilla2d.clonar()).build());
        Producto palomitas = new Producto("Palomitas grandes", 12000, 1);
        Producto gaseosa = new Producto("Gaseosa", 7000, 1);
        Producto nachos = new Producto("Nachos", 11000, 1);
        confiteria.add(palomitas);
        confiteria.add(gaseosa);
        confiteria.add(nachos);
        List<ElementoProducto> clasico = new ArrayList<>();
        clasico.add(palomitas);
        clasico.add(gaseosa);
        confiteria.add(new Combo("Combo clásico (-10 %)", 0.10, clasico));
        List<ElementoProducto> familiar = new ArrayList<>();
        familiar.add(palomitas);
        familiar.add(gaseosa);
        familiar.add(nachos);
        confiteria.add(new Combo("Combo familiar (-15 %)", 0.15, familiar));
    }

    /**
     * Cine con datos de demostración: clientes, historial de compras, recargas,
     * cancelaciones y puntos. Lo usa la aplicación al arrancar para que los
     * reportes y los gráficos tengan información (ver DatosDemostracion).
     */
    public static SistemaCinema conDatosDemostracion() {
        SistemaCinema sistema = new SistemaCinema();
        DatosDemostracion.cargar(sistema, LocalDateTime.now());
        return sistema;
    }

    // ---- Operaciones internas para los datos de demostración (solo este paquete) ----

    Cliente registrarConFecha(String id, String nombre, int edad, String clave, LocalDateTime fecha) {
        return sesiones.registrar(id, nombre, edad, clave, fecha);
    }

    /** Función que ya ocurrió: solo sirve como historial para reportes y estadísticas. */
    Funcion agregarFuncionHistorica(Pelicula pelicula, Sala sala, LocalDateTime fecha, String idioma) {
        Funcion funcion = new Funcion.Builder().conIdFuncion("F-" + String.format("%03d", siguienteFuncion))
                .conFechaHora(fecha).conIdioma(idioma).conPelicula(pelicula).conSala(sala.clonar()).build();
        siguienteFuncion++;
        funciones.add(funcion);
        return funcion;
    }

    void recargarConFecha(Cliente cliente, int monto, LocalDateTime fecha) {
        validarMonto(monto);
        cliente.getTarjeta().recargar(sesiones.administradorActual(), monto, fecha);
    }

    public Cliente registrar(String id, String nombre, int edad, String clave) {
        return sesiones.registrar(id, nombre, edad, clave);
    }

    public void iniciarSesion(String id, String clave) {
        sesiones.iniciarSesion(id, clave);
    }

    public void cerrarSesion() {
        sesiones.cerrarSesion();
    }

    public boolean esAdministrador() { return sesiones.esAdministrador(); }
    public boolean hayCliente() { return sesiones.hayCliente(); }
    public long getVersionSesion() { return sesiones.getVersionSesion(); }

    public void exigirAdministrador() {
        sesiones.exigirAdministrador();
    }

    Cliente exigirCliente(long version) {
        return sesiones.exigirCliente(version);
    }

    public Cliente getClienteActual() {
        return sesiones.getClienteActual();
    }

    public List<Pelicula> getPeliculas() { sesiones.exigirSesion(); return new ArrayList<>(peliculas); }
    public List<Sala> getSalas() { exigirAdministrador(); return new ArrayList<>(salas); }
    public List<Funcion> getFuncionesAdministracion() { exigirAdministrador(); return new ArrayList<>(funciones); }
    public List<ElementoProducto> getConfiteria() { sesiones.exigirSesion(); return new ArrayList<>(confiteria); }
    public List<Promocion> getPromociones() { exigirAdministrador(); return new ArrayList<>(promociones); }
    public List<Compra> getTodasCompras() { exigirAdministrador(); return new ArrayList<>(compras); }
    public List<SolicitudRecarga> getSolicitudes() { exigirAdministrador(); return new ArrayList<>(solicitudes); }

    public List<Cliente> getClientes() {
        return sesiones.getClientes();
    }

    public int[] getTarifas() {
        exigirAdministrador();
        return new int[]{tarifas.getEntradaBase(), tarifas.getRecargo3D(), tarifas.getRecargoIMAX()};
    }

    public void cambiarTarifas(int base, int tresD, int imax) {
        exigirAdministrador();
        tarifas.actualizar(base, tresD, imax);
    }

    List<Funcion> funcionesDisponibles(Pelicula pelicula) {
        List<Funcion> lista = new ArrayList<>();
        for (Funcion funcion : funciones) {
            if (funcion.getPeliculaFuncion() == pelicula && !funcion.isCancelada()
                    && funcion.getFechaHoraFuncion().isAfter(LocalDateTime.now())) lista.add(funcion);
        }
        return lista;
    }

    List<Compra> comprasCliente(Cliente cliente) {
        List<Compra> lista = new ArrayList<>();
        for (Compra compra : compras) if (compra.getCliente() == cliente) lista.add(compra);
        return lista;
    }

    public List<Compra> comprasParaAdministrador(Cliente cliente) {
        exigirAdministrador();
        return comprasCliente(cliente);
    }

    List<SolicitudRecarga> solicitudesCliente(Cliente cliente) {
        List<SolicitudRecarga> lista = new ArrayList<>();
        for (SolicitudRecarga solicitud : solicitudes) if (solicitud.getCliente() == cliente) lista.add(solicitud);
        return lista;
    }

    public ServicioCompra getServicio() { return servicio; }

    void validarCarrito(Funcion funcion, List<ElementoProducto> productos) {
        if (!funciones.contains(funcion) || funcion.isCancelada()
                || !funcion.getFechaHoraFuncion().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("La función ya no está disponible");
        }
        if (productos == null) throw new IllegalArgumentException("Carrito inválido");
        for (ElementoProducto producto : productos) {
            if (!confiteria.contains(producto)) throw new IllegalArgumentException("Un producto ya no está disponible; vuelve a la confitería");
        }
    }

    void agregarCompra(Compra compra) { compras.add(compra); }

    void validarPropietario(Compra compra, Cliente cliente) {
        if (compra == null || !compras.contains(compra) || compra.getCliente() != cliente) {
            throw new SecurityException("Solo puedes consultar o cancelar tus propias compras");
        }
    }

    void solicitarRecarga(Cliente cliente, int monto) {
        validarMonto(monto);
        solicitudes.add(new SolicitudRecarga(cliente, monto));
    }

    private void validarMonto(int monto) {
        if (monto <= 0 || monto > 10000000) {
            throw new IllegalArgumentException("El monto debe estar entre $1 y $10.000.000");
        }
    }

    public void recargarCliente(Cliente cliente, int monto) {
        exigirAdministrador();
        if (!getClientes().contains(cliente)) throw new IllegalArgumentException("Selecciona un cliente registrado");
        validarMonto(monto);
        cliente.getTarjeta().recargar(sesiones.administradorActual(), monto);
    }

    public void resolverSolicitud(SolicitudRecarga solicitud, boolean aprobar) {
        exigirAdministrador();
        if (!solicitudes.contains(solicitud) || !solicitud.isPendiente()) {
            throw new IllegalArgumentException("Selecciona una solicitud pendiente");
        }
        if (aprobar) recargarCliente(solicitud.getCliente(), solicitud.getMonto());
        solicitud.resolver(aprobar);
    }

    public Pelicula guardarPelicula(Pelicula actual, String nombre, String descripcion, RestriccionPelicula restriccion) {
        exigirAdministrador();
        if (actual != null && !peliculas.contains(actual)) throw new IllegalArgumentException("Película no encontrada");
        if (actual == null) {
            Pelicula nueva = new Pelicula("P" + String.format("%02d", siguientePelicula), nombre, descripcion, restriccion);
            nueva.actualizar(nombre, descripcion, restriccion);
            siguientePelicula++;
            peliculas.add(nueva);
            return nueva;
        }
        actual.actualizar(nombre, descripcion, restriccion);
        return actual;
    }

    public void eliminarPelicula(Pelicula pelicula) {
        exigirAdministrador();
        if (!peliculas.contains(pelicula)) throw new IllegalArgumentException("Selecciona una película");
        for (Funcion funcion : funciones) {
            if (funcion.getPeliculaFuncion() == pelicula && !funcion.isCancelada()
                    && funcion.getFechaHoraFuncion().isAfter(LocalDateTime.now())) {
                throw new IllegalArgumentException("Primero cancela o elimina sus funciones futuras");
            }
        }
        peliculas.remove(pelicula);
    }

    public Sala guardarSala(Sala actual, int numero, String formato, int filas, int columnas) {
        exigirAdministrador();
        if (numero <= 0 || columnas < 1 || columnas > 30
                || formato == null || !(formato.equals("2D") || formato.equals("3D")
                || formato.equals("IMAX") || formato.equals("IMAX 3D"))) {
            throw new IllegalArgumentException("Revisa el número, formato y columnas (1 a 30)");
        }
        if (actual != null && (!salas.contains(actual) || numero != actual.getNumero())) {
            throw new IllegalArgumentException("No cambies el número de una sala existente");
        }
        for (Sala sala : salas) {
            if (sala != actual && sala.getNumero() == numero) throw new IllegalArgumentException("Ya existe ese número de sala");
        }
        Sala nueva = new Sala(numero, formato, filas, columnas);
        if (actual == null) salas.add(nueva);
        else salas.set(salas.indexOf(actual), nueva);
        return nueva;
    }

    public void eliminarSala(Sala sala) {
        exigirAdministrador();
        if (!salas.contains(sala)) throw new IllegalArgumentException("Selecciona una sala");
        for (Funcion f : funciones) {
            if (f.getSalaFuncion().getNumero() == sala.getNumero() && !f.isCancelada()
                    && f.getFechaHoraFuncion().isAfter(LocalDateTime.now())) {
                throw new IllegalArgumentException("La sala tiene funciones futuras; cancélalas primero");
            }
        }
        salas.remove(sala);
    }

    public Funcion guardarFuncion(Funcion actual, Pelicula pelicula, Sala sala,
                                  LocalDateTime fecha, String idioma) {
        exigirAdministrador();
        if (!peliculas.contains(pelicula) || !salas.contains(sala)
                || fecha == null || !fecha.isAfter(LocalDateTime.now())
                || idioma == null || idioma.isBlank()) {
            throw new IllegalArgumentException("Selecciona película, sala, fecha futura e idioma");
        }
        if (actual != null) {
            if (!funciones.contains(actual) || actual.isCancelada()) throw new IllegalArgumentException("La función no se puede editar");
            for (Compra compra : compras) {
                if (compra.getFuncion() == actual) throw new IllegalArgumentException("Una función con compras no se edita; cancélala y crea otra");
            }
        }
        for (Funcion f : funciones) {
            if (f != actual && !f.isCancelada() && f.getSalaFuncion().getNumero() == sala.getNumero()
                    && f.getFechaHoraFuncion().equals(fecha)) {
                throw new IllegalArgumentException("Ya hay una función en esa sala a la misma hora");
            }
        }
        String id = actual == null ? "F-" + String.format("%03d", siguienteFuncion) : actual.getIdFuncion();
        Funcion nueva = new Funcion.Builder().conIdFuncion(id).conFechaHora(fecha)
                .conIdioma(idioma.trim()).conPelicula(pelicula).conSala(sala.clonar()).build();
        if (actual == null) { siguienteFuncion++; funciones.add(nueva); }
        else funciones.set(funciones.indexOf(actual), nueva);
        return nueva;
    }

    public void eliminarFuncion(Funcion funcion) {
        exigirAdministrador();
        if (!funciones.contains(funcion)) throw new IllegalArgumentException("Selecciona una función");
        for (Compra compra : compras) if (compra.getFuncion() == funcion) {
            throw new IllegalArgumentException("Esta función tiene historial: utiliza Cancelar función");
        }
        funciones.remove(funcion);
    }

    public int cancelarFuncion(Funcion funcion) {
        exigirAdministrador();
        if (!funciones.contains(funcion) || funcion.isCancelada()) throw new IllegalArgumentException("La función ya fue cancelada o no existe");
        List<Compra> afectadas = new ArrayList<>();
        for (Compra compra : compras) {
            if (compra.getFuncion() == funcion && !compra.estaCancelada()) {
                if (compra.getTotal() > 0) compra.getCliente().getTarjeta().validarReembolso(compra.getTotal(), compra.getFactura());
                afectadas.add(compra);
            }
        }
        // Validar también la suma de devoluciones a cada cliente antes de cambiar saldos.
        for (Cliente cliente : getClientes()) {
            long total = 0;
            for (Compra compra : afectadas) if (compra.getCliente() == cliente) total += compra.getTotal();
            if (total > Integer.MAX_VALUE - cliente.getTarjeta().getSaldo()) {
                throw new IllegalArgumentException("Un saldo no admite el reembolso completo");
            }
        }
        for (Compra compra : afectadas) servicio.cancelarPorCine(compra);
        funcion.cancelar();
        return afectadas.size();
    }

    public Producto guardarProducto(Producto actual, String nombre, int precio) {
        exigirAdministrador();
        if (actual != null && !confiteria.contains(actual)) throw new IllegalArgumentException("Producto no encontrado");
        if (actual == null) {
            Producto nuevo = new Producto(nombre, precio, 1);
            nuevo.actualizar(nombre, precio);
            confiteria.add(nuevo);
            return nuevo;
        }
        actual.actualizar(nombre, precio);
        return actual;
    }

    public Combo guardarCombo(Combo actual, String nombre, int porcentaje, List<ElementoProducto> productos) {
        exigirAdministrador();
        if (actual != null && !confiteria.contains(actual)) throw new IllegalArgumentException("Combo no encontrado");
        if (porcentaje < 0 || porcentaje > 100 || productos == null || productos.isEmpty()) {
            throw new IllegalArgumentException("Elige productos y un descuento de 0 a 100 %");
        }
        for (ElementoProducto producto : productos) {
            if (!(producto instanceof Producto) || !confiteria.contains(producto)) {
                throw new IllegalArgumentException("Selecciona productos del catálogo");
            }
        }
        if (actual == null) {
            Combo nuevo = new Combo(nombre, porcentaje / 100.0, productos);
            confiteria.add(nuevo);
            return nuevo;
        }
        actual.actualizar(nombre, porcentaje / 100.0, productos);
        return actual;
    }

    public void eliminarConfiteria(ElementoProducto elemento) {
        exigirAdministrador();
        if (!confiteria.contains(elemento)) throw new IllegalArgumentException("Selecciona un producto o combo");
        for (ElementoProducto otro : confiteria) {
            if (otro instanceof Combo && ((Combo) otro).getElementos().contains(elemento)) {
                throw new IllegalArgumentException("El producto pertenece a un combo; edita o elimina ese combo primero");
            }
        }
        confiteria.remove(elemento);
    }

    public Promocion guardarPromocion(Promocion actual, String nombre, int porcentaje,
                                      LocalDate desde, LocalDate hasta, boolean activa) {
        exigirAdministrador();
        if (actual != null && !promociones.contains(actual)) throw new IllegalArgumentException("Promoción no encontrada");
        if (actual == null) {
            Promocion nueva = new Promocion("PR-" + siguientePromocion, nombre, porcentaje, desde, hasta, activa);
            siguientePromocion++;
            promociones.add(nueva);
            return nueva;
        }
        actual.actualizar(nombre, porcentaje, desde, hasta, activa);
        return actual;
    }

    public void eliminarPromocion(Promocion promocion) {
        exigirAdministrador();
        if (!promociones.remove(promocion)) throw new IllegalArgumentException("Selecciona una promoción");
    }
}
