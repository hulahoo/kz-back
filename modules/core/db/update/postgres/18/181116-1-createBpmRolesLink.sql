create table TSADV_BPM_ROLES_LINK (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PROC_DEFINITON_ID uuid not null,
    BPM_ROLE_ID uuid,
    HR_ROLE_ID uuid,
    --
    primary key (ID)
);
