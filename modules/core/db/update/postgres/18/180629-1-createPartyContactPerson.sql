create table TSADV_PARTY_CONTACT_PERSON (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PARTY_EXT_ID uuid,
    FULL_NAME varchar(255),
    CONTACT_PERSON_TYPE_ID uuid,
    --
    primary key (ID)
);
