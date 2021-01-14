create table TSADV_SCORE_SETTING (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PERFORMANCE_PLAN_ID uuid,
    MIN_PERCENT double precision,
    MAX_PERCENT double precision,
    FINAL_SCORE integer,
    --
    primary key (ID)
);