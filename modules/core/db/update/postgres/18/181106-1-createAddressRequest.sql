create table TSADV_ADDRESS_REQUEST (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ADDRESS_TYPE_ID uuid,
    ADDRESS varchar(255),
    COUNTRY_ID uuid,
    POSTAL_CODE integer,
    CITY varchar(255),
    START_DATE date,
    END_DATE date,
    ATTACHMENT_ID uuid,
    STATUS_ID uuid,
    BASE_ADDRESS_ID uuid not null,
    --
    primary key (ID)
);
