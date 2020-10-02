create table TSADV_LEARNING_EXPENSE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    EXPENSE_TYPE_ID uuid not null,
    CURRENCY_ID uuid not null,
    AMOUNT integer not null,
    PERSON_LEARNING_CONTRACT_ID uuid,
    PERSON_ENROLLMENT_ID uuid,
    --
    primary key (ID)
);
