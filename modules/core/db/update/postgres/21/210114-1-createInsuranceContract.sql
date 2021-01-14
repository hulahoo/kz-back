create table TSADV_INSURANCE_CONTRACT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    POLICY_NAME varchar(25) not null,
    CONTRACT varchar(10) not null,
    SIGN_DATE date not null,
    COMPANY_ID uuid not null,
    RESPONSIBLE_ID uuid,
    INSURER varchar(255),
    YEAR integer,
    START_DATE date not null,
    EXPIRATION_DATE date not null,
    AVAILABILITY_PERIOD_FROM date not null,
    INSURANCE_PROGRAM varchar(500) not null,
    INSURER_CONTACTS varchar(100),
    NOTIFICATION_DATE date not null,
    ATTACHING_AN_EMPLOYEE integer not null,
    ATTACHING_FAMILY integer not null,
    COUNT_OF_FREE_MEMBERS integer,
    --
    primary key (ID)
);