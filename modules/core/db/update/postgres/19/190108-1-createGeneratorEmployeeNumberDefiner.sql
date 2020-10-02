create table TSADV_GENERATOR_EMPLOYEE_NUMBER_DEFINER (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    GENERATOR_EMPLOYEE_NUMBER_ID uuid,
    PERSON_TYPE_ID uuid,
    --
    primary key (ID)
);
