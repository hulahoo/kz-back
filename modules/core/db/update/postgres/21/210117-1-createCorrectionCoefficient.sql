create table TSADV_CORRECTION_COEFFICIENT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    GROUP_EFFICIENCY_PERCENT double precision,
    COMPANY_RESULT double precision,
    PERFORMANCE_PLAN_ID uuid,
    --
    primary key (ID)
);