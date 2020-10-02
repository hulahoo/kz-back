create table TSADV_ASSIGNMENT_SALARY_REQUEST (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PERSON_GROUP_ID uuid,
    REQUEST_NUMBER varchar(255),
    STATUS_ID uuid,
    DATE_FROM date,
    POSITION_GROUP_ID uuid,
    GRADE_GROUP_ID uuid,
    ORGANIZATION_GROUP_ID uuid,
    JOB_GROUP_ID uuid,
    TYPE_ varchar(50),
    AMOUNT double precision,
    CHANGE_PERCENT double precision,
    NET_GROSS varchar(50),
    CURRENCY_ID uuid,
    REASON_ID uuid,
    NOTE varchar(3000),
    --
    primary key (ID)
);
