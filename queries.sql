-- Населені пункти
SELECT
	IFNULL(r.name, d.region) AS region,
	IFNULL(c.name, d.county) as county,
	city,
	score,
	d.region as region_id
FROM distribution d
LEFT JOIN regions r ON d.region=r.id
LEFT JOIN counties c ON d.county=c.id
WHERE county > 0
ORDER by score DESC

-- Області
SELECT
    IFNULL(r.name, d.region) as region,
    d.score
FROM
(SELECT region, sum(score) as score
FROM distribution d
GROUP BY region
ORDER BY sum(score) DESC) as d
LEFT JOIN regions as r on d.region = r.id

-- Райони
SELECT
    IFNULL(r.name, d.region) as region,
    IFNULL(c.name, d.county) as county,
    d.score
FROM
(SELECT county, region, sum(score) as score
FROM distribution d
WHERE county > 0
GROUP BY county, region
ORDER BY sum(score) DESC) as d
LEFT JOIN counties as c ON d.county = c.id
LEFT JOIN regions as r on d.region = r.id

-- Райони невизначені
SELECT
	r.name,
    d.county,
    SUM(d.score) as score
FROM distribution d
LEFT JOIN counties c ON d.county=c.id
INNER JOIN regions r ON d.region=r.id
WHERE d.county > 0 AND c.id IS NULL
GROUP BY r.name, d.county
ORDER BY SUM(d.score) DESC

CREATE TABLE "regions" (
	"id"	INTEGER NOT NULL,
	"name"	TEXT NOT NULL,
	PRIMARY KEY("id" AUTOINCREMENT)
);

CREATE TABLE "counties" (
	"id"	INTEGER NOT NULL,
	"region"	INTEGER NOT NULL,
	"name"	TEXT NOT NULL,
	PRIMARY KEY("id" AUTOINCREMENT)
);


=CONCAT("insert into counties values (",E3,",",B3,", '",C3,"/",A3,"');")