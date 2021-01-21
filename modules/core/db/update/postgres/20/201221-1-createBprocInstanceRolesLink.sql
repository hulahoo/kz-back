create table TSADV_BPROC_INSTANCE_ROLES_LINK (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PROCESS_INSTANCE_ID varchar(255),
    HR_ROLE_ID uuid,
    USER_ID uuid,
    --
    primary key (ID)
);