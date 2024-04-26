drop table if exists
    FILM,
    FILM_GENRE,
    FILM_LIKE,
    FILM_RATING,
    FRIENDSHIP_STATUS,
    GENRE_CATEGORY,
    USER_FRIEND,
    USERS;
create table IF NOT EXISTS FILM_RATING
(
    RATING_ID        INTEGER auto_increment,
    MPAA_FILM_RATING CHARACTER VARYING(5) not null,
    constraint FILM_RATING
        primary key (RATING_ID)
);

create table IF NOT EXISTS FRIENDSHIP_STATUS
(
    STATUS_ID   INTEGER auto_increment,
    STATUS_NAME CHARACTER VARYING(15) not null,
    constraint FRIENDSHIP_STATUS_ID
        primary key (STATUS_ID)
);

create table IF NOT EXISTS FILM
(
    FILM_ID           INTEGER auto_increment,
    FILM_NAME         CHARACTER VARYING(100) not null,
    FILM_DESCRIPTION  CHARACTER VARYING(200),
    FILM_RELEASE_DATE DATE                   not null,
    FILM_DURATION     INTEGER                not null,
    FILM_RATING_ID    INTEGER                not null,
    constraint FILM_ID
        primary key (FILM_ID),
    constraint RATING_ID_FK
        foreign key (FILM_RATING_ID) references FILM_RATING
);



create table IF NOT EXISTS GENRE_CATEGORY
(
    GENRE_ID   INTEGER auto_increment,
    GENRE_NAME CHARACTER VARYING(50) not null,
    constraint GENRE_ID
        primary key (GENRE_ID)
);

create table IF NOT EXISTS FILM_GENRE
(
    FILM_ID  INTEGER not null,
    GENRE_ID INTEGER not null,
    constraint FILM_GENRE_ID_FK
        foreign key (FILM_ID) references FILM on delete cascade,
    constraint GENRE_CATEGORY_ID_FK
        foreign key (GENRE_ID) references GENRE_CATEGORY on delete cascade
);

create table IF NOT EXISTS USERS
(
    USER_ID       INTEGER auto_increment,
    USER_EMAIL    CHARACTER VARYING(100) not null UNIQUE CHECK (USER_EMAIL <> ''),
    USER_LOGIN    CHARACTER VARYING(255) not null UNIQUE CHECK (USER_LOGIN <> ''),
    USER_NAME     CHARACTER VARYING(100),
    USER_BIRTHDAY DATE                   not null,
    constraint USER_ID
        primary key (USER_ID)
);

create table IF NOT EXISTS FILM_LIKE
(
    FILM_ID INTEGER not null,
    USER_ID INTEGER not null,
    constraint FILM_ID__FK
        foreign key (FILM_ID) references FILM on delete cascade,
    constraint FILM_LIKE_USER_ID_FK
        foreign key (USER_ID) references USERS on delete cascade
);

create table IF NOT EXISTS USER_FRIEND
(
    USER_ID   INTEGER not null,
    FRIEND_ID INTEGER not null,
    STATUS_ID INTEGER not null,
    constraint STATUS_ID_FK
        foreign key (STATUS_ID) references FRIENDSHIP_STATUS on delete cascade,
    constraint USER_FRIEND__ID_FK
        foreign key (FRIEND_ID) references USERS on delete cascade,
    constraint USER_ID_FK
        foreign key (USER_ID) references USERS on delete cascade,
            PRIMARY KEY (user_id, friend_id)
);