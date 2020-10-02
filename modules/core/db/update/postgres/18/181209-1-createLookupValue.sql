create table TSADV_LOOKUP_VALUE (
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
    LOOKUP_TYPE_ID uuid not null,
    LOOKUP_TYPE_CODE varchar(255) not null,
    MEANING_LANG1 varchar(255) not null,
    MEANING_LANG2 varchar(255),
    MEANING_LANG3 varchar(255),
    DESCRIPTION_LANG1 varchar(255),
    DESCRIPTION_LANG2 varchar(255),
    DESCRIPTION_LANG3 varchar(255),
    ENABLED_FLAG boolean,
    START_DATE date,
    END_DATE date,
    TAG varchar(255),
    --
    primary key (ID)
);
