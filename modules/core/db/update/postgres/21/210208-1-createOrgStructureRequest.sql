create table TSADV_ORG_STRUCTURE_REQUEST (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    REQUEST_NUMBER bigint not null,
    REQUEST_DATE date not null,
    REQUEST_STATUS_ID uuid not null,
    COMPANY_ID uuid not null,
    DEPARTMENT_ID uuid not null,
    AUTHOR_ID uuid not null,
    --
    primary key (ID)
);