create table TSADV_TRAINER (
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
    ADD_PAYMENT_AMOUNT integer,
    ORDER_NUMBER varchar(255),
    ORDER_DATE date,
    PARTY_ID uuid,
    --
    primary key (ID)
);
