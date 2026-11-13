package paagbi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import paagbi.model.Bilduma;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * XML eta JSON fitxategiak irakurtzeko eta idazteko zerbitzua.
 *
 * <p>XML fitxategien lotura JAXB bidez egiten da, eta JSON fitxategiena
 * Jackson liburutegia erabiliz.</p>
 */
public class FitxategiZerbitzua {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Bilduma XML fitxategi batetik irakurtzen du.
     *
     * @param fitxategia irakurri beharreko XML fitxategiaren bidea
     * @return XML fitxategian gordetako bilduma
     * @throws Exception fitxategia irakurri edo XMLa prozesatu ezin bada
     */
    public Bilduma xmlIrakurri(Path fitxategia) throws Exception {
        JAXBContext testuingurua = JAXBContext.newInstance(Bilduma.class);

        return (Bilduma) testuingurua
                .createUnmarshaller()
                .unmarshal(fitxategia.toFile());
    }

    /**
     * Bilduma XML fitxategi batean gordetzen du.
     *
     * @param bilduma gorde beharreko datuak
     * @param fitxategia XML fitxategiaren bidea
     * @throws Exception fitxategia sortu edo XMLa idatzi ezin bada
     */
    public void xmlIdatzi(Bilduma bilduma, Path fitxategia) throws Exception {
        sortuGurasoDirektorioa(fitxategia);

        JAXBContext testuingurua = JAXBContext.newInstance(Bilduma.class);
        Marshaller marshaller = testuingurua.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(bilduma, fitxategia.toFile());
    }

    /**
     * Bilduma JSON fitxategi batean esportatzen du.
     *
     * @param bilduma esportatu beharreko datuak
     * @param fitxategia JSON fitxategiaren bidea
     * @throws Exception fitxategia sortu edo JSONa idatzi ezin bada
     */
    public void jsonIdatzi(Bilduma bilduma, Path fitxategia) throws Exception {
        sortuGurasoDirektorioa(fitxategia);

        objectMapper
                .writerWithDefaultPrettyPrinter() /**configura la escritura */
                .writeValue(fitxategia.toFile(), bilduma); /**convierte el objeto Java en JSON */
    }

    /**
     * Bilduma JSON fitxategi batetik inportatzen du.
     *
     * @param fitxategia irakurri beharreko JSON fitxategiaren bidea
     * @return JSON fitxategian gordetako bilduma
     * @throws Exception fitxategia irakurri edo JSONa prozesatu ezin bada
     */
    public Bilduma jsonIrakurri(Path fitxategia) throws Exception {
        return objectMapper.readValue(fitxategia.toFile(), Bilduma.class);
    }

    /**
     * Fitxategiaren guraso-direktorioa sortzen du, oraindik ez badago.
     *
     * @param fitxategia sortu edo idatzi beharreko fitxategiaren bidea
     * @throws Exception direktorioa sortu ezin bada
     */
    private void sortuGurasoDirektorioa(Path fitxategia) throws Exception {
        Path gurasoDirektorioa = fitxategia.toAbsolutePath().getParent();

        if (gurasoDirektorioa != null) {
            Files.createDirectories(gurasoDirektorioa);
        }
    }
}
