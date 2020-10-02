create table TSADV_ATTESTATION (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ATTESTATION_NAME varchar(255),
    ATTESTATION_TYPE_ID uuid not null,
    START_DATE date,
    END_DATE date,
    ORGANIZATION_GROUP_ID uuid,
    REASON varchar(1000),
    DOCUMENT_NUMBER varchar(255),
    DOCUMENT_DATE date,
    --
    primary key (ID)
);
