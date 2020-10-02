create table TSADV_RCG_FEEDBACK_COMMENT (
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
    TEXT varchar(2000) not null,
    TEXT_EN varchar(2000) not null,
    TEXT_RU varchar(2000) not null,
    AUTHOR_ID uuid not null,
    RCG_FEEDBACK_ID uuid not null,
    --
    primary key (ID)
);
