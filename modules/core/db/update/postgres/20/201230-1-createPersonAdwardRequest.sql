create table TSADV_PERSON_ADWARD_REQUEST (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    LEGACY_ID varchar(255),
    ORGANIZATION_BIN varchar(255),
    INTEGRATION_USER_LOGIN varchar(255),
    --
    PERSON_GROUP_ID uuid,
    REQUEST_STATUS_ID uuid,
    ACADEMIC_DEGREE varchar(2000),
    SCIENTIFIC_WORKS_IVENTIONS varchar(2000),
    STATE_AWARDS varchar(2000),
    --
    primary key (ID)
);