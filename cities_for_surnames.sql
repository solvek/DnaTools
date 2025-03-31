-- Скільки прізвищ зустрічається в селах
CREATE TABLE city_surnames AS
SELECT
	county,
	city,
	0 as population,
	0.0 as spm,
	COUNT(*) as surnames_cnt
FROM 
	(SELECT
		P."Районнародження" AS county,
		P."Названаселеногопунктународження(Україна)" as city,
		P."Прізвищє" as surname,
		COUNT(*) AS CNT
	FROM
		"people" P
	JOIN
		"surnames" S ON P."Прізвищє"=S.surname
	WHERE
		P."Стать"=1
		AND P."Областьнародження"=13
		AND P."Районнародження" IS NOT NULL
	GROUP BY 
		P."Районнародження",
		P."Названаселеногопунктународження(Україна)",
		P."Прізвищє")
GROUP BY
	county,
	city
ORDER BY surnames_cnt DESC

-- Скільки всього чоловіків народились у вибраних селах
-- Занести кількість чоловічого населення народженого в населених пунктах
UPDATE city_surnames
SET population = (
	SELECT population 
	FROM cities 
	WHERE 
		city_surnames.county=cities.county 
		AND city_surnames.city=cities.city)
		
-- Скільки прізвищ в селі на 1000 мешканців
UPDATE city_surnames
SET spm = 1000.0*surnames_cnt/population

-- Вибираємо села з найбільшою вагою прізвищ
SELECT
	C.name as "county_name",
	S.*
FROM city_surnames S
JOIN counties C ON S.county = C.id
WHERE surnames_cnt > 2
ORDER BY spm DESC

-- Найпоширеніші прізвища у певному населеному пункті
SELECT
	"Прізвищє",
	COUNT(*) AS cnt
FROM
	"people"
WHERE
	"Районнародження"=27
	AND "Названаселеногопунктународження(Україна)"="ГАМАЛІЇВКА"
	AND "Стать"=1
GROUP BY "Прізвищє"
ORDER BY cnt DESC

-- Занести кількість чоловічого населення народженого в населених пунктах
CREATE TABLE cities AS
SELECT
	county,
	city,
	COUNT(*) AS population
FROM
	city_surnames C
	JOIN "people" P ON "Районнародження"=county AND "Названаселеногопунктународження(Україна)"=city
WHERE
	"Стать"=1
GROUP BY 
	county,
	city
