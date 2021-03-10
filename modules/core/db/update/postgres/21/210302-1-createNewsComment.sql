create table TSADV_NEWS_COMMENT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NEWSID_ID uuid,
    COMMENT_LANG1 varchar(2000),
    COMMENT_LANG2 varchar(2000),
    COMMENT_LANG3 varchar(2000),
    --
    primary key (ID)
);