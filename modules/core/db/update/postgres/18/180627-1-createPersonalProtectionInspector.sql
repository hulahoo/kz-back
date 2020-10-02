create table TSADV_PERSONAL_PROTECTION_INSPECTOR (
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
    ORGANIZATION_GROUP_ID uuid not null,
    ASSIGNMENT_DATE date not null,
    ASSIGNMENT_ORDER varchar(255),
    --
    primary key (ID)
);
