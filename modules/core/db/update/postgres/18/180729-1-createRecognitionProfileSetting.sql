create table TSADV_RECOGNITION_PROFILE_SETTING (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    AUTOMATIC_TRANSLATE boolean not null,
    PERSON_GROUP_ID uuid not null,
    --
    primary key (ID)
);
