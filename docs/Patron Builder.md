# Patrón Builder — Funcion

**Responsable:** Andrés David Santafé · **Entrega 1**

## 1. Problema
Una función reúne varios datos: identificador, fecha y hora, idioma, sala y película. Según RN-001, no puede existir una función sin película, sala, fecha y hora.

Con un constructor de cinco parámetros es fácil confundir el orden de los datos, y la validación quedaría mezclada con la creación. Además, una vez creada, la función no debería cambiar: su sala o su película no pueden modificarse a mitad de camino.

## 2. Patrón
**Builder** (creacional): separa la construcción de un objeto complejo de su representación. El objeto se arma paso a paso con métodos que dicen qué dato se está asignando, y se crea al final con `build()`.

## 3. Justificación
- **Lectura clara:** `new Funcion.Builder().conSala(sala).conPelicula(pelicula)...build()` deja ver qué dato va en cada paso, sin depender del orden de los parámetros.
- **Validación en un solo punto:** `build()` revisa que estén todos los datos. Si falta alguno, lanza `IllegalStateException` y la función nunca se crea (RN-001, caso CP-01).
- **Inmutabilidad:** los atributos de `Funcion` son `final` y su constructor es privado. La única forma de crear una función es el Builder, y después no se puede modificar.
- **Interfaz fluida:** cada método `con...()` devuelve el mismo Builder, lo que permite encadenar las llamadas.

**Pregunta probable en la sustentación:** «Si todos los datos son obligatorios, ¿por qué no un constructor normal?». Respuesta: el Builder aporta la validación centralizada con mensajes claros, la inmutabilidad y la legibilidad. Además, deja preparado el crecimiento: datos opcionales como el formato 3D o los subtítulos se agregan con un método nuevo, sin cambiar las llamadas existentes. En el pensamiento computacional también se consideró aplicarlo a `Compra`, que tiene más datos opcionales; queda como alternativa para la siguiente entrega.

## 4. Clases
| Clase | Rol |
|---|---|
| `Funcion` | Producto: objeto inmutable con constructor privado `Funcion(Builder b)` |
| `Funcion.Builder` | Builder: clase estática anidada con los métodos `conIdFuncion`, `conFechaHora`, `conIdioma`, `conSala`, `conPelicula` y `build()` |
| `Sala`, `Pelicula` | Datos que recibe el Builder |

## 5. Diagrama
```mermaid
classDiagram
    class Funcion {
        -idFuncion : String
        -fechaHoraFuncion : LocalDateTime
        -idioma : String
        -salaFuncion : Sala
        -peliculaFuncion : Pelicula
        -Funcion(b : Builder)
    }
    class Builder {
        +conIdFuncion(f : String) Builder
        +conFechaHora(h : LocalDateTime) Builder
        +conIdioma(i : String) Builder
        +conSala(s : Sala) Builder
        +conPelicula(p : Pelicula) Builder
        +build() Funcion
    }
    Funcion +-- Builder : clase anidada
    Builder ..> Funcion : «crea»
    Funcion --> Sala
    Funcion --> Pelicula
```
También aparece en `docs/Diagrama de clases Entrega 1.drawio`.

## 6. Evidencia
Código: `src/main/java/com/example/cinemauq/model/Funcion.java`

Pruebas en `src/test/java/com/example/cinemauq/model/FuncionBuilderTest.java`:

| Prueba | Qué demuestra |
|---|---|
| `construyeUnaFuncionConTodosLosDatos` | Con todos los datos, la función se crea y conserva cada valor |
| `cp01CrearFuncionSinSalaSeRechaza` | Caso CP-01: sin sala, `build()` lanza `IllegalStateException` |
| `crearFuncionSinPeliculaSeRechaza` | Sin película, la función no se crea |
| `crearFuncionSinFechaSeRechaza` | Sin fecha y hora, la función no se crea |

```java
Funcion funcion = new Funcion.Builder()
        .conIdFuncion("F-001")
        .conFechaHora(LocalDateTime.of(2026, 10, 9, 19, 0))
        .conIdioma("Español")
        .conSala(sala.clonar())      // copia de la sala (Prototype)
        .conPelicula(pelicula)
        .build();
```
