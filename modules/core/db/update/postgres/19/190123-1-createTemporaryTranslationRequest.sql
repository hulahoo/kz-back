create table TSADV_TEMPORARY_TRANSLATION_REQUEST (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    REQUEST_NUMBER varchar(255),
    STATUS_ID uuid,
    PERSON_GROUP_ID uuid,
    START_DATE date,
    END_DATE date,
    POSITION_GROUP_ID uuid,
    GRADE_GROUP_ID uuid,
    ORGANIZATION_GROUP_ID uuid,
    JOB_GROUP_ID uuid,
    NOTE varchar(3000),
    ATTACHMENT_ID uuid,
    SUBSTITUTED_EMPLOYEE_ID uuid,
    REASON varchar(3000),
    --
    primary key (ID)
);
