create table TSADV_SCHOLARSHIP (
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
    GRANTEES_AGREEMENT_ID uuid not null,
    ACCOUNT varchar(255) not null,
    ACCOUNT_NAME varchar(255) not null,
    PAYMENT_DATE date not null,
    AMOUNT decimal(19, 2) not null,
    NOTE varchar(255),
    --
    primary key (ID)
);