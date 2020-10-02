create table TSADV_MEDAL (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    LANG_NAME1 varchar(255) not null,
    LANG_NAME2 varchar(255) not null,
    LANG_NAME5 varchar(255),
    ICON_ID uuid,
    TEMPLATE_ID uuid,
    LANG_NAME3 varchar(255),
    LANG_NAME4 varchar(255),
    --
    primary key (ID)
);
