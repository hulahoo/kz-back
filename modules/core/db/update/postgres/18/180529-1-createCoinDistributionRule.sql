create table TSADV_COIN_DISTRIBUTION_RULE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255) not null,
    DESCRIPTION varchar(2000),
    MANAGER_COINS bigint not null,
    EMPLOYEE_COINS bigint not null,
    DISTRIBUTION_FREQUENCY integer not null,
    ZERO_FREQUENCY integer not null,
    ACTIVE boolean not null,
    --
    primary key (ID)
);
