create table TSADV_LEARNING_FEEDBACK_TEMPLATE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(500) not null,
    ACTIVE boolean not null,
    DESCRIPTION varchar(2000),
    USAGE_TYPE varchar(50) not null,
    EMPLOYEE boolean not null,
    MANAGER boolean not null,
    TRAINER boolean not null,
    --
    primary key (ID)
);
