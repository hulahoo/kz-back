create table TSADV_RCG_QUESTION (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    TEXT varchar(500) not null,
    DESCRIPTION varchar(500) not null,
    ACTIVE boolean not null,
    ANSWER_TYPE varchar(50) not null,
    COINS bigint,
    --
    primary key (ID)
);
