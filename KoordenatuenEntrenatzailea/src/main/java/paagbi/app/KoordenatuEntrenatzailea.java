package paagbi.app;

import paagbi.model.Bilduma;
import paagbi.service.FitxategiZerbitzua;
import paagbi.service.GeometriaZerbitzua;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Koordenatuen ikaskuntza-menu nagusia.
 */
public class KoordenatuEntrenatzailea {

    private static final Scanner SARRERA = new Scanner(System.in);
    private static final Path XML_FITXATEGIA = Path.of("src/main/resources/datuak/koordenatuak.xml");

    public static void main(String[] args) {
        FitxategiZerbitzua fitxategiZerbitzua = new FitxategiZerbitzua();
        GeometriaZerbitzua geometriaZerbitzua = new GeometriaZerbitzua();
        Bilduma bilduma;

        try {
            bilduma = fitxategiZerbitzua.xmlIrakurri(XML_FITXATEGIA);
        } catch (Exception e) {
            System.err.println("Ezin izan da XML irakurri: " + e.getMessage());
            return;
        }

        boolean amaitu = false;

        while (!amaitu) {
            erakutsiMenua();
            String aukera = SARRERA.nextLine().trim();

            try {
                switch (aukera) {
                    case "1" -> erakutsiFormak(bilduma);
                    case "2" -> kalkulatuDistantzia(geometriaZerbitzua);
                    case "3" -> kalkulatuAzalera(bilduma, geometriaZerbitzua);
                    case "4" -> bilatuPuntuHurbilena(bilduma, geometriaZerbitzua);
                    case "5" -> esportatuJson(bilduma, fitxategiZerbitzua);
                    case "6" -> bilduma = inportatuJson(fitxategiZerbitzua);
                    case "0" -> amaitu = true;
                    default -> System.out.println("Aukera baliogabea.");
                }
            } catch (Exception e) {
                System.out.println("Eragiketa ezin izan da egin: " + e.getMessage());
            }
        }
    }

    private static void erakutsiMenua() {
        System.out.println("""
                
                === Koordenatuen entrenatzailea ===
                1. Formak erakutsi
                2. Bi punturen arteko distantzia
                3. Poligono baten azalera
                4. Puntu hurbilena bilatu
                5. JSONera esportatu
                6. JSONtik inportatu
                0. Irten
                """);
        System.out.print("Aukera: ");
    }

    private static void erakutsiFormak(Bilduma bilduma) {
        /**Por cada elemento de bilduma.herrialdeak, guarda ese elemento temporalmente en la variable herrialde */
        for (Bilduma.Herrialde herrialde : bilduma.herrialdeak) {
            for (Bilduma.Eskualde eskualde : herrialde.eskualdeak) {
                for (Bilduma.Forma forma : eskualde.formak) {
                    System.out.printf(
                            "%s / %s - %s [%s] (%s)%n",
                            herrialde.izena,
                            eskualde.izena,
                            forma.izena,
                            forma.id,
                            forma.mota
                    );
                }
            }
        }
    }

    private static void kalkulatuDistantzia(GeometriaZerbitzua geometriaZerbitzua) {
        System.out.println("Sartu lehen puntua:");
        Bilduma.Koordenatua lehenPuntua = eskatuPuntua();

        System.out.println("Sartu bigarren puntua:");
        Bilduma.Koordenatua bigarrenPuntua = eskatuPuntua();

        double distantzia = geometriaZerbitzua.distantzia(lehenPuntua, bigarrenPuntua);
        System.out.printf(Locale.ROOT, "Distantzia: %.5f unitate%n", distantzia);
    }

    private static void kalkulatuAzalera(
            Bilduma bilduma,
            GeometriaZerbitzua geometriaZerbitzua
    ) {
        Bilduma.Forma poligonoa = aukeratuPoligonoa(bilduma);
        double azalera = geometriaZerbitzua.azalera(poligonoa.koordenatuak);

        System.out.printf(Locale.ROOT, "Azalera: %.5f unitate karratu%n", azalera);
    }

