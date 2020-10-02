create table TSADV_LOOKUP_TYPE (
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
    LOOKUP_TYPE varchar(255) not null,
    LOOKUP_NAME_LANG1 varchar(255) not null,
    LOOKUP_NAME_LANG2 varchar(255),
    LOOKUP_NAME_LANG3 varchar(255),
    --
    primary key (ID)
);
