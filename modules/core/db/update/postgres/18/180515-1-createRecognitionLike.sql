create table TSADV_RECOGNITION_LIKE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PERSON_GROUP_ID uuid not null,
    RECOGNITION_ID uuid,
    --
    primary key (ID)
);
