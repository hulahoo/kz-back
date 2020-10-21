create table TSADV_APP_PROPERTY_ENTITY_DESCRIPTION (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    DESCRIPTION varchar(512),
    APP_PROPERTY_NAME varchar(512) not null,
    --
    primary key (ID)
);