    private static void bilatuPuntuHurbilena(
            Bilduma bilduma,
            GeometriaZerbitzua geometriaZerbitzua
    ) {
        Bilduma.Koordenatua puntua = eskatuPuntua();
        Bilduma.Forma hurbilena = geometriaZerbitzua.hurbilena(puntua, bilduma);

        if (hurbilena == null) {
            System.out.println("Ez dago punturik.");
            return;
        }

        System.out.printf("Puntu hurbilena: %s (%s)%n", hurbilena.izena, hurbilena.id);
    }
    

    private static void esportatuJson(
            Bilduma bilduma,
            FitxategiZerbitzua fitxategiZerbitzua
    ) throws Exception {
        System.out.print("JSON fitxategiaren bidea [koordenatuak.json]: ");
        String bidea = SARRERA.nextLine().trim();

        if (bidea.isEmpty()) {
            bidea = "koordenatuak.json";
        }

        fitxategiZerbitzua.jsonIdatzi(bilduma, Path.of(bidea));
        System.out.println("JSON fitxategia esportatu da.");
    }

    private static Bilduma inportatuJson(
            FitxategiZerbitzua fitxategiZerbitzua
    ) throws Exception {
        System.out.print("Inportatu beharreko JSON fitxategiaren bidea: ");
        Path bidea = Path.of(SARRERA.nextLine().trim());

        Bilduma bilduma = fitxategiZerbitzua.jsonIrakurri(bidea);
        fitxategiZerbitzua.xmlIdatzi(bilduma, XML_FITXATEGIA);

        System.out.println("JSON datuak inportatu eta XML fitxategia eguneratu da.");
        return bilduma;
    }

    private static Bilduma.Koordenatua eskatuPuntua() {
        double longitudea = eskatuZenbakia(
                "Longitudea (-180 eta 180 artean): ",
                -180,
                180
        );
        double latitudea = eskatuZenbakia(
                "Latitudea (-90 eta 90 artean): ",
                -90,
                90
        );

        return new Bilduma.Koordenatua(longitudea, latitudea);
    }

    private static double eskatuZenbakia(String mezua, double minimoa, double maximoa) {
        while (true) {
            System.out.print(mezua);
            String sarrera = SARRERA.nextLine().trim().replace(',', '.');

            try {
                double zenbakia = Double.parseDouble(sarrera);

                if (Double.isFinite(zenbakia)
                        && zenbakia >= minimoa
                        && zenbakia <= maximoa) {
                    return zenbakia;
                }
            } catch (NumberFormatException ignored) {
                // Sarrera ez bada zenbakizkoa, berriro eskatuko da.
            }

            System.out.println("Sartu zenbaki baliodun bat, baimendutako tartean.");
        }
    }

    private static Bilduma.Forma aukeratuPoligonoa(Bilduma bilduma) {
        List<Bilduma.Forma> poligonoak = bilduma.herrialdeak.stream() /**Coge la lista de países y empieza a recorrerla mediante Streams. */
                .flatMap(herrialde -> herrialde.eskualdeak.stream())
                .flatMap(eskualde -> eskualde.formak.stream())
                .filter(forma -> forma.koordenatuak != null
                        && forma.koordenatuak.size() >= 3)
                .toList();

        if (poligonoak.isEmpty()) {
            throw new IllegalStateException("Ez dago poligono erabilgarririk.");
        }

        System.out.println("Aukeratu poligono bat:");
        for (int i = 0; i < poligonoak.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, poligonoak.get(i).izena);
        }

        System.out.print("Aukera: ");
        int indizea = Integer.parseInt(SARRERA.nextLine().trim()) - 1;

        if (indizea < 0 || indizea >= poligonoak.size()) {
            throw new IllegalArgumentException("Aukera baliogabea.");
        }

        return poligonoak.get(indizea);
    }
}
