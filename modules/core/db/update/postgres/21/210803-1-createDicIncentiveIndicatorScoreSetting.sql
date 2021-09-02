create table TSADV_DIC_INCENTIVE_INDICATOR_SCORE_SETTING (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    INDICATOR_ID uuid not null,
    MIN_PERCENT double precision not null,
    MAX_PERCENT double precision not null,
    TOTAL_SCORE double precision not null,
    --
    primary key (ID)
);