create table TSADV_PERSON_QUALIFICATION (
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
    TYPE_ID uuid not null,
    PERSON_GROUP_ID uuid not null,
    START_DATE date not null,
    END_DATE date not null,
    ASSIGN_VALIDATION_DATE date,
    ATTACHMENT_ID uuid,
    NOTE varchar(3000),
    --
    primary key (ID)
);
