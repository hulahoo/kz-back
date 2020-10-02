create table if not exists TSADV_TALENT_PROGRAM_STEP (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    TALENT_PROGRAM_ID uuid,
    ORDER_NUM integer,
    STEP_ID uuid,
    NOTIFICATION_ID uuid,
    --
    primary key (ID)
);
