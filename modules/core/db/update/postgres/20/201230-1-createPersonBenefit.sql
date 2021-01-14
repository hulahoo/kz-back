create table TSADV_PERSON_BENEFIT (
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
    REASON_ID uuid,
    COMBATANT varchar(50),
    CERTIFICATE_FROM_DATE varchar(255),
    DOCUMENT_NUMBER varchar(255),
    RADIATION_RISK_ZONE varchar(50),
    START_DATE_HISTORY date,
    END_DATE_HISTORY date,
    PERSON_GROUP_ID uuid,
    --
    primary key (ID)
);