create table TSADV_ORGANIZATION_INCENTIVE_RESULT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ORGANIZATION_GROUP_ID uuid not null,
    PERIOD_DATE date,
    INDICATOR_ID uuid,
    PLAN_ decimal(19, 2),
    FACT decimal(19, 2),
    WEIGHT double precision,
    RESULT_ double precision,
    --
    primary key (ID)
);