create table TSADV_MEDAL_CONDITION (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    MEDAL_ID uuid not null,
    QUALITY_ID uuid,
    QUALITY_QUANTITY bigint,
    CHILD_MEDAL_ID uuid,
    MEDAL_QUANTITY bigint,
    --
    primary key (ID)
);
