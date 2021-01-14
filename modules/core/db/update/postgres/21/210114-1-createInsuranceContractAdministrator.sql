create table TSADV_INSURANCE_CONTRACT_ADMINISTRATOR (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NOTIFY_ABOUT_NEW_ATTACHMENTS boolean,
    EMPLOYEE_ID uuid not null,
    --
    primary key (ID)
);