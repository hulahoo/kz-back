create table TSADV_ENROLLMENT_CERTIFICATE_FILE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ENROLLMENT_ID uuid not null,
    CERTIFICATE_FILE_ID uuid,
    --
    primary key (ID)
);
