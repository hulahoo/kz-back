create table TSADV_GOODS_CART (
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
    ISSUED boolean not null,
    PERSON_GROUP_ID uuid not null,
    QUANTITY bigint not null,
    --
    primary key (ID)
);
