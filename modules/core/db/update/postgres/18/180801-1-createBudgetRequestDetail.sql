create table TSADV_BUDGET_REQUEST_DETAIL (
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
    MONTH_ID uuid not null,
    EMPLOYEES_COUNT integer not null,
    BUSINESS_TRIP_EMPLOYEE integer,
    --
    primary key (ID)
);
