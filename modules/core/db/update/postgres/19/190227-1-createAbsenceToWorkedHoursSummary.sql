create table TSADV_ABSENCE_TO_WORKED_HOURS_SUMMARY (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    LEGACY_ID varchar(255),
    --
    ABSENCE_ID uuid,
    WORKED_HOURS_SUMMARY_ID uuid,
    --
    primary key (ID)
);
