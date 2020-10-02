create table TSADV_BUDGET_HEADER (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    BUDGET_ID uuid not null,
    HEADER_NAME varchar(255) not null,
    ORGANIZATION_GROUP_ID uuid not null,
    STATUS varchar(50) not null,
    --
    primary key (ID)
);
