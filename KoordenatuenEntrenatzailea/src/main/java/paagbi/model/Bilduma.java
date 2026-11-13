package paagbi.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

import java.util.ArrayList;
import java.util.List;

/**
 * Modelo jerárquico de los datos geográficos.
 *
 * <p>La estructura contiene países, regiones y formas geométricas.
 * Se utiliza tanto para leer y escribir XML con JAXB como para manejar
 * los datos importados o exportados en JSON.</p>
 */
@XmlRootElement(name = "bilduma")
@XmlAccessorType(XmlAccessType.FIELD)
public class Bilduma {

    @XmlElement(name = "herrialde")
    public List<Herrialde> herrialdeak = new ArrayList<>();

    /**
     * País que agrupa varias regiones.
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Herrialde {

        @XmlAttribute
        public String izena;

        @XmlElement(name = "eskualde")
        public List<Eskualde> eskualdeak = new ArrayList<>();

        public Herrialde() {
            // JAXB necesita un constructor sin argumentos.
        }

        public Herrialde(String izena) {
            this.izena = izena;
        }
    }

    /**
     * Región que contiene puntos, polilíneas o polígonos.
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Eskualde {

        @XmlAttribute
        public String izena;

        @XmlElements({
                @XmlElement(name = "puntu", type = Puntua.class),
                @XmlElement(name = "polilinea", type = Polilinea.class),
                @XmlElement(name = "poligono", type = Poligono.class)
        })
        public List<Forma> formak = new ArrayList<>();

        public Eskualde() {
            // JAXB necesita un constructor sin argumentos.
        }

        public Eskualde(String izena) {
            this.izena = izena;
        }
    }

    /**
     * Forma geométrica con identificador, nombre y coordenadas.
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Forma {

        @XmlAttribute
        public String id;

        @XmlAttribute
        public String izena;

        /** Coordenada única, utilizada por los puntos. */
        @XmlElement(name = "koordenatua")
        public Koordenatua koordenatua;

        /** Lista de vértices, utilizada por líneas y polígonos. */
        @XmlElementWrapper(name = "koordenatuak")
        @XmlElement(name = "koordenatua")
        public List<Koordenatua> koordenatuak = new ArrayList<>();

        /** Tipo de forma; este dato no se escribe como elemento XML. */
        @XmlTransient
        public String mota = "puntu";

        public Forma() {
            // JAXB necesita un constructor sin argumentos.
        }

        public Forma(String id, String izena, String mota) {
            this.id = id;
            this.izena = izena;
            this.mota = mota;
        }
    }

    /** Forma puntual. */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Puntua extends Forma {

        public Puntua() {
            mota = "puntu";
        }
    }

    /** Forma lineal compuesta por varios vértices. */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Polilinea extends Forma {

        public Polilinea() {
            mota = "polilinea";
        }
    }

    /** Polígono definido por una lista de vértices. */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Poligono extends Forma {

        public Poligono() {
            mota = "poligono";
        }
    }

    /**
     * Coordenada geográfica expresada como longitud y latitud.
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Koordenatua {

        @XmlAttribute
        public double longitudea;

        @XmlAttribute
        public double latitudea;

        public Koordenatua() {
            // JAXB necesita un constructor sin argumentos.
        }

        public Koordenatua(double longitudea, double latitudea) {
            this.longitudea = longitudea;
            this.latitudea = latitudea;
        }
    }
}


/** Anotación	Función
@XmlRootElement	Define el elemento raíz del XML.
@XmlAccessorType	Define cómo JAXB accede a los atributos y campos.
@XmlAttribute	Convierte un campo en atributo XML.
@XmlElement	Representa un campo como elemento XML.
@XmlElements	Permite diferentes tipos de elementos en una misma propiedad.
@XmlElementWrapper	Añade un elemento contenedor a una colección.
@XmlTransient	Excluye una propiedad de la serialización XML. */


/**List<String> lerroak = Files.readAllLines(Path.of("puntos.csv"));

for (String lerroa : lerroak) {
    System.out.println(lerroa);
}
     */