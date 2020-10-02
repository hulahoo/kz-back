create table TSADV_DIC_GOODS_CATEGORY (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(255),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(255),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(255),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(255),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(255),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    LEGACY_ID varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    --
    PARENT_ID uuid,
    --
    primary key (ID)
);
