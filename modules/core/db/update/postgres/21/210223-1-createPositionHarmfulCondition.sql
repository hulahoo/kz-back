create table TSADV_POSITION_HARMFUL_CONDITION (
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
    END_DATE date not null,
    DAYS integer not null,
    START_DATE date not null,
    --
    primary key (ID)
);