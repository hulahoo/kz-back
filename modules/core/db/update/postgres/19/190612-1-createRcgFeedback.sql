create table TSADV_RCG_FEEDBACK (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    LEGACY_ID varchar(255),
    --
    TYPE_ID uuid,
    AUTHOR_ID uuid not null,
    RECEIVER_ID uuid not null,
    COMMENT_ varchar(2000) not null,
    COMMENT_EN varchar(2000) not null,
    COMMENT_RU varchar(2000) not null,
    FEEDBACK_DATE timestamp not null,
    THEME varchar(2000) not null,
    THEME_RU varchar(2000) not null,
    THEME_EN varchar(2000) not null,
    --
    primary key (ID)
);
