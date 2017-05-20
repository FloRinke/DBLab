-- Aufgabe 1 - Abfragen
-- A.1.1
--Auftrag 2: Kunde 3, Personal 5
--Kunde 3: Maier Ingrid
--Personal 5: Johanna Köster
--Vorgesetzter: Maria Forster
SELECT kunde.name AS kunde, personal.name AS mitarbeiter, vg.name AS vorgesetzter
FROM auftrag
INNER JOIN kunde ON kunde.nr = auftrag.kundnr
INNER JOIN personal ON personal.persnr = auftrag.persnr
INNER JOIN personal vg ON vg.persnr = personal.vorgesetzt
WHERE auftrag.auftrnr = 2;

-- A.1.2 Teile im lager
SELECT lager.teilnr AS teilnr --, teilestamm.bezeichnung, lager.bestand
FROM lager
--INNER JOIN teilestamm ON teilestamm.teilnr = lager.teilnr
WHERE lager.bestand > 0
ORDER BY lager.bestand ASC

-- A.1.3 gelieferte Teile
-- Annahme: ausstehende Lieferungen
SELECT DISTINCT teilnr
FROM lieferung
ORDER BY teilnr DESC

-- A.1.4 teure Teile
SELECT teilnr AS Teilenummer, bezeichnung AS Bezeichnung, preis AS Bruttopreis
FROM teilestamm
WHERE preis > 30.00

-- A.1.5 Häufig benötigte Einzelteile
SELECT teilestruktur.einzelteilnr AS Teilenummer --, teilestruktur.anzahl
FROM teilestruktur
--JOIN teilestamm ON teilestamm.teilnr = teilestruktur.einzelteilnr
WHERE teilestruktur.oberteilnr = 300001 AND teilestruktur.anzahl > 100

-- A.1.6 blaue Teile in Dollar
SELECT bezeichnung, preis, preis*1.0665 AS PREIS_IN_DOLLAR
FROM teilestamm
WHERE farbe = 'blau'
ORDER BY teilnr

-- A.1.7
SELECT DISTINCT teilestamm.bezeichnung --, teilestamm.teilnr, teilestruktur.oberteilnr, teilestruktur.EINZELTEILNR
FROM auftragsposten
JOIN teilestamm ON teilestamm.teilnr = auftragsposten.teilnr
JOIN lager ON lager.teilnr = auftragsposten.teilnr
JOIN teilestruktur ON teilestruktur.oberteilnr = auftragsposten.teilnr -- hier wird die aufgabenbedingung abgefragt
WHERE lager.bestand > 0
ORDER BY teilestamm.bezeichnung
