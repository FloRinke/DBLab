DROP TABLE farbe;

CREATE TABLE farbe(nr INTEGER NOT NULL, name CHAR(10) NOT NULL, rot REAL DEFAULT 0, gruen REAL DEFAULT 0, blau REAL DEFAULT 0, PRIMARY KEY (nr), UNIQUE (name), CHECK ( rot BETWEEN 0.0 AND 1.0), CHECK ( gruen BETWEEN 0.0 AND 1.0), CHECK ( blau BETWEEN 0.0 AND 1.0));

SELECT DISTINCT farbe FROM teilestamm WHERE NOT farbe IS NULL;

SELECT teilnr, bezeichnung, farbnr
FROM teilestamm

SELECT * FROM farbe