create table TSADV_BUDGET_REQUEST_ITEM (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    BUDGET_REQUEST_ID uuid not null,
    BUDGET_ITEM_ID uuid not null,
    AMOUNT double precision not null,
    CURRENCY_ID uuid not null,
    --
    primary key (ID)
);
