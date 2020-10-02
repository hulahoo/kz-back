create table TSADV_RECOGNITION_QUALITY (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    RECOGNITION_ID uuid,
    QUALITY_ID uuid,
    --
    primary key (ID)
);
