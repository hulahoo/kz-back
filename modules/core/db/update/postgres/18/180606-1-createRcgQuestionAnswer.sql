create table TSADV_RCG_QUESTION_ANSWER (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    TEXT varchar(500),
    ICON_ID uuid,
    RCG_QUESTION_ID uuid not null,
    --
    primary key (ID)
);
