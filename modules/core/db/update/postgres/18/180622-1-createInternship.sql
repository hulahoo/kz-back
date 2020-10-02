create table TSADV_INTERNSHIP (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    TRAINEE_ID uuid not null,
    INTERNSHIP_TYPE_ID uuid not null,
    SPECIALIZATION varchar(255),
    REASON varchar(255),
    AGREEMENT_NUMBER varchar(255),
    AGREEMENT_DATE date,
    START_DATE date not null,
    END_DATE date not null,
    PAYABLE boolean not null,
    MAIN_MENTOR_ID uuid,
    MAIN_MENTOR_REASON varchar(255),
    --
    primary key (ID)
);
