create table TSADV_BPM_USER_SUBSTITUTION (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    SUBSTITUTED_USER_ID uuid not null,
    USER_ID uuid not null,
    START_DATE date not null,
    END_DATE date not null,
    --
    primary key (ID)
);
