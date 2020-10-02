create table TSADV_GOODS_ORDER_HISTORY (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    GOODS_ORDER_ID uuid not null,
    STATUS varchar(50) not null,
    DATE_TIME timestamp not null,
    PERSON_GROUP_ID uuid,
    --
    primary key (ID)
);
