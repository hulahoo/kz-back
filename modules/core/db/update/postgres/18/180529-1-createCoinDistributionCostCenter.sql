create table TSADV_COIN_DISTRIBUTION_COST_CENTER (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    COST_CENTER_ID uuid not null,
    COIN_DISTRIBUTION_RULE_ID uuid not null,
    --
    primary key (ID)
);
