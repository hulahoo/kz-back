create table TSADV_INTERNSHIP_EXPENSES (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    TRAINEE_ID uuid not null,
    EXPENSE_TYPE_ID uuid not null,
    AMOUNT double precision not null,
    CURRENCY_ID uuid not null,
    --
    primary key (ID)
);
