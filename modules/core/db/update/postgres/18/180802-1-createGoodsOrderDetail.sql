create table TSADV_GOODS_ORDER_DETAIL (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    GOODS_ID uuid not null,
    QUANTITY bigint not null,
    GOODS_ORDER_ID uuid not null,
    --
    primary key (ID)
);
