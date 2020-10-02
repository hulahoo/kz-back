create table TSADV_BOOK_REVIEW (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    BOOK_ID uuid not null,
    AUTHOR_ID uuid,
    RATING decimal(19, 2),
    POST_DATE date,
    REVIEW_TEXT varchar(2000),
    --
    primary key (ID)
);
