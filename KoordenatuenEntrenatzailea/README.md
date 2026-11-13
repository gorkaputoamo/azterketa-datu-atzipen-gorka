# Koordenatuen entrenatzailea

Java 17ko Maven kontsola-aplikazioa, aurreko `KokalekuKudeatzailea` proiektuko leku/koordenatuen eredutik abiatuta.

## Abiaraztea
Java 17 eta Maven instalatuta: `mvn clean compile` eta ondoren `mvn exec:java`.

## Edukia
- XML datuek herrialdea → eskualdea → forma mailak dituzte (gutxienez bi anidazio maila).
- Puntuak, polilineak eta poligonoak modelatzen dira. Koordenatuak longitude/latitude ordenan dira.
- Menuak distantzia euklidearra, azalera (Shoelace), punturik hurbilena eta poligono barrukoa lantzen ditu.
- JAXB XMLrako; Jackson JSON inportazio/esportaziorako.
- `model`, `service` eta `app` paketeek eredua, iraunkortasuna/kalkuluak eta interfazea bereizten dituzte.

Kalkuluak hezkuntza-helburuko plano kartesiarrekoak dira, ez distantzia geodesikoak. Sarrerako koordenatuak balidatzen dira.
