create table TSADV_PERSON_LENGTH_OF_SERVICE (
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
    PERSON_GROUP_ID uuid,
    LENGTH_OF_SERVICE_TYPE_ID uuid,
    RANGE_ID uuid,
    EFFECTIVE_DATE date,
    --
    primary key (ID)
);
