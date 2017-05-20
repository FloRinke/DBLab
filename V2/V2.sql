--Aufgabe 2

-- A.2.1 Niedrigster Nettopreis
DROP VIEW v500009;
CREATE VIEW v500009 AS
SELECT teilestamm.bezeichnung, lieferant.name AS Lieferant
FROM teilestamm
JOIN lieferung ON lieferung.teilnr = teilestamm.teilnr
JOIN lieferant ON lieferant.nr = lieferung.liefnr
WHERE teilestamm.teilnr = 500009 AND lieferung.nettopreis = (SELECT MIN(nettopreis) FROM lieferung WHERE teilnr=teilestamm.teilnr);
SELECT * FROM v500009;

-- A.2.2
DROP VIEW bestelltT;
CREATE VIEW bestelltT AS
SELECT teilnr, SUM(anzahl) AS bestellt
FROM auftragsposten
GROUP BY teilnr;
SELECT * FROM bestelltT;

DROP VIEW bestandT;
CREATE VIEW bestandT AS
SELECT auftragsposten.teilnr, SUM(bestand) AS bestand
FROM auftragsposten
INNER JOIN  lager ON lager.teilnr = auftragsposten.teilnr
GROUP BY auftragsposten.teilnr
UNION 
SELECT auftragsposten.teilnr, 0 AS bestand
FROM auftragsposten
LEFT OUTER JOIN lager ON lager.teilnr = auftragsposten.teilnr
WHERE lager.bestand IS NULL
GROUP BY auftragsposten.teilnr;
SELECT * FROM bestandT;

SELECT teilestamm.Bezeichnung, bestelltT.teilnr, (bestelltT.bestellt - bestandT.bestand) AS fehlend
FROM bestelltT
JOIN teilestamm ON teilestamm.teilnr = bestelltT.teilnr
JOIN bestandT ON bestandt.teilnr = bestelltT.teilnr
WHERE bestelltT.bestellt > bestandT.bestand
ORDER BY teilestamm.bezeichnung

-- A.2.3 a) EXISTS
SELECT DISTINCT name 
FROM personal
WHERE EXISTS (
	SELECT * 
	FROM personal p --oracle mag hier kein AS
	WHERE p.vorgesetzt = personal.persnr
)

-- A.2.3 b) JOIN
SELECT DISTINCT vg.name 
FROM personal p  --oracle mag hier kein AS
JOIN personal vg ON vg.persnr=p.vorgesetzt --oracle mag hier kein AS

-- A.2.3 c) IN
SELECT DISTINCT name
FROM personal
WHERE persnr IN (
	SELECT vorgesetzt FROM personal
)

-- A.2.3 d) ANY
SELECT DISTINCT name
FROM personal 
WHERE persnr = ANY (
	SELECT vorgesetzt
	FROM personal
	--WHERE vorgesetzt IS NOT NULL
)

-- A.2.4 
DROP VIEW orders_october;
CREATE VIEW orders_october AS
SELECT kunde.nr AS KdNr, kunde.name AS KdName, personal.name AS KdBetreuer, vg.name AS KundenbetreuerChef, SUM(auftragsposten.gesamtpreis) AS Preis, 0.1*SUM(auftragsposten.gesamtpreis) AS Rabatt 
FROM auftrag 
JOIN auftragsposten on auftragsposten.auftrnr = auftrag.auftrnr 
JOIN kunde ON kunde.nr = auftrag.kundnr 
JOIN personal ON personal.persnr = auftrag.persnr 
LEFT OUTER JOIN personal vg ON vg.persnr = personal.vorgesetzt --oracle mag hier kein AS
WHERE auftrag.datum BETWEEN DATE '2008-10-01' AND DATE '2008-11-01'
GROUP BY kunde.nr, kunde.name, personal.name, vg.name 

SELECT * FROM orders_october
WHERE Preis = (SELECT MAX(Preis) FROM orders_october)

