create table TSADV_TIMECARD_DEVIATION (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    TIMECARD_HEADER_ID uuid not null,
    HOURS DOUBLE PRECISION not null,
    DATE_FROM date not null,
    DATE_TO date not null,
    IS_CHANGES_FACT_HOURS boolean not null,
    IS_CHANGES_PLAN_HOURS boolean not null,
    IS_CHANGES_DETAILS_FROM_BEGIN boolean not null,
    --
    primary key (ID)
);
