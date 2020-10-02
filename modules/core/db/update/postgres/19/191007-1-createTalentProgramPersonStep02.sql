create table TSADV_TALENT_PROGRAM_PERSON_STEP (
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
    DIC_TALENT_PROGRAM_STEP_ID uuid not null,
    PERSON_GROUP_ID uuid not null,
    ADDRESS varchar(255),
    DATE_FROM timestamp,
    DATE_TO timestamp,
    STATUS_ID uuid,
    --
    primary key (ID)
);
