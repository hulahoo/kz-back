create table TSADV_PERSON_EDUCATION_REQUEST (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    LEGACY_ID varchar(255),
    ORGANIZATION_BIN varchar(255),
    INTEGRATION_USER_LOGIN varchar(255),
    --
    PERSON_GROUP_ID uuid not null,
    DIPLOMA_NUMBER varchar(255),
    GRADUATION_DATE date,
    FOREIGN_EDUCATION boolean not null,
    SCHOOL varchar(255),
    EDUCATIONAL_INSTITUTION_ID uuid,
    EDUCATION_TYPE_ID uuid,
    START_YEAR integer,
    END_YEAR integer,
    SPECIALIZATION varchar(255),
    DEGREE_ID uuid,
    LOCATION varchar(255),
    LEVEL_ID uuid,
    FACULTY varchar(2000),
    QUALIFICATION varchar(2000),
    FORM_STUDY_ID uuid,
    STATUS_ID uuid,
    FILE_ID uuid,
    --
    primary key (ID)
);