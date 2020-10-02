create table TSADV_ATTESTATION_PARTICIPANT (
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
    PERSON_GROUP_ID uuid not null,
    ATTESTATION_DATE date not null,
    PASSING_LANGUAGE varchar(50) not null,
    RESULT_ID uuid not null,
    EVENT_ID uuid not null,
    --
    primary key (ID)
);
