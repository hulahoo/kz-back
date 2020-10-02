create table TSADV_VACATION_CONDITIONS (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    START_DATE date,
    END_DATE date,
    WRITE_HISTORY boolean,
    --
    ADDITIONAL_DAYS integer not null,
    VACATION_DURATION_TYPE varchar(50),
    MAIN_DAYS_NUMBER bigint,
    DAYS_TYPE_ID uuid,
    GROUP_ID uuid,
    POSITION_GROUP_ID uuid,
    JOB_GROUP_ID uuid,
    --
    primary key (ID)
);
