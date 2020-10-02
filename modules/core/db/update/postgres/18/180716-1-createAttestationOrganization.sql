create table TSADV_ATTESTATION_ORGANIZATION (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ATTESTATION_ID uuid not null,
    ORGANIZATION_GROUP_ID uuid not null,
    INCLUDE_CHILD BOOLEAN DEFAULT TRUE,
    --
    primary key (ID)
);
