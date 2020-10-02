create table TSADV_PERSON_LEARNING_HISTORY (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    LEGACY_ID varchar(255),
    --
    PERSON_GROUP_ID uuid not null,
    START_DATE date not null,
    END_DATE date not null,
    HOURS double precision,
    COURSE_ID uuid,
    BUDGET_ITEM_ID uuid,
    EMPLOYEE_CATEGORY_ID uuid,
    LEARNING_TYPE_ID uuid,
    STATUS_ID uuid,
    PARTY_ID uuid,
    LOCATION_ID uuid,
    --
    primary key (ID)
);
