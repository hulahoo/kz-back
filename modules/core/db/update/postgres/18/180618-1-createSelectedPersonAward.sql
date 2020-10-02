create table TSADV_SELECTED_PERSON_AWARD (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    AWARD_PROGRAM_ID uuid not null,
    PERSON_GROUP_ID uuid not null,
    AWARDED boolean not null,
    --
    primary key (ID)
);
