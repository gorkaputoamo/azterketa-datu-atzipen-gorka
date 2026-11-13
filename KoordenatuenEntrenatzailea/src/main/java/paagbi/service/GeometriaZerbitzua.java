package paagbi.service;

import paagbi.model.Bilduma;

import java.util.Comparator;
import java.util.List;

/**
 * Koordenatuen kalkulu geometriko arruntak egiten ditu.
 *
 * <p>Kalkuluak plano kartesiarrean egiten dira, ariketa didaktikoetarako.</p>
 */
public class GeometriaZerbitzua {

    /**
     * Bi punturen arteko distantzia euklidearra kalkulatzen du.
     *
     * @param lehenPuntua lehen koordenatua
     * @param bigarrenPuntua bigarren koordenatua
     * @return puntuen arteko distantzia
     */
    public static double distantzia(
            Bilduma.Koordenatua lehenPuntua,
            Bilduma.Koordenatua bigarrenPuntua
    ) {
        double longitudeenDiferentzia =
                lehenPuntua.longitudea - bigarrenPuntua.longitudea;
        double latitudeenDiferentzia =
                lehenPuntua.latitudea - bigarrenPuntua.latitudea;

        return Math.hypot(longitudeenDiferentzia, latitudeenDiferentzia);
    }

    /**
     * Poligono baten azalera kalkulatzen du, Shoelace formularen bidez.
     *
     * @param erpinak poligonoaren erpinak
     * @return poligonoaren azalera
     * @throws IllegalArgumentException poligonoak hiru erpin baino gutxiago baditu
     */
    public static double azalera(List<Bilduma.Koordenatua> erpinak) {
        if (erpinak == null || erpinak.size() < 3) {
            throw new IllegalArgumentException(
                    "Poligonoak gutxienez 3 erpin behar ditu."
            );
        }

        double batura = 0;

        for (int i = 0; i < erpinak.size(); i++) {
            Bilduma.Koordenatua oraingoErpina = erpinak.get(i);
            Bilduma.Koordenatua hurrengoErpina =
                    erpinak.get((i + 1) % erpinak.size());

            batura += oraingoErpina.longitudea * hurrengoErpina.latitudea
                    - hurrengoErpina.longitudea * oraingoErpina.latitudea;
        }

        return Math.abs(batura) / 2;
    }

    /**
     * Puntu bat poligono baten barruan dagoen egiaztatzen du.
     *
     * @param puntua egiaztatu beharreko puntua
     * @param erpinak poligonoaren erpinak
     * @return {@code true}, puntua poligonoaren barruan badago
     */
    public static boolean barruan(
            Bilduma.Koordenatua puntua,
            List<Bilduma.Koordenatua> erpinak
    ) {
        if (puntua == null || erpinak == null || erpinak.size() < 3) {
            return false;
        }

        boolean barruanDago = false;

        for (int i = 0, aurrekoa = erpinak.size() - 1; i < erpinak.size(); aurrekoa = i++) {

            Bilduma.Koordenatua oraingoErpina = erpinak.get(i);
            Bilduma.Koordenatua aurrekoErpina = erpinak.get(aurrekoa);

            boolean ertzakLatitudeaGurutzatzenDu =
                    (oraingoErpina.latitudea > puntua.latitudea) != (aurrekoErpina.latitudea > puntua.latitudea);

            if (ertzakLatitudeaGurutzatzenDu) {
                double ebakiduraLongitudea = (aurrekoErpina.longitudea - oraingoErpina.longitudea)
                                * (puntua.latitudea - oraingoErpina.latitudea)
                                / (aurrekoErpina.latitudea - oraingoErpina.latitudea)
                                + oraingoErpina.longitudea;

                if (puntua.longitudea < ebakiduraLongitudea) {
                    barruanDago = !barruanDago;
                }
            }
        }

        return barruanDago;
    }

    /**
     * Bilduma bateko puntu hurbilena aurkitzen du.
     *
     * @param puntua erreferentzia-puntua
     * @param bilduma bilatu beharreko datuen bilduma
     * @return puntu hurbilena duen forma, edo {@code null} puntu erabilgarririk ez badago
     */
    public static Bilduma.Forma hurbilena(
            Bilduma.Koordenatua puntua,
            Bilduma bilduma
    ) {
        if (puntua == null || bilduma == null || bilduma.herrialdeak == null) {
            return null;
        }

        return bilduma.herrialdeak.stream()
                .filter(herrialdea -> herrialdea.eskualdeak != null)
                .flatMap(herrialdea -> herrialdea.eskualdeak.stream())
                .filter(eskualdea -> eskualdea.formak != null)
                .flatMap(eskualdea -> eskualdea.formak.stream()) //** juntar todas las formas */
                .filter(forma -> forma.koordenatua != null) //** kordenatu bakar bat */
                .min(Comparator.comparingDouble(
                        forma -> distantzia(puntua, forma.koordenatua)
                ))
                .orElse(null);
    }
}