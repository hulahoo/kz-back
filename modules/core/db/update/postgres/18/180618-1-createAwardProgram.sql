create table TSADV_AWARD_PROGRAM (
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
    ORDER_ integer not null,
    --
    primary key (ID)
);
