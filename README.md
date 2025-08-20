# java-filmorate

![Схема БД] (images/Filmorate_scheme.png)

Примеры запросов:

### Топ 10 фильмов по лайкам
```SELECT 
    f.name
FROM films f
LEFT JOIN film_likes fl ON f.id = fl.film_id
GROUP BY f.id, f.name
ORDER BY COUNT(fl.user_id) DESC
LIMIT 10;```

### Запрос фильмов без лайков
```SELECT 
    f.name
FROM films f
LEFT JOIN film_likes fl ON f.id = fl.film_id
WHERE fl.user_id IS NULL;```

### Запрос всех фильмов с условным жанром с id=2
```SELECT 
    f.name
FROM films f
JOIN film_genres fg ON f.id = fg.film_id
WHERE fg.genre_id = 2;```

### Запрос пользователей которые поставили лайк условному фильму с id=12
```SELECT 
    u.id,
    u.name,
    u.login,
    u.email
FROM users u
JOIN film_likes fl ON u.id = fl.user_id
WHERE fl.film_id = 12;```