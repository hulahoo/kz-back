create table TSADV_ORGANIZATION_INCENTIVE_MONTH_RESULT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    COMPANY_ID uuid not null,
    PERIOD_ date not null,
    DEPARTMENT_ID uuid not null,
    INDICATOR_ID uuid not null,
    WEIGHT double precision not null,
    PLAN_ decimal(19, 2) not null,
    FACT decimal(19, 2) not null,
    PREMIUM_PERCENT double precision not null,
    TOTAL_PREMIUM_PERCENT double precision not null,
    STATUS_ID uuid not null,
    COMMENT_ varchar(2500) not null,
    --
    primary key (ID)
);