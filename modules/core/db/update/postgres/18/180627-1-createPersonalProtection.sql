create table TSADV_PERSONAL_PROTECTION (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    EMPLOYEE_ID uuid not null,
    PROTECTION_EQUIPMENT_ID uuid not null,
    DATE_OF_ISSUE date not null,
    STATUS varchar(50) not null,
    WRITTEN_OF_REASON varchar(255),
    WRITTEN_OF_DATE date,
    --
    primary key (ID)
);
