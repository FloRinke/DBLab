--SELECT kunde.name, kunde.nr, auftrag.auftrnr, auftragsposten.posnr, teilestamm.bezeichnung, lieferung.liefnr, lieferant.name, lieferant.nr
SELECT DISTINCT kunde.name as kunde, kunde.nr as knr, lieferant.name as lieferant, lieferant.nr as lnr
FROM kunde
LEFT OUTER JOIN auftrag ON auftrag.kundnr = kunde.nr
LEFT OUTER JOIN auftragsposten ON auftragsposten.auftrnr = auftrag.auftrnr
LEFT OUTER JOIN teilestamm ON teilestamm.teilnr = auftragsposten.teilnr
LEFT OUTER JOIN lieferung ON lieferung.teilnr = teilestamm.teilnr
LEFT OUTER JOIN lieferant ON lieferant.nr = lieferung.liefnr
WHERE NOT lieferung.liefnr IS NULL OR auftrag.auftrnr IS NULL OR 0=(
	SELECT COUNT(l.liefnr)
	FROM kunde kd
	LEFT OUTER JOIN auftrag a ON a.kundnr = kd.nr
	LEFT OUTER JOIN auftragsposten ap ON ap.auftrnr = a.auftrnr
	LEFT OUTER JOIN teilestamm ts ON ts.teilnr = ap.teilnr
	LEFT OUTER JOIN lieferung l ON l.teilnr = ts.teilnr
	WHERE kd.nr = kunde.nr --AND ap.posnr=auftragsposten.posnr
)
ORDER BY kunde.name


SELECT COUNT(l.liefnr)
FROM kunde kd
LEFT OUTER JOIN auftrag a ON a.kundnr = kd.nr
LEFT OUTER JOIN auftragsposten ap ON ap.auftrnr = a.auftrnr
LEFT OUTER JOIN teilestamm ts ON ts.teilnr = ap.teilnr
LEFT OUTER JOIN lieferung l ON l.teilnr = ts.teilnr
WHERE kd.nr = 6 ---AND ap.posnr=41