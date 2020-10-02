create table TSADV_RCG_FEEDBACK_ATTACHMENT (
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
    FILE_ID uuid not null,
    RCG_FEEDBACK_ID uuid not null,
    --
    primary key (ID)
);
