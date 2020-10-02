create table TSADV_JOB_PROTECTION_EQUIPMENT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    JOB_GROUP_ID uuid not null,
    NORM_PER_YEAR integer not null,
    START_DATE date,
    END_DATE date,
    --
    primary key (ID)
);
