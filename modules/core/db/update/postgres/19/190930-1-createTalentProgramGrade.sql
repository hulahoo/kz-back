create table if not exists TSADV_TALENT_PROGRAM_GRADE (
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
    GRADE_GROUP_ID uuid,
    --
    primary key (ID)
);
