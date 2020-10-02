create table TSADV_PERSONAL_PROTECTION_VIOLATION (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PERSONAL_PROTECTION_ID uuid not null,
    PERSONAL_PROTECTION_INSPECTOR_ID uuid not null,
    VIOLATION_DATE date not null,
    VIOLATION_INFO varchar(255),
    --
    primary key (ID)
);
