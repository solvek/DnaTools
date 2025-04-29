r'\s*\[.*\]$'

# Скрапінг збігів із сайту MyHeritage

Використовується розширення chrome [Web Scrapper](https://chromewebstore.google.com/detail/web-scraper-free-web-scra/jnhgnonknehpejjnehehllkliplmbmhn?hl=en)

## Створити CSV файл

Ім'я файлу: `matches.csv`

Заголовок має бути такого виду:

'''
name	shared_dna	match_shared	match_name
'''

Колонки розділяються табами

## Перейти на сторінку зі збігами
## Для кожного збігу в порядку зменшення спільного ДНК

### Увімкнути сортування "Спільна ДНК з вами"

### Відобразати всі спільні збіги з принаймні 20 сМ

### Натиснути F12 (відобразити інструментарій розробника)

### Перейти в закладку Web Scrapper

### Імпортувати sitemap

#### Create Sitemap/Import Sitemap

#### Ввести Sitemap name, наприклад MyHeritageMatches

#### Ввести код мапи

'''
{"_id":"MyHeritageMatches","startUrl":["https://www.myheritage.com.ua/"],"selectors":[{"id":"name","parentSelectors":["_root"],"type":"SelectorText","selector":".match_details_section [data-automations='MatchProfileDetailsOther'] h5","multiple":false,"regex":""},{"id":"shared_dna","parentSelectors":["_root"],"type":"SelectorText","selector":".match_quality_value_inner span[data-automations]","multiple":false,"regex":""},{"id":"match","parentSelectors":["_root"],"type":"SelectorElement","selector":"div.card_row div.shared_match_profile_content_wrapper","multiple":true},{"id":"match_name","parentSelectors":["match"],"type":"SelectorText","selector":"a","multiple":true,"regex":""},{"id":"match_shared","parentSelectors":["match"],"type":"SelectorText","selector":".other_dna_match_side span span","multiple":false,"regex":""}]}
'''

#### Натиснути Import Sitemap