MERGE INTO FILM_RATING (RATING_ID, MPAA_FILM_RATING)
    VALUES (1, 'G'),
           (2, 'PG'),
           (3, 'PG-13'),
           (4, 'R'),
           (5, 'NC-17');

MERGE INTO GENRE_CATEGORY (GENRE_ID, GENRE_NAME)
    VALUES (1, 'Комедия'),
           (2, 'Драма'),
           (3, 'Мультфильм'),
           (4, 'Триллер'),
           (5, 'Документальный'),
           (6, 'Боевик');

MERGE INTO FRIENDSHIP_STATUS (STATUS_ID, STATUS_NAME)
    VALUES (1, 'Не в друзьях'),
           (2, 'В друзьях');