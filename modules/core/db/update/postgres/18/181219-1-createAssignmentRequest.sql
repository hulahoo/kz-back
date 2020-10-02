create table TSADV_ASSIGNMENT_REQUEST (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    DATE_FROM date,
    STATUS_ID uuid,
    REQUEST_NUMBER varchar(255),
    POSITION_GROUP_ID uuid,
    GRADE_GROUP_ID uuid,
    ORGANIZATION_GROUP_ID uuid,
    JOB_GROUP_ID uuid,
    REASON_ID uuid,
    NODE varchar(4000),
    PERSON_GROUP_ID uuid,
    --
    primary key (ID)
);
