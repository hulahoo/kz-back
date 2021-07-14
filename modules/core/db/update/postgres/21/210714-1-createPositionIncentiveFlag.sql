create table TSADV_POSITION_INCENTIVE_FLAG (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    POSITION_GROUP_ID uuid not null,
    LEGACY_ID varchar(255),
    IS_INCENTIVE boolean not null,
    DATE_FROM date,
    DATE_TO date,
    --
    primary key (ID)
);