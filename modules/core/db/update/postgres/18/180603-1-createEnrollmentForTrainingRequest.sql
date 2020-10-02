create table TSADV_ENROLLMENT_FOR_TRAINING_REQUEST (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    REASON varchar(1000),
    PERSON_GROUP_ID uuid,
    STATUS integer,
    DATE_ date,
    MONEY_IN_BUDGET boolean,
    TRAINING_REQUEST_ID uuid,
    --
    primary key (ID)
);
