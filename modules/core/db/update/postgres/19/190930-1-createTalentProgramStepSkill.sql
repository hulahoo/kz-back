create table if not exists TSADV_TALENT_PROGRAM_STEP_SKILL (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ORDER_NUMBER integer,
    TALENT_PROGRAM_STEP_ID uuid,
    SKILL_ID uuid,
    --
    primary key (ID)
);
