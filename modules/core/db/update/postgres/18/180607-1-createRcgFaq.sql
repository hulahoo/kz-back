create table TSADV_RCG_FAQ (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    TITLE varchar(1000) not null,
    CONTENT text not null,
    CODE varchar(20),
    --
    primary key (ID)
);
