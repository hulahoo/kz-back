create table TSADV_PERSON_QUESTIONNAIRE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    APPRAISE_ID uuid not null,
    STATUS varchar(50) not null,
    OVERALL_SCORE integer,
    AVERAGE_SCORE double precision,
    APPRAISER_ID uuid not null,
    QUESTIONNAIRE_ID uuid not null,
    APPRAISAL_DATE date not null,
    --
    primary key (ID)
);
