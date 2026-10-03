package com.example.cinemauq.model;

import java.time.LocalDateTime;

public class Funcion {
    private final String idFuncion;
    private final LocalDateTime fechaHoraFuncion;
    private final String idioma;

    private final Sala salaFuncion;
    private final Pelicula peliculaFuncion;

    private Funcion(Builder b) {
        this.idFuncion = b.idFuncion;
        this.fechaHoraFuncion = b.fechaHoraFuncion;
        this.idioma = b.idioma;
        this.salaFuncion = b.salaFuncion;
        this.peliculaFuncion = b.peliculaFuncion;
    }

    public static class Builder{
        private  String idFuncion;
        private  LocalDateTime fechaHoraFuncion;
        private  String idioma;

        private  Sala salaFuncion;
        private  Pelicula peliculaFuncion;

        public Builder conIdFuncion(String f){
            this.idFuncion=f;
            return this;
        }
        public Builder conFechaHora(LocalDateTime h){
            this.fechaHoraFuncion=h;
            return this;
        }
        public Builder conIdioma(String i){
            this.idioma=i;
            return this;
        }
        public Builder conSala(Sala s){
            this.salaFuncion=s;
            return this;
        }
        public Builder conPelicula(Pelicula p){
            this.peliculaFuncion=p;
            return this;
        }

        public Funcion build(){
            if(idFuncion==null){
                throw new IllegalStateException("El identificador es incorrecto");
            }
            if(fechaHoraFuncion==null){
                throw new IllegalStateException("Hay un error con la fehca de la funcion");
            }
            if(idioma==null){
                throw new IllegalStateException("El idioma es incorrecto");
            }
            if(salaFuncion==null){
                throw new IllegalStateException("La sala es incorrecta");
            }
            if(peliculaFuncion==null){
                throw new IllegalStateException("hay un error en la asignacion de la pelicula");
            }
            return new Funcion(this);
        }
    }

    public String getIdFuncion() {
        return idFuncion;
    }

    public LocalDateTime getFechaHoraFuncion() {
        return fechaHoraFuncion;
    }

    public String getIdioma() {
        return idioma;
    }

    public Sala getSalaFuncion() {
        return salaFuncion;
    }

    public Pelicula getPeliculaFuncion() {
        return peliculaFuncion;
    }
}
