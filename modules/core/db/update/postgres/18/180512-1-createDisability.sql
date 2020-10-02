create table TSADV_DISABILITY (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    DISABILITY_TYPE_ID uuid,
    ATTACHMENT bytea,
    DURATION_ID uuid,
    DATE_FROM date,
    DATE_TO date,
    PERSON_GROUP_EXT_ID uuid,
    --
    primary key (ID)
);
