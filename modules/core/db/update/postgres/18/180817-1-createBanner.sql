create table TSADV_BANNER (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PAGE varchar(255) not null,
    IMAGE_LANG1_ID uuid not null,
    IMAGE_LANG2_ID uuid,
    IMAGE_LANG3_ID uuid,
    ACTIVE boolean not null,
    --
    primary key (ID)
);
