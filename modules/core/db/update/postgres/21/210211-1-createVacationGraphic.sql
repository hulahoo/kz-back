create table TSADV_VACATION_GRAPHIC (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    REQUEST_NUMBER bigint,
    NAME varchar(255) not null,
    SURNAME varchar(255) not null,
    MIDDLENAME varchar(255) not null,
    DIVISION varchar(255) not null,
    DUTY varchar(255) not null,
    START_DATE date not null,
    END_DATE date,
    COMMENTS varchar(255),
    IS_SEND_TO_ORACL boolean,
    --
    primary key (ID)
);