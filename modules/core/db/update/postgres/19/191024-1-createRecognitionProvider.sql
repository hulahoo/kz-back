create table TSADV_RECOGNITION_PROVIDER (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PROVIDER_NAME_LANG1 varchar(350) not null,
    PROVIDER_NAME_LANG2 varchar(350),
    PROVIDER_NAME_LANG3 varchar(360),
    CONTACT_INFO varchar(350),
    ADDRESS_LANG1 varchar(350),
    ADDRESS_LANG2 varchar(350),
    ADDRESS_LANG3 varchar(350),
    --
    primary key (ID)
);
