create table TSADV_NEWS (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NEWS_LANG1 varchar(2000),
    NEWS_LANG2 varchar(2000),
    NEWS_LANG3 varchar(2000),
    TITLE_LANG1 varchar(256),
    TITLE_LANG2 varchar(256),
    TITLE_LANG3 varchar(256),
    IS_PUBLISHED boolean,
    BANNER_ID uuid,
    --
    primary key (ID)
);