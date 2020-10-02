create table TSADV_PERSON_AWARD (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    TYPE_ID uuid not null,
    DATE_ date not null,
    AUTHOR_ID uuid not null,
    RECEIVER_ID uuid not null,
    HISTORY varchar(2000) not null,
    WHY varchar(2000) not null,
    STATUS varchar(50) not null,
    --
    primary key (ID)
);
