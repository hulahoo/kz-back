create table TSADV_JOB_TEST (
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
    TEST_ID uuid,
    PURPOSE varchar(50) not null,
    START_DATE date not null,
    END_DATE date not null,
    --
    primary key (ID)
);
