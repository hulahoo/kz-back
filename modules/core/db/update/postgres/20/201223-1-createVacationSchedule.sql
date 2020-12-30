create table TSADV_VACATION_SCHEDULE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PERSON_GROUP_ID uuid,
    START_DATE date,
    END_DATE date,
    ABSENCE_DAYS integer,
    --
    primary key (ID)
);