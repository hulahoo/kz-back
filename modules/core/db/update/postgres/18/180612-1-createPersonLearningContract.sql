create table TSADV_PERSON_LEARNING_CONTRACT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PERSON_GROUP_ID uuid not null,
    CONTRACT_NUMBER varchar(255) not null,
    CONTRACT_DATE date not null,
    LEARNING_COST double precision not null,
    TERM_OF_SERVICE date not null,
    BUSINESS_TRIP_COST double precision,
    PER_DIEM_COST double precision,
    LIVING_COST double precision,
    LEARNING_CENTER varchar(255),
    SPECIALIZATION varchar(255),
    REASON text,
    OTHER text,
    --
    primary key (ID)
);
