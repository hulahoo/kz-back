create table TSADV_AWARDS_REQUEST (
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
    PERSON_GROUP_ID uuid,
    PROMOTION_TYPE_ID uuid,
    CALCULATED varchar(255),
    SUR_CHARGE_TYPE integer,
    VALUE_ integer,
    AWARD_TYPE_ID uuid,
    DATE_ date,
    ORDER_NUM varchar(255),
    ORDER_DATE date,
    ASSIGNMENT_GROUP_ID uuid,
    REASON varchar(255),
    START_DATE date,
    NOTE varchar(2500),
    REQUEST_STATUS_ID uuid,
    --
    primary key (ID)
);