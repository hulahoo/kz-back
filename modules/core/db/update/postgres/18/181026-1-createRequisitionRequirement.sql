create table TSADV_REQUISITION_REQUIREMENT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    REQUIREMENT_ID uuid not null,
    REQUISITION_ID uuid not null,
    REQUIREMENT_LEVEL_ID uuid not null,
    CRITICAL BOOLEAN DEFAULT TRUE,
    --
    primary key (ID)
);
