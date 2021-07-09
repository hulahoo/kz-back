create table TSADV_PORTAL_MENU_CUSTOMIZATION (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    MENU_ITEM varchar(255) not null,
    PORTAL_AVAILABILITY varchar(50) not null,
    --
    primary key (ID)
);