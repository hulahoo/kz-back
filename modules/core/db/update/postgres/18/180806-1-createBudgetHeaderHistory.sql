create table TSADV_BUDGET_HEADER_HISTORY (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    BUDGET_HEADER_ID uuid not null,
    CHANGE_PERSON_ID uuid not null,
    COMMENT_ varchar(1000),
    --
    primary key (ID)
);
