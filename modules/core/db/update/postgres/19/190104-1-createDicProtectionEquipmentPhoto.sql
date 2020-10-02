create table TSADV_DIC_PROTECTION_EQUIPMENT_PHOTO (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    LEGACY_ID varchar(255),
    --
    DESCRIPTION text not null,
    ATTACHMENT_ID uuid,
    DIC_PROTECTION_EQUIPMENT_ID uuid,
    --
    primary key (ID)
);
