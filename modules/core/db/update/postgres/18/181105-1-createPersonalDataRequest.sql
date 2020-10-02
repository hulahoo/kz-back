create table TSADV_PERSONAL_DATA_REQUEST (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    LAST_NAME varchar(255),
    FIRST_NAME varchar(255),
    MIDDLE_NAME varchar(255),
    PREVIOUS_LAST_NAME varchar(255),
    PREVIOUS_FIRST_NAME varchar(255),
    LAST_NAME_LATIN varchar(255),
    FIRST_NAME_LATIN varchar(255),
    MIDDLE_NAME_LATIN varchar(255),
    MARITAL_STATUS_ID uuid,
    DATE_OF_BIRTH date,
    ATTACHMENT_ID uuid,
    STATUS_ID uuid,
    PERSON_ID uuid not null,
    START_DATE date,
    END_DATE date,
    --
    primary key (ID)
);
