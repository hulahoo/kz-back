create table TSADV_ORGANIZATION_INCENTIVE_FLAG (
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
    IS_INCENTIVE boolean not null,
    DATE_FROM date not null,
    DATE_TO date not null,
    --
    primary key (ID)
);