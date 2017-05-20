-- A.1.1
SELECT * FROM teilestamm;

-- A.1.2 Name enthält 'city'
SELECT teilnr, bezeichnung
FROM teilestamm
WHERE lower(bezeichnung) LIKE '%city%'

-- A.1.3 wie viele Kunden, Personal, Teilstamm
SELECT 'ANZ-Kunde', COUNT(*) FROM kunde
UNION
SELECT 'ANZ-Personal', COUNT(*) FROM personal
UNION
SELECT 'ANZ-Teile', COUNT(*) FROM teilestamm;

-- A.1.4 Auftragszeitraum
SELECT MIN(datum) AS von, MAX(datum) AS bis
FROM auftrag

-- A.2.1 AUFTRNR, DATUM aller Aufträge des Kunden Fahrrad Shop mit IN.
SELECT auftrnr, datum
FROM auftrag
WHERE kundnr IN (
	SELECT nr FROM kunde
	WHERE name = 'Fahrrad Shop'
)

-- A.2.2 genauso, mit SOME
SELECT auftrnr, datum
FROM auftrag
WHERE kundnr = SOME (
	SELECT nr FROM kunde
	WHERE name = 'Fahrrad Shop'
)

-- A.2.3 genauso, mit EXISTS
SELECT auftrnr, datum
FROM auftrag
WHERE EXISTS (
	SELECT nr FROM kunde
	WHERE kunde.nr=auftrag.kundnr AND name = 'Fahrrad Shop'
)

-- A.2.4 Wie viele Aufträge je Kunde, in welchem Zeitraum
SELECT kundnr, COUNT(kundnr) AS Anzahl, MIN(datum) AS von, MAX(datum) AS bis
FROM auftrag
GROUP BY kundnr
ORDER BY kundnr


-- A.2.5 Wie 2.4, aber nur Kunden mit einem einzigen Auftrag
SELECT kundnr, COUNT(kundnr) AS Anzahl, MIN(datum) AS von, MAX(datum) AS bis
FROM auftrag
GROUP BY kundnr
HAVING COUNT(kundnr) < 2

-- A.3.1 Liste Kdnr, Kunde, Auftragsnr
SELECT auftrag.kundnr AS Kundennummer, kunde.name AS Kunde, auftrag.auftrnr
FROM auftrag
INNER JOIN kunde ON kunde.nr = auftrag.kundnr
ORDER BY auftrag.kundnr, auftrag.auftrnr

-- A.3.2 Wie 3.1, mit Mitarbeiter
SELECT auftrag.kundnr AS Kundennummer, kunde.name AS Kunde, auftrag.auftrnr, personal.name AS MITARBEITER
FROM auftrag
INNER JOIN kunde ON kunde.nr = auftrag.kundnr
INNER JOIN personal ON personal.persnr = auftrag.persnr
ORDER BY auftrag.kundnr, auftrag.auftrnr

-- A.3.3 Wie 3.2, mit Chef
SELECT auftrag.kundnr AS Kundennummer, kunde.name AS Kunde, auftrag.auftrnr, personal.name AS MITARBEITER, chefs.name AS Chef
FROM auftrag
INNER JOIN kunde ON kunde.nr = auftrag.kundnr
INNER JOIN personal ON personal.persnr = auftrag.persnr
INNER JOIN personal AS chefs ON chefs.persnr = personal.vorgesetzt
ORDER BY auftrag.kundnr, auftrag.auftrnr

-- A.3.4 Wie 3.3, mit Anzahl Auftragsposten
SELECT auftrag.kundnr AS Kundennummer, kunde.name AS Kunde, auftrag.auftrnr, 
		personal.name AS MITARBEITER, chefs.name AS Chef,
		SUM(auftrag.auftrnr) AS Posten
FROM auftrag
INNER JOIN kunde ON kunde.nr = auftrag.kundnr
INNER JOIN personal ON personal.persnr = auftrag.persnr
INNER JOIN personal AS chefs ON chefs.persnr = personal.vorgesetzt
INNER JOIN auftragsposten ON auftragsposten.auftrnr = auftrag.auftrnr
GROUP BY auftrag.auftrnr, kunde.name, personal.name, chefs.name

-- A.3.5 Wie 3.4, mit Min/Max-Preis
SELECT auftrag.kundnr AS Kundennummer, kunde.name AS Kunde, auftrag.auftrnr, 
		personal.name AS MITARBEITER, chefs.name AS Chef,
		SUM(auftrag.auftrnr) AS Posten,
		MIN(teilestamm.preis), MAX(teilestamm.preis)
FROM auftrag
INNER JOIN kunde ON kunde.nr = auftrag.kundnr
INNER JOIN personal ON personal.persnr = auftrag.persnr
INNER JOIN personal AS chefs ON chefs.persnr = personal.vorgesetzt
INNER JOIN auftragsposten ON auftragsposten.auftrnr = auftrag.auftrnr
INNER JOIN teilestamm ON teilestamm.teilnr = auftragsposten.teilnr
GROUP BY auftrag.auftrnr, kunde.name, personal.name, chefs.name, auftragsposten.teilnr
ORDER BY auftrag.kundnr, auftrag.auftrnr

-- A.4.1
BEGIN TRANSACTION
	SELECT COUNT(*) FROM lieferung;
	DELETE FROM lieferant; --CONSTRAINT fk_lieferant  REFERENCES lieferant  ON DELETE CASCADE,
	SELECT COUNT(*) FROM lieferung;
ROLLBACK
SELECT COUNT(*) FROM lieferung;