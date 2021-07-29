create table TSADV_BPROC_REASSIGNMENT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    EXECUTION_ID varchar(255) not null,
    START_TIME timestamp not null,
    END_TIME timestamp,
    COMMENT_ varchar(2000),
    OUTCOME varchar(50) not null,
    ORDER_ integer,
    ASSIGNEE_ID uuid not null,
    PROCESS_INSTANCE_ID varchar(50) not null,
    --
    primary key (ID)
);