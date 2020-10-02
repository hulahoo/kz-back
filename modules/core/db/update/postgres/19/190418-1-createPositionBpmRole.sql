create table TSADV_POSITION_BPM_ROLE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    POSITION_GROUP_ID uuid,
    PROC_MODEL_ID uuid,
    --
    primary key (ID)
);
