create table TSADV_CERTIFICATE_REQUEST (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    REQUEST_NUMBER bigint not null,
    REQUEST_DATE date not null,
    PERSON_GROUP_ID uuid not null,
    CRETIFICATE_TYPE_ID uuid not null,
    RECEIVING_TYPE_ID uuid not null,
    LANGUAGE_ID uuid not null,
    SHOW_SALARY boolean not null,
    NUMBER_OF_COPY integer not null,
    FILE_ID uuid,
    STATUS_ID uuid not null,
    --
    primary key (ID)
);