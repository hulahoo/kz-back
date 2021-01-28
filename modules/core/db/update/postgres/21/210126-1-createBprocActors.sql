create table TSADV_BPROC_ACTORS (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ENTITY_ID uuid not null,
    HR_ROLE_ID uuid not null,
    USER_ID uuid not null,
    BPROC_USER_TASK_CODE varchar(255) not null,
    --
    primary key (ID)
);