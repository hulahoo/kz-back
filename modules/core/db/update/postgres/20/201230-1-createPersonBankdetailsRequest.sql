create table TSADV_PERSON_BANKDETAILS_REQUEST (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    LEGACY_ID varchar(255),
    ORGANIZATION_BIN varchar(255),
    INTEGRATION_USER_LOGIN varchar(255),
    --
    BANK_ID uuid,
    FULL_NAME_BANK_CARD varchar(2000),
    IBAN varchar(255),
    BIC_BANK varchar(255),
    PERSON_GROUP_ID uuid,
    REQUEST_STATUS_ID uuid,
    FILE_ID uuid,
    BANK_DETAILS_ID uuid,
    --
    primary key (ID)
);