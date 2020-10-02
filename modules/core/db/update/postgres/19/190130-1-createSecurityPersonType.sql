create table TSADV_SECURITY_PERSON_TYPE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    SECURITY_GROUP_ID uuid not null,
    PERSON_TYPE_ID uuid not null,
    --
    primary key (ID)
);
