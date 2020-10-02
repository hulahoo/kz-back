create table TSADV_PERSON_COIN_LOG (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PERSON_GROUP_ID uuid not null,
    COINS bigint not null,
    DATE_ date not null,
    RECOGNITION_ID uuid,
    GOODS_ID uuid,
    --
    primary key (ID)
);
