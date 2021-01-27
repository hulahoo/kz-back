create table TSADV_INSURED_PERSON (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ATTACH_DATE date not null,
    STATUS_REQUEST_ID uuid not null,
    INSURANCE_CONTRACT_ID uuid not null,
    COMPANY_ID uuid not null,
    EMPLOYEE_ID uuid not null,
    RELATIVE_ID uuid not null,
    FIRST_NAME varchar(50) not null,
    SECOND_NAME varchar(50) not null,
    MIDDLE_NAME varchar(50),
    JOB_ID uuid,
    SEX_ID uuid not null,
    IIN varchar(255) not null,
    BIRTHDATE date not null,
    DOCUMENT_TYPE_ID uuid not null,
    DOCUMENT_NUMBER varchar(255) not null,
    REGION_ID uuid not null,
    ADDRESS_ID uuid,
    ADDRESS varchar(255),
    INSURANCE_PROGRAM varchar(500) not null,
    FILE_ID uuid,
    TYPE varchar(50) not null,
    AMOUNT decimal(19, 2),
    TOTAL_AMOUNT decimal(19, 2) not null,
    EXCLUSION_DATE date,
    COMMENT varchar(500),
    --
    primary key (ID)
);