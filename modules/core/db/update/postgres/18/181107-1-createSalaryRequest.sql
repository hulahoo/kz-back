create table TSADV_SALARY_REQUEST (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ASSIGNMENT_GROUP_ID uuid,
    STATUS_ID uuid not null,
    LEGACY_ID varchar(255),
    AMOUNT integer not null,
    NET_GROSS varchar(50) not null,
    REASON_ID uuid,
    CURRENCY_ID uuid not null,
    ORD_ASSIGNMENT_ID uuid,
    START_DATE date not null,
    END_DATE date not null,
    ORDER_GROUP_ID uuid,
    AGREEMENT_ID uuid,
    TYPE_ varchar(50) not null,
    --
    primary key (ID)
);
