create table TSADV_COIN_DISTRIBUTION_POSITION (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PARENT_POSITION_GROUP_ID uuid,
    HIERARCHY_ID uuid,
    POSITION_GROUP_ID uuid,
    COIN_DISTRIBUTION_RULE_ID uuid not null,
    --
    primary key (ID)
);
