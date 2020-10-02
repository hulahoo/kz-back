create table TSADV_GOODS_ORDER (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    STATUS varchar(50) not null,
    TOTAL_SUM bigint not null,
    ORDER_NUMBER varchar(255) not null,
    ORDER_DATE timestamp not null,
    PERSON_GROUP_ID uuid not null,
    --
    primary key (ID)
);
