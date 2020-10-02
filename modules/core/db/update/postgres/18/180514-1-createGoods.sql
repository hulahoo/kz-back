create table TSADV_GOODS (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    GOOD_NAME_LANG1 varchar(100) not null,
    GOOD_NAME_LANG2 varchar(100) not null,
    GOOD_NAME_LANG3 varchar(100) not null,
    GOOD_NAME_LANG4 varchar(100),
    GOOD_NAME_LANG5 varchar(100),
    DESCRIPTION_LANG1 varchar(100),
    DESCRIPTION_LANG2 varchar(100),
    DESCRIPTION_LANG3 varchar(100),
    DESCRIPTION_LANG4 varchar(100),
    DESCRIPTION_LANG5 varchar(100),
    PRICE double precision not null,
    ACTIVE boolean,
    --
    primary key (ID)
);
