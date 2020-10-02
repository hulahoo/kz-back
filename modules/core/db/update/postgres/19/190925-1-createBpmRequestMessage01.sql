create table TSADV_BPM_REQUEST_MESSAGE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ENTITY_NAME varchar(255) not null,
    ENTITY_ID uuid not null,
    ENTITY_REQUEST_NUMBER bigint,
    SEND_DATE timestamp,
    MESSAGE varchar(3000),
    ASSIGNED_USER_ID uuid,
    ASSIGNED_BY_ID uuid,
    STATUS integer,
    PARENT_ID uuid,
    LVL integer,
    SCREEN_NAME varchar(255),
    PROC_INSTANCE_ID uuid,
    --
    primary key (ID)
);
