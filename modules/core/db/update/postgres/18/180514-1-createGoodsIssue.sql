create table TSADV_GOODS_ISSUE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    GOOD_ID uuid not null,
    STATUS integer not null,
    QUANTITY integer not null,
    PERSON_GROUP_ID uuid not null,
    --
    primary key (ID)
);
