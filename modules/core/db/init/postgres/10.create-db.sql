-- begin TSADV_DIC_FIRE_SAFETY_CATEGORY
create table TSADV_DIC_FIRE_SAFETY_CATEGORY (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_FIRE_SAFETY_CATEGORY
-- begin TSADV_EXCAVATION
create table TSADV_EXCAVATION (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_EXCAVATION
-- begin TSADV_DIC_ATTITUDE_TO_MILITARY
create table TSADV_DIC_ATTITUDE_TO_MILITARY (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_ATTITUDE_TO_MILITARY
-- begin TSADV_DIC_INTERVIEW_STATUS
create table TSADV_DIC_INTERVIEW_STATUS (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_INTERVIEW_STATUS
-- begin TSADV_EVENT_TYPE
create table TSADV_EVENT_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_EVENT_TYPE
-- begin TSADV_DIC_POSTING_CHANNEL
create table TSADV_DIC_POSTING_CHANNEL (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    CHANNAL_NAME varchar(255) not null,
    USER_NAME varchar(30),
    PASSWORD varchar(30),
    CONNECTION_URL varchar(255),
    REQUISITION_POSTING_CHANNEL_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_DIC_POSTING_CHANNEL
-- begin TSADV_DIC_COMPANY
create table TSADV_DIC_COMPANY (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_COMPANY
-- begin TSADV_DIC_EQUIPMENT_REPLACEMENT_REASON
create table TSADV_DIC_EQUIPMENT_REPLACEMENT_REASON (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_EQUIPMENT_REPLACEMENT_REASON
-- begin TSADV_DIC_REQUIRED_EDUCATION
create table TSADV_DIC_REQUIRED_EDUCATION (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_REQUIRED_EDUCATION
-- begin TSADV_DIC_INDUSTRY
create table TSADV_DIC_INDUSTRY (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_INDUSTRY
-- begin TSADV_DISCONFORMITY_TYPE
create table TSADV_DISCONFORMITY_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DISCONFORMITY_TYPE
-- begin TSADV_DIC_MILITARY_DOCUMENT_TYPE
create table TSADV_DIC_MILITARY_DOCUMENT_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_MILITARY_DOCUMENT_TYPE
-- begin TSADV_DIC_ATTESTATION_EVENT
create table TSADV_DIC_ATTESTATION_EVENT (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    DIC_ATTESTATION_RESULT_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_DIC_ATTESTATION_EVENT
-- begin TSADV_DIC_RC_QUESTION_ACCESSIBILITY
create table TSADV_DIC_RC_QUESTION_ACCESSIBILITY (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_RC_QUESTION_ACCESSIBILITY
-- begin TSADV_DIC_REQUEST_STATUS
create table TSADV_DIC_REQUEST_STATUS (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_REQUEST_STATUS
-- begin TSADV_DIC_CRIMINAL_LIABILITY_TYPE
create table TSADV_DIC_CRIMINAL_LIABILITY_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_CRIMINAL_LIABILITY_TYPE
-- begin TSADV_DIC_EVENT_TYPE
create table TSADV_DIC_EVENT_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_EVENT_TYPE
-- begin TSADV_DIC_RECEIVING_TYPE
create table TSADV_DIC_RECEIVING_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_RECEIVING_TYPE
-- begin TSADV_DIC_REGISTER_GROUP
create table TSADV_DIC_REGISTER_GROUP (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_REGISTER_GROUP
-- begin TSADV_DIC_QUESTIONNAIRE_STATUS
create table TSADV_DIC_QUESTIONNAIRE_STATUS (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_QUESTIONNAIRE_STATUS
-- begin TSADV_DIC_PAYROLL
create table TSADV_DIC_PAYROLL (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_PAYROLL
-- begin TSADV_DIC_NINE_BOX_LEVEL
create table TSADV_DIC_NINE_BOX_LEVEL (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_NINE_BOX_LEVEL
-- begin TSADV_DIC_CASE
create table TSADV_DIC_CASE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_CASE
-- begin TSADV_DIC_LEARNING_TYPE
create table TSADV_DIC_LEARNING_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_LEARNING_TYPE
-- begin TSADV_DIC_LANGUAGE
create table TSADV_DIC_LANGUAGE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_LANGUAGE
-- begin TSADV_DIC_ORDER_TYPE
create table TSADV_DIC_ORDER_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_ORDER_TYPE
-- begin TSADV_HARMFUL_FACTOR_TYPE
create table TSADV_HARMFUL_FACTOR_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_HARMFUL_FACTOR_TYPE
-- begin TSADV_DIC_ASSIGNMENT
create table TSADV_DIC_ASSIGNMENT (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_ASSIGNMENT
-- begin TSADV_DIC_COST_TYPE
create table TSADV_DIC_COST_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    IS_BUSINESS_TRIP boolean,
    --
    primary key (ID)
)^
-- end TSADV_DIC_COST_TYPE
-- begin TSADV_DIC_ABSENCE_PURPOSE
create table TSADV_DIC_ABSENCE_PURPOSE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_ABSENCE_PURPOSE
-- begin TSADV_INSPECTION_CATEGORY
create table TSADV_INSPECTION_CATEGORY (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_INSPECTION_CATEGORY
-- begin TSADV_DIC_DISMISSAL_REASON
create table TSADV_DIC_DISMISSAL_REASON (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_DISMISSAL_REASON
-- begin TSADV_EVENT_STATUS
create table TSADV_EVENT_STATUS (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_EVENT_STATUS
-- begin TSADV_DIC_TEST_TYPE
create table TSADV_DIC_TEST_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_TEST_TYPE
-- begin TSADV_DIC_RETIREMENT_TYPE
create table TSADV_DIC_RETIREMENT_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_RETIREMENT_TYPE
-- begin TSADV_ATTACHMENT_TYPE
create table TSADV_ATTACHMENT_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_ATTACHMENT_TYPE
-- begin TSADV_DIC_PHONE_TYPE
create table TSADV_DIC_PHONE_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_PHONE_TYPE
-- begin TSADV_DIC_REASON_BENIFIT
create table TSADV_DIC_REASON_BENIFIT (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_REASON_BENIFIT
-- begin TSADV_DIC_RELATIONSHIP_TYPE
create table TSADV_DIC_RELATIONSHIP_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_RELATIONSHIP_TYPE
-- begin TSADV_DIC_ATTACHMENT_CATEGORY
create table TSADV_DIC_ATTACHMENT_CATEGORY (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_ATTACHMENT_CATEGORY
-- begin TSADV_DIC_ORDER_REASON
create table TSADV_DIC_ORDER_REASON (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_ORDER_REASON
-- begin TSADV_DIC_EXAM_RESULTS
create table TSADV_DIC_EXAM_RESULTS (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_EXAM_RESULTS
-- begin TSADV_UOM
create table TSADV_UOM (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_UOM
-- begin TSADV_DIC_MARITAL_STATUS
create table TSADV_DIC_MARITAL_STATUS (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_MARITAL_STATUS
-- begin TSADV_ACCIDENT_TYPE
create table TSADV_ACCIDENT_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_ACCIDENT_TYPE
-- begin TSADV_DIC_UOM
create table TSADV_DIC_UOM (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_UOM
-- begin TSADV_DIC_EMPLOYMENT_TYPE
create table TSADV_DIC_EMPLOYMENT_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_EMPLOYMENT_TYPE
-- begin TSADV_CONTROL_STAGE
create table TSADV_CONTROL_STAGE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_CONTROL_STAGE
-- begin TSADV_DIC_TROOP_TYPE
create table TSADV_DIC_TROOP_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_TROOP_TYPE
-- begin TSADV_DIC_RECOGNITION_TYPE
create table TSADV_DIC_RECOGNITION_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    COINS bigint not null,
    EMPTY_STICKER_ID uuid,
    ALLOW_COINS_EDIT boolean not null,
    STICKER_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_DIC_RECOGNITION_TYPE
-- begin TSADV_DIC_TRANSPORT_CLASS
create table TSADV_DIC_TRANSPORT_CLASS (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    TRANSPORT_TYPE_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_DIC_TRANSPORT_CLASS
-- begin TSADV_DIC_LC_ARTICLE
create table TSADV_DIC_LC_ARTICLE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    ARTICLE varchar(255) not null,
    ITEM varchar(255),
    SUB_ITEM varchar(255),
    ATTRIBUTE varchar(50),
    --
    primary key (ID)
)^
-- end TSADV_DIC_LC_ARTICLE
-- begin TSADV_DIC_RC_QUESTIONNAIRE_CATEGORY
create table TSADV_DIC_RC_QUESTIONNAIRE_CATEGORY (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_RC_QUESTIONNAIRE_CATEGORY
-- begin TSADV_DIC_PROTECTION_EQUIPMENT_CONDITION
create table TSADV_DIC_PROTECTION_EQUIPMENT_CONDITION (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_PROTECTION_EQUIPMENT_CONDITION
-- begin TSADV_DIC_EDUCATIONAL_ESTABLISHMENT_TYPE
create table TSADV_DIC_EDUCATIONAL_ESTABLISHMENT_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_EDUCATIONAL_ESTABLISHMENT_TYPE
-- begin TSADV_DIC_ADDITIONAL_EDUCATION
create table TSADV_DIC_ADDITIONAL_EDUCATION (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_ADDITIONAL_EDUCATION
-- begin TSADV_DIC_REGISTER_CATEGORY
create table TSADV_DIC_REGISTER_CATEGORY (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_REGISTER_CATEGORY
-- begin TSADV_DIC_ISSUING_AUTHORITY
create table TSADV_DIC_ISSUING_AUTHORITY (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_ISSUING_AUTHORITY
-- begin TSADV_DIC_ATTESTATION_RESULT
create table TSADV_DIC_ATTESTATION_RESULT (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_ATTESTATION_RESULT
-- begin TSADV_DIC_TALENT_PROGRAM_SKILL
create table TSADV_DIC_TALENT_PROGRAM_SKILL (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    TALENT_PROGRAM_STEP_SKILL_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_DIC_TALENT_PROGRAM_SKILL
-- begin TSADV_DIC_EMPLOYEE_CATEGORY
create table TSADV_DIC_EMPLOYEE_CATEGORY (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_EMPLOYEE_CATEGORY
-- begin TSADV_DIC_OPERATOR_CODE
create table TSADV_DIC_OPERATOR_CODE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_OPERATOR_CODE
-- begin TSADV_DIC_NONRESIDENT_TYPE
create table TSADV_DIC_NONRESIDENT_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_NONRESIDENT_TYPE
-- begin TSADV_DIC_ACCOMMODATION_TYPE
create table TSADV_DIC_ACCOMMODATION_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_ACCOMMODATION_TYPE
-- begin TSADV_DIC_PARTICIPANT_TYPE
create table TSADV_DIC_PARTICIPANT_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_PARTICIPANT_TYPE
-- begin TSADV_SUR_CHARGE_NAME
create table TSADV_SUR_CHARGE_NAME (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_SUR_CHARGE_NAME
-- begin TSADV_DIC_CERTIFICATION_STATUS
create table TSADV_DIC_CERTIFICATION_STATUS (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_CERTIFICATION_STATUS
-- begin TSADV_DIC_PREV_JOB_OBLIGATION
create table TSADV_DIC_PREV_JOB_OBLIGATION (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_PREV_JOB_OBLIGATION
-- begin TSADV_DIC_ASSIGNMENT_CATEGORY
create table TSADV_DIC_ASSIGNMENT_CATEGORY (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_ASSIGNMENT_CATEGORY
-- begin TSADV_SUBSECTIONS
create table TSADV_SUBSECTIONS (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_SUBSECTIONS
-- begin TSADV_DIC_INCIDENT_TYPE
create table TSADV_DIC_INCIDENT_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_INCIDENT_TYPE
-- begin TSADV_DIC_EDUCATIONAL_ESTABLISHMENT
create table TSADV_DIC_EDUCATIONAL_ESTABLISHMENT (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    EDUCATIONAL_ESTABLISHMENT_TYPE_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_DIC_EDUCATIONAL_ESTABLISHMENT
-- begin TSADV_DIC_MILITARY_TYPE
create table TSADV_DIC_MILITARY_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_MILITARY_TYPE
-- begin TSADV_DIC_AWARD_TYPE
create table TSADV_DIC_AWARD_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    PROMOTION_TYPE_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_DIC_AWARD_TYPE
-- begin TSADV_DIC_EDUCATION_TYPE
create table TSADV_DIC_EDUCATION_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_EDUCATION_TYPE
-- begin TSADV_INTOXICATION_TYPE
create table TSADV_INTOXICATION_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_INTOXICATION_TYPE
-- begin TSADV_DIC_EDUCATION_DEGREE
create table TSADV_DIC_EDUCATION_DEGREE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_EDUCATION_DEGREE
-- begin TSADV_DIC_COURSE_CYCLE
create table TSADV_DIC_COURSE_CYCLE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_COURSE_CYCLE
-- begin TSADV_DIC_ACCEPTED_ACTION
create table TSADV_DIC_ACCEPTED_ACTION (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_ACCEPTED_ACTION
-- begin TSADV_RISKS_VIOLATIONS
create table TSADV_RISKS_VIOLATIONS (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_RISKS_VIOLATIONS
-- begin TSADV_TRAUMA_LEVEL
create table TSADV_TRAUMA_LEVEL (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_TRAUMA_LEVEL
-- begin TSADV_DIC_PRIORITY
create table TSADV_DIC_PRIORITY (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_PRIORITY
-- begin TSADV_DIC_TROOPS_STRUCTURE
create table TSADV_DIC_TROOPS_STRUCTURE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_TROOPS_STRUCTURE
-- begin TSADV_DIC_FIRE_RESISTANCE
create table TSADV_DIC_FIRE_RESISTANCE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_FIRE_RESISTANCE
-- begin TSADV_DIC_BUSINESS_TRIP_TYPE
create table TSADV_DIC_BUSINESS_TRIP_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    TIMESHEET_CODE varchar(255),
    TIMECARD_WEEKEND_CODE varchar(255),
    WORKING_DAY boolean not null,
    --
    primary key (ID)
)^
-- end TSADV_DIC_BUSINESS_TRIP_TYPE
-- begin TSADV_DIC_RC_QUESTION_CATEGORY
create table TSADV_DIC_RC_QUESTION_CATEGORY (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_RC_QUESTION_CATEGORY
-- begin TSADV_INVESTIGATION_TYPE
create table TSADV_INVESTIGATION_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_INVESTIGATION_TYPE
-- begin TSADV_DIC_ACTION_LIKE_TYPE
create table TSADV_DIC_ACTION_LIKE_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_ACTION_LIKE_TYPE
-- begin TSADV_DIC_OFFICER_TYPE
create table TSADV_DIC_OFFICER_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_OFFICER_TYPE
-- begin TSADV_REABILITATION_TYPE
create table TSADV_REABILITATION_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_REABILITATION_TYPE
-- begin TSADV_DIC_ATTESTATION_INTERVIEW_RESULT
create table TSADV_DIC_ATTESTATION_INTERVIEW_RESULT (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_ATTESTATION_INTERVIEW_RESULT
-- begin TSADV_OBJECT_TYPE
create table TSADV_OBJECT_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_OBJECT_TYPE
-- begin TSADV_DIC_ENTITY_TYPE
create table TSADV_DIC_ENTITY_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_ENTITY_TYPE
-- begin TSADV_DIC_BUDGET_STATUS
create table TSADV_DIC_BUDGET_STATUS (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_BUDGET_STATUS
-- begin TSADV_DIC_PERSON_ACTION_TYPE
create table TSADV_DIC_PERSON_ACTION_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_PERSON_ACTION_TYPE
-- begin TSADV_DIC_LEARNING_HISTORY_STATUS
create table TSADV_DIC_LEARNING_HISTORY_STATUS (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_LEARNING_HISTORY_STATUS
-- begin TSADV_DIC_SCHEDULE_TYPE
create table TSADV_DIC_SCHEDULE_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_SCHEDULE_TYPE
-- begin TSADV_DIC_SCHEDULE_PURPOSE
create table TSADV_DIC_SCHEDULE_PURPOSE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_SCHEDULE_PURPOSE
-- begin TSADV_DIC_BUSINESS_TRIP_LINE
create table TSADV_DIC_BUSINESS_TRIP_LINE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    TRIP_ID uuid not null,
    CITY_ID uuid not null,
    DATE_FROM date not null,
    DATE_TO date not null,
    --
    primary key (ID)
)^
-- end TSADV_DIC_BUSINESS_TRIP_LINE
-- begin TSADV_DIC_DISMISSAL_STATUS
create table TSADV_DIC_DISMISSAL_STATUS (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_DISMISSAL_STATUS
-- begin TSADV_DIC_ATTESTATION_TYPE
create table TSADV_DIC_ATTESTATION_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_ATTESTATION_TYPE
-- begin TSADV_DIC_INTERVIEW_REASON
create table TSADV_DIC_INTERVIEW_REASON (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_INTERVIEW_REASON
-- begin TSADV_DIC_HIRING_MEMBER_TYPE
create table TSADV_DIC_HIRING_MEMBER_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_HIRING_MEMBER_TYPE
-- begin TSADV_DIC_RC_QUESTIONNAIRE_STATUS
create table TSADV_DIC_RC_QUESTIONNAIRE_STATUS (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_RC_QUESTIONNAIRE_STATUS
-- begin TSADV_DIC_REASON_FOR_LEARNING
create table TSADV_DIC_REASON_FOR_LEARNING (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_REASON_FOR_LEARNING
-- begin TSADV_DIC_CERTIFICATE_TYPE
create table TSADV_DIC_CERTIFICATE_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_CERTIFICATE_TYPE
-- begin TSADV_DIC_QUESTIONNAIRE_QUESTION_SECTION
create table TSADV_DIC_QUESTIONNAIRE_QUESTION_SECTION (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_QUESTIONNAIRE_QUESTION_SECTION
-- begin TSADV_DIC_LENGTH_OF_SERVICE_TYPE
create table TSADV_DIC_LENGTH_OF_SERVICE_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_LENGTH_OF_SERVICE_TYPE
-- begin TSADV_DIC_MONTH
create table TSADV_DIC_MONTH (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_MONTH
-- begin TSADV_DIC_OFFENCE_TYPE
create table TSADV_DIC_OFFENCE_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_OFFENCE_TYPE
-- begin TSADV_DIC_ABSENCE_STATUS
create table TSADV_DIC_ABSENCE_STATUS (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_ABSENCE_STATUS
-- begin TSADV_REASON_NO_PRODUCTION_CONNECTION
create table TSADV_REASON_NO_PRODUCTION_CONNECTION (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_REASON_NO_PRODUCTION_CONNECTION
-- begin TSADV_DIC_SCHEDULE_ELEMENT_TYPE
create table TSADV_DIC_SCHEDULE_ELEMENT_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    SHORT_NAME1 varchar(255),
    SHORT_NAME2 varchar(255),
    SHORT_NAME3 varchar(255),
    SHORT_NAME4 varchar(255),
    SHORT_NAME5 varchar(255),
    TIME_FROM time,
    TIME_TO time,
    DISPLAY_ON_TIMECARD_EDIT_SCREEN boolean,
    SHORTNAME varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_DIC_SCHEDULE_ELEMENT_TYPE
-- begin TSADV_DIC_LANGUAGE_LEVEL
create table TSADV_DIC_LANGUAGE_LEVEL (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_LANGUAGE_LEVEL
-- begin TSADV_DIC_REQUISITION_STATUS
create table TSADV_DIC_REQUISITION_STATUS (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_REQUISITION_STATUS
-- begin TSADV_DIC_MEASURE_TYPE
create table TSADV_DIC_MEASURE_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_MEASURE_TYPE
-- begin TSADV_DIC_QUESTION_TYPE
create table TSADV_DIC_QUESTION_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_QUESTION_TYPE
-- begin TSADV_ACCIDENT_PERSON_TYPE
create table TSADV_ACCIDENT_PERSON_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_ACCIDENT_PERSON_TYPE
-- begin TSADV_DIC_SUITABILITY_TO_MILITARY
create table TSADV_DIC_SUITABILITY_TO_MILITARY (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_SUITABILITY_TO_MILITARY
-- begin TSADV_DIC_RCG_FEEDBACK_TYPE
create table TSADV_DIC_RCG_FEEDBACK_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    IMAGE_ID uuid not null,
    COLOR varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_DIC_RCG_FEEDBACK_TYPE
-- begin TSADV_INSPECTION_TYPE
create table TSADV_INSPECTION_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_INSPECTION_TYPE
-- begin TSADV_DIC_TRADE_UNION
create table TSADV_DIC_TRADE_UNION (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_TRADE_UNION
-- begin TSADV_DIC_INTERNSHIP_RATING
create table TSADV_DIC_INTERNSHIP_RATING (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_INTERNSHIP_RATING
-- begin TSADV_DIC_PROTECTION_EQUIPMENT_TYPE
create table TSADV_DIC_PROTECTION_EQUIPMENT_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_PROTECTION_EQUIPMENT_TYPE
-- begin TSADV_DIC_COURSE_FORMAT
create table TSADV_DIC_COURSE_FORMAT (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_COURSE_FORMAT
-- begin TSADV_DIC_ACCOMMODATION_CLASS
create table TSADV_DIC_ACCOMMODATION_CLASS (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    DIC_ACCOMMODATION_TYPE_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_DIC_ACCOMMODATION_CLASS
-- begin TSADV_DIC_ORDER_STATUS
create table TSADV_DIC_ORDER_STATUS (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_ORDER_STATUS
-- begin TSADV_DIC_RATE_TYPE
create table TSADV_DIC_RATE_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_RATE_TYPE
-- begin TSADV_DIC_APPROVAL_STATUS
create table TSADV_DIC_APPROVAL_STATUS (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_APPROVAL_STATUS
-- begin TSADV_DIC_CHANGE_TYPE
create table TSADV_DIC_CHANGE_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_CHANGE_TYPE
-- begin TSADV_TECHNICAL_STATUS_DICTIONARY
create table TSADV_TECHNICAL_STATUS_DICTIONARY (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_TECHNICAL_STATUS_DICTIONARY
-- begin TSADV_DIC_TALENT_PROGRAM_REQUEST_STATUS
create table TSADV_DIC_TALENT_PROGRAM_REQUEST_STATUS (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_TALENT_PROGRAM_REQUEST_STATUS
-- begin TSADV_DIC_ABSENCE_TYPE
create table TSADV_DIC_ABSENCE_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    USE_IN_SELF_SERVICE boolean not null,
    AVAILABLE_TO_MANAGER boolean,
    VACATION_DURATION_TYPE varchar(50),
    ELMA_TRANSFER boolean not null,
    USE_IN_BALANCE boolean not null,
    IGNORE_HOLIDAYS boolean not null,
    IS_ONLY_WORKING_DAY boolean,
    ABSENCE_CATEGORY_ID uuid,
    TIMESHEET_CODE varchar(255),
    IS_WORKING_DAY boolean not null,
    USE_ONLY_ABSENCE_TYPE boolean,
    DISPLAY_ABSENCE boolean,
    CANCEL_PARENT_ABSENCE boolean,
    AVAILABLE_FOR_TIMECARD boolean not null,
    IS_REQUIRED_ORDER_NUMBER boolean not null,
    INCLUDE_CALC_GZP boolean not null,
    MAX_DAY integer,
    MIN_DAY integer,
    DAYS_ADVANCE integer,
    DAYS_BEFORE_ABSENCE integer,
    MANY_DAYS integer,
    AVAILABLE_FOR_RECALL_ABSENCE boolean not null,
    AVAILABLE_FOR_CHANGE_DATE boolean not null,
    AVAILABLE_FOR_LEAVING_VACATION boolean not null,
    IS_JUST_REQUIRED boolean not null,
    IS_ORIGINAL_SHEET boolean not null,
    IS_CHECK_WORK boolean not null,
    IS_VACATION_DATE boolean not null,
    --
    primary key (ID)
)^
-- end TSADV_DIC_ABSENCE_TYPE
-- begin TSADV_DIC_SALARY_CHANGE_REASON
create table TSADV_DIC_SALARY_CHANGE_REASON (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_SALARY_CHANGE_REASON
-- begin TSADV_DIC_POSITION_STATUS
create table TSADV_DIC_POSITION_STATUS (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_POSITION_STATUS
-- begin TSADV_DIC_FORM_STUDY
create table TSADV_DIC_FORM_STUDY (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_FORM_STUDY
-- begin TSADV_DIC_PERSON_AWARD_TYPE
create table TSADV_DIC_PERSON_AWARD_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_PERSON_AWARD_TYPE
-- begin TSADV_DIC_UDO
create table TSADV_DIC_UDO (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_UDO
-- begin TSADV_DIC_LENGTH_OF_SERVICE_RANGE
create table TSADV_DIC_LENGTH_OF_SERVICE_RANGE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    RANGE_ORDER integer,
    MIN_ double precision,
    MAX_ double precision,
    --
    primary key (ID)
)^
-- end TSADV_DIC_LENGTH_OF_SERVICE_RANGE
-- begin TSADV_DIC_COST_CENTER
create table TSADV_DIC_COST_CENTER (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_COST_CENTER
-- begin TSADV_DIC_SOURCE
create table TSADV_DIC_SOURCE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_SOURCE
-- begin TSADV_DIC_CITIZENSHIP
create table TSADV_DIC_CITIZENSHIP (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_CITIZENSHIP
-- begin TSADV_STATUS_MSDS
create table TSADV_STATUS_MSDS (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_STATUS_MSDS
-- begin TSADV_DIC_CATEGORY
create table TSADV_DIC_CATEGORY (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    PARENT_CATEGORY_ID uuid,
    IMAGE bytea,
    --
    primary key (ID)
)^
-- end TSADV_DIC_CATEGORY
-- begin TSADV_RESULT
create table TSADV_RESULT (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_RESULT
-- begin TSADV_DIC_TRANSPORT_TYPE
create table TSADV_DIC_TRANSPORT_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_TRANSPORT_TYPE
-- begin TSADV_DIC_ADDRESS_TYPE
create table TSADV_DIC_ADDRESS_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_ADDRESS_TYPE
-- begin TSADV_DIC_TYPE_OF_WORK
create table TSADV_DIC_TYPE_OF_WORK (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_TYPE_OF_WORK
-- begin TSADV_DIC_REQUISITION_TYPE
create table TSADV_DIC_REQUISITION_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_REQUISITION_TYPE
-- begin TSADV_DIC_DURATION
create table TSADV_DIC_DURATION (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_DURATION
-- begin TSADV_DIC_ABSENCE_CATEGORY
create table TSADV_DIC_ABSENCE_CATEGORY (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_ABSENCE_CATEGORY
-- begin TSADV_DIC_READINESS_LEVEL
create table TSADV_DIC_READINESS_LEVEL (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_READINESS_LEVEL
-- begin TSADV_DIC_PERSON_QUALIFICATION_TYPE
create table TSADV_DIC_PERSON_QUALIFICATION_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_PERSON_QUALIFICATION_TYPE
-- begin TSADV_DIC_LEARNING_OBJECT_TYPE
create table TSADV_DIC_LEARNING_OBJECT_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_LEARNING_OBJECT_TYPE
-- begin TSADV_DIC_HR_ROLE
create table TSADV_DIC_HR_ROLE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_HR_ROLE
-- begin TSADV_DIC_ASSESSMENT_STATUS
create table TSADV_DIC_ASSESSMENT_STATUS (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_ASSESSMENT_STATUS
-- begin TSADV_DIC_MILITARY_RANK
create table TSADV_DIC_MILITARY_RANK (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    TROOP_TYPE_ID uuid,
    CATEGORY_COMPOSITION_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_DIC_MILITARY_RANK
-- begin TSADV_DISABILITY_GROUP
create table TSADV_DISABILITY_GROUP (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DISABILITY_GROUP
-- begin TSADV_DIC_ASSIGNMENT_STATUS
create table TSADV_DIC_ASSIGNMENT_STATUS (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_ASSIGNMENT_STATUS
-- begin TSADV_DIC_GOAL_CATEGORY
create table TSADV_DIC_GOAL_CATEGORY (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_GOAL_CATEGORY
-- begin TSADV_DIC_LEARNING_CENTER
create table TSADV_DIC_LEARNING_CENTER (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    ADDRESS varchar(255),
    LATITUDE double precision,
    LONGITUDE double precision,
    --
    primary key (ID)
)^
-- end TSADV_DIC_LEARNING_CENTER
-- begin TSADV_CONTROL_TYPE
create table TSADV_CONTROL_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_CONTROL_TYPE
-- begin TSADV_DIC_REQUISITION_ACCESS_LEVEL
create table TSADV_DIC_REQUISITION_ACCESS_LEVEL (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_REQUISITION_ACCESS_LEVEL
-- begin TSADV_DIC_NATIONALITY
create table TSADV_DIC_NATIONALITY (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_NATIONALITY
-- begin TSADV_DIC_DOCUMENT_TYPE
create table TSADV_DIC_DOCUMENT_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    FOREIGNER Boolean default false,
    --
    primary key (ID)
)^
-- end TSADV_DIC_DOCUMENT_TYPE
-- begin TSADV_DIC_COMPETENCE_TYPE
create table TSADV_DIC_COMPETENCE_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_COMPETENCE_TYPE
-- begin TSADV_DIC_CONTRACTS_TYPE
create table TSADV_DIC_CONTRACTS_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_CONTRACTS_TYPE
-- begin TSADV_DIC_JOB_REQUEST_STATUS
create table TSADV_DIC_JOB_REQUEST_STATUS (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_JOB_REQUEST_STATUS
-- begin TSADV_DIC_INTERNSHIP_TYPE
create table TSADV_DIC_INTERNSHIP_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_INTERNSHIP_TYPE
-- begin TSADV_DIC_DISABILITY_TYPE
create table TSADV_DIC_DISABILITY_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_DISABILITY_TYPE
-- begin TSADV_DIC_QUESTIONNAIRE_TYPE
create table TSADV_DIC_QUESTIONNAIRE_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_QUESTIONNAIRE_TYPE
-- begin TSADV_PUNISHMENT_TYPE
create table TSADV_PUNISHMENT_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_PUNISHMENT_TYPE
-- begin TSADV_DIC_TRAINING_METHOD
create table TSADV_DIC_TRAINING_METHOD (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_TRAINING_METHOD
-- begin TSADV_DIC_WORKING_CONDITION
create table TSADV_DIC_WORKING_CONDITION (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_WORKING_CONDITION
-- begin TSADV_DIC_PUNISHMENT_TYPES
create table TSADV_DIC_PUNISHMENT_TYPES (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_PUNISHMENT_TYPES
-- begin TSADV_DIRECT_REASON
create table TSADV_DIRECT_REASON (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIRECT_REASON
-- begin TSADV_DIC_GOODS_CATEGORY
create table TSADV_DIC_GOODS_CATEGORY (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    PARENT_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_DIC_GOODS_CATEGORY
-- begin TSADV_DIC_JOB_REQUEST_REASON
create table TSADV_DIC_JOB_REQUEST_REASON (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_JOB_REQUEST_REASON
-- begin TSADV_DIC_LEARNING_FEEDBACK_QUESTION_TYPE
create table TSADV_DIC_LEARNING_FEEDBACK_QUESTION_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_LEARNING_FEEDBACK_QUESTION_TYPE
-- begin TSADV_DIC_BUDGET_ITEM
create table TSADV_DIC_BUDGET_ITEM (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_BUDGET_ITEM
-- begin TSADV_DIC_PROMOTION_TYPE
create table TSADV_DIC_PROMOTION_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_PROMOTION_TYPE
-- begin TSADV_DICDELIVERY_ADDRESS
create table TSADV_DICDELIVERY_ADDRESS (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DICDELIVERY_ADDRESS
-- begin TSADV_DIC_OVERALL_RATING
create table TSADV_DIC_OVERALL_RATING (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_OVERALL_RATING
-- begin TSADV_DIC_PERSON_PREFERENCE_TYPE
create table TSADV_DIC_PERSON_PREFERENCE_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    COINS bigint,
    --
    primary key (ID)
)^
-- end TSADV_DIC_PERSON_PREFERENCE_TYPE
-- begin TSADV_DIC_AGREEMENT_STATUS
create table TSADV_DIC_AGREEMENT_STATUS (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_AGREEMENT_STATUS
-- begin TSADV_DIC_MIC_ATTACHMENT_STATUS
create table TSADV_DIC_MIC_ATTACHMENT_STATUS (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_MIC_ATTACHMENT_STATUS
-- begin TSADV_DIC_PROTECTION_EQUIPMENT
create table TSADV_DIC_PROTECTION_EQUIPMENT (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    GOST varchar(255),
    REPLACEMENT_DURATION integer,
    TYPE_ID uuid,
    IS_SINGLE boolean not null,
    REPLACEMENT_UOM integer,
    UNIT_OF_MEASURE_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_DIC_PROTECTION_EQUIPMENT
-- begin TSADV_INCIDENT_TYPE
create table TSADV_INCIDENT_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_INCIDENT_TYPE
-- begin TSADV_INVESTIGATION_CONDUCTED
create table TSADV_INVESTIGATION_CONDUCTED (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_INVESTIGATION_CONDUCTED
-- begin TSADV_DIC_BOOK_CATEGORY
create table TSADV_DIC_BOOK_CATEGORY (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    PARENT_CATEGORY_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_DIC_BOOK_CATEGORY
-- begin TSADV_DIC_EDUCATION_LEVEL
create table TSADV_DIC_EDUCATION_LEVEL (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_EDUCATION_LEVEL
-- begin TSADV_DIC_CONTACT_PERSON_TYPE
create table TSADV_DIC_CONTACT_PERSON_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_CONTACT_PERSON_TYPE
-- begin TSADV_DIC_JOB_CATEGORY
create table TSADV_DIC_JOB_CATEGORY (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_JOB_CATEGORY
-- begin TSADV_DIC_LEARNING_FORM
create table TSADV_DIC_LEARNING_FORM (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_LEARNING_FORM
-- begin TSADV_DIC_QUALITY
create table TSADV_DIC_QUALITY (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    FULL_LANG_VALUE1 varchar(2000),
    FULL_LANG_VALUE2 varchar(2000),
    FULL_LANG_VALUE3 varchar(2000),
    FULL_LANG_VALUE4 varchar(2000),
    FULL_LANG_VALUE5 varchar(2000),
    FULLLANGVALUE varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_DIC_QUALITY
-- begin TSADV_DIC_TALENT_PROGRAM_STEP
create table TSADV_DIC_TALENT_PROGRAM_STEP (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_TALENT_PROGRAM_STEP
-- begin TSADV_DIC_PERSON_TYPE
create table TSADV_DIC_PERSON_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    SORT_ORDER integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_PERSON_TYPE
-- begin TSADV_INTERNSHIP_EXPENSES
create table TSADV_INTERNSHIP_EXPENSES (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    INTERNSHIP_ID uuid not null,
    EXPENSE_TYPE_ID uuid not null,
    AMOUNT double precision not null,
    CURRENCY_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_INTERNSHIP_EXPENSES
-- begin TSADV_ORG_STRUCTURE_REQUEST_DETAIL
create table TSADV_ORG_STRUCTURE_REQUEST_DETAIL (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ORG_STRUCTURE_REQUEST_ID uuid not null,
    PARENT_ID uuid,
    CHANGE_TYPE varchar(50) not null,
    ORGANIZATION_NAME_RU varchar(1000),
    ORGANIZATION_NAME_EN varchar(1000),
    POSITION_NAME_RU varchar(1000),
    POSITION_NAME_EN varchar(1000),
    ORGANIZATION_GROUP_ID uuid,
    PARENT_ORGANIZATION_GROUP_ID uuid,
    POSITION_GROUP_ID uuid,
    ELEMENT_TYPE integer not null,
    GRADE_GROUP_ID uuid,
    HEAD_COUNT decimal(19, 2),
    --
    primary key (ID)
)^
-- end TSADV_ORG_STRUCTURE_REQUEST_DETAIL
-- begin TSADV_RCG_FAQ
create table TSADV_RCG_FAQ (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ORDER_ integer,
    TITLE_LANG1 varchar(1000) not null,
    TITLE_LANG2 varchar(1000),
    TITLE_LANG3 varchar(1000),
    TITLE_LANG4 varchar(1000),
    TITLE_LANG5 varchar(1000),
    CONTENT_LANG1 text,
    CONTENT_LANG2 text,
    CONTENT_LANG3 text,
    CONTENT_LANG4 text,
    CONTENT_LANG5 text,
    CODE varchar(20),
    --
    primary key (ID)
)^
-- end TSADV_RCG_FAQ
-- begin TSADV_TALENT_PROGRAM
create table TSADV_TALENT_PROGRAM (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PROGRAM_NAME_LANG_1 varchar(255),
    ESSAY_REQUIREMENT_LANG1 text,
    ESSAY_REQUIREMENT_LANG2 text,
    ESSAY_REQUIREMENT_LANG3 text,
    PROGRAM_NAME_LANG_2 varchar(255),
    PROGRAM_NAME_LANG_3 varchar(255),
    IS_ACTIVE boolean,
    START_DATE date,
    END_DATE date,
    PARTICIPATION_RULE_LANG_1 text,
    PARTICIPATION_RULE_LANG_2 text,
    PARTICIPATION_RULE_LANG_3 text,
    BANNER_LANG_1_ID uuid,
    BANNER_LANG_2_ID uuid,
    BANNER_LANG_3_ID uuid,
    WEB_LINK varchar(255),
    QUESTION_OF_ESSAY_RU varchar(255) not null,
    QUESTION_OF_ESSAY_KZ varchar(255) not null,
    QUESTION_OF_ESSAY_EN varchar(255) not null,
    QUESTIONOFESSAY varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_TALENT_PROGRAM
-- begin TSADV_INSURED_PERSON
create table TSADV_INSURED_PERSON (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ATTACH_DATE date not null,
    STATUS_REQUEST_ID uuid not null,
    INSURANCE_CONTRACT_ID uuid not null,
    COMPANY_ID uuid not null,
    EMPLOYEE_ID uuid not null,
    RELATIVE_ID uuid not null,
    FIRST_NAME varchar(50) not null,
    SECOND_NAME varchar(50) not null,
    MIDDLE_NAME varchar(50),
    JOB_ID uuid,
    JOB_MEMBER varchar(255),
    SEX_ID uuid not null,
    IIN varchar(255) not null,
    BIRTHDATE date not null,
    DOCUMENT_TYPE_ID uuid not null,
    DOCUMENT_NUMBER varchar(255) not null,
    REGION_ID uuid not null,
    ADDRESS_ID uuid,
    ADDRESS varchar(255),
    INSURANCE_PROGRAM varchar(500) not null,
    STATEMENT_FILE_ID uuid,
    TYPE varchar(50) not null,
    AMOUNT decimal(19, 2),
    TOTAL_AMOUNT decimal(19, 2) not null,
    EXCLUSION_DATE date,
    COMMENT varchar(500),
    --
    primary key (ID)
)^
-- end TSADV_INSURED_PERSON
-- begin TSADV_GOODS_ORDER_DETAIL
create table TSADV_GOODS_ORDER_DETAIL (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    GOODS_ID uuid not null,
    COMMENT_ varchar(2000),
    EXCLUDED boolean not null,
    QUANTITY bigint not null,
    GOODS_ORDER_ID uuid not null,
    VOUCHER_USED boolean not null,
    QR_CODE varchar(255),
    QR_CODE_IMG_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_GOODS_ORDER_DETAIL
-- begin TSADV_TELEGRAM_ANSWER
create table TSADV_TELEGRAM_ANSWER (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    COMAND_NAME varchar(255) not null,
    --
    primary key (ID)
)^
-- end TSADV_TELEGRAM_ANSWER
-- begin TSADV_PERSON_COIN
create table TSADV_PERSON_COIN (
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
    COINS bigint not null,
    --
    primary key (ID)
)^
-- end TSADV_PERSON_COIN
-- begin TSADV_RECOGNITION_PROFILE_SETTING
create table TSADV_RECOGNITION_PROFILE_SETTING (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    AUTOMATIC_TRANSLATE boolean not null,
    PERSON_GROUP_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_RECOGNITION_PROFILE_SETTING
-- begin TSADV_ABSENCE_TO_ABSENCE_BALANCE
create table TSADV_ABSENCE_TO_ABSENCE_BALANCE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ABSENCE_ID uuid,
    ADDITIONAL_ABSENCE_DAYS integer,
    ABSENCE_BALANCE_ID uuid,
    ABSENCE_DAYS integer,
    --
    primary key (ID)
)^
-- end TSADV_ABSENCE_TO_ABSENCE_BALANCE
-- begin TSADV_COURSE_REVIEW
create table TSADV_COURSE_REVIEW (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    COURSE_ID uuid not null,
    PERSON_GROUP_ID uuid not null,
    RATE double precision,
    TEXT varchar(500) not null,
    --
    primary key (ID)
)^
-- end TSADV_COURSE_REVIEW
-- begin TSADV_RCG_QUESTION_ANSWER
create table TSADV_RCG_QUESTION_ANSWER (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    TEXT_LANG1 varchar(1000),
    TEXT_LANG2 varchar(1000),
    TEXT_LANG3 varchar(1000),
    TEXT_LANG4 varchar(1000),
    TEXT_LANG5 varchar(1000),
    SCORE integer not null,
    CODE varchar(255),
    ICON_ID uuid,
    RCG_QUESTION_ID uuid not null,
    TEXT varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_RCG_QUESTION_ANSWER
-- begin TSADV_JOB_REQUEST_CARD_SETTING
create table TSADV_JOB_REQUEST_CARD_SETTING (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PROPERTY varchar(255),
    USER_ID uuid,
    PROPERTY_VALUE boolean,
    --
    primary key (ID)
)^
-- end TSADV_JOB_REQUEST_CARD_SETTING
-- begin TSADV_BPM_REQUEST_MESSAGE
create table TSADV_BPM_REQUEST_MESSAGE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ENTITY_NAME varchar(255) not null,
    ENTITY_ID uuid not null,
    ENTITY_REQUEST_NUMBER bigint,
    SEND_DATE timestamp,
    MESSAGE varchar(3000),
    ASSIGNED_USER_ID uuid,
    ASSIGNED_BY_ID uuid,
    PARENT_ID uuid,
    LVL integer,
    SCREEN_NAME varchar(255),
    ACTIVITY_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_BPM_REQUEST_MESSAGE
-- begin TSADV_BPM_ROLES_LINK
create table TSADV_BPM_ROLES_LINK (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    BPM_ROLES_DEFINER_ID uuid,
    HR_ROLE_ID uuid not null,
    BPROC_USER_TASK_CODE varchar(255),
    ORDER_ integer not null,
    REQUIRED boolean not null,
    IS_ADDABLE_APPROVER boolean not null,
    FIND_BY_COUNTER boolean not null,
    --
    primary key (ID)
)^
-- end TSADV_BPM_ROLES_LINK
-- begin TSADV_INVESTIGATION_RESULT
create table TSADV_INVESTIGATION_RESULT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    INVESTIGATION_DATE date,
    INVESTIGATION_TYPE_ID uuid not null,
    PRODUCTION_CONNECTION boolean,
    EMPLOYEE_GUILT bigint,
    EMPLOYERGUILT bigint,
    ACCIDEN_INJURED_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_INVESTIGATION_RESULT
-- begin TSADV_SECURITY_PERSON_LIST
create table TSADV_SECURITY_PERSON_LIST (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    SECURITY_GROUP_ID uuid not null,
    PERSON_GROUP_ID uuid not null,
    TRANSACTION_DATE date not null,
    --
    primary key (ID)
)^
-- end TSADV_SECURITY_PERSON_LIST
-- begin TSADV_PERSON_POINT
create table TSADV_PERSON_POINT (
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
    POINTS bigint not null,
    --
    primary key (ID)
)^
-- end TSADV_PERSON_POINT
-- begin TSADV_RECOGNITION_QUALITY
create table TSADV_RECOGNITION_QUALITY (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    RECOGNITION_ID uuid,
    QUALITY_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_RECOGNITION_QUALITY
-- begin TSADV_GOODS_ORDER
create table TSADV_GOODS_ORDER (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    STATUS varchar(50) not null,
    DELIVERY_ADDRESS_ID uuid,
    DISCOUNT integer,
    TOTAL_SUM bigint not null,
    ORDER_NUMBER varchar(255) not null,
    ORDER_DATE timestamp not null,
    PERSON_GROUP_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_GOODS_ORDER
-- begin TSADV_TEMPORARY_TRANSLATION_REQUEST
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
    REQUEST_NUMBER bigint,
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
    ACTUAL_POSITION_GROUP_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_TEMPORARY_TRANSLATION_REQUEST
-- begin TSADV_BUSINESS_RULE
create table TSADV_BUSINESS_RULE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    RULE_CODE varchar(255) not null,
    RULE_NAME varchar(255),
    DESCRIPTION text,
    RULE_STATUS integer not null,
    ERROR_TEXT_LANG1 varchar(1000),
    ERROR_TEXT_LANG2 varchar(1000),
    ERROR_TEXT_LANG3 varchar(1000),
    ERROR_TEXT_LANG4 varchar(1000),
    ERROR_TEXT_LANG5 varchar(1000),
    WARNING_TEXT_LANG1 varchar(1000),
    WARNING_TEXT_LANG2 varchar(1000),
    WARNING_TEXT_LANG3 varchar(1000),
    WARNING_TEXT_LANG4 varchar(1000),
    WARNING_TEXT_LANG5 varchar(1000),
    --
    primary key (ID)
)^
-- end TSADV_BUSINESS_RULE
-- begin TSADV_MEDAL_CONDITION
create table TSADV_MEDAL_CONDITION (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    MEDAL_ID uuid not null,
    QUALITY_ID uuid,
    QUALITY_QUANTITY bigint,
    CHILD_MEDAL_ID uuid,
    MEDAL_QUANTITY bigint,
    --
    primary key (ID)
)^
-- end TSADV_MEDAL_CONDITION
-- begin TSADV_TALENT_PROGRAM_GRADE
create table TSADV_TALENT_PROGRAM_GRADE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    TALENT_PROGRAM_ID uuid,
    GRADE_GROUP_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_TALENT_PROGRAM_GRADE
-- begin TSADV_ORGANIZATION_PROPERTIES
create table TSADV_ORGANIZATION_PROPERTIES (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ORGANIZATION_ID uuid not null,
    FUNCTIONAL_CURRENCY_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_ORGANIZATION_PROPERTIES
-- begin TSADV_PERSON_ACTION
create table TSADV_PERSON_ACTION (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ACTION_LANG1 varchar(60),
    AUTHOR_ID uuid not null,
    RECEIVER_ID uuid not null,
    ACTION_TYPE_ID uuid not null,
    ACTION_LANG2 varchar(60),
    ACTION_LANG3 varchar(60),
    ACTION_LANG4 varchar(60),
    ACTION_LANG5 varchar(60),
    ACTOR_FULLNAME_LANG1 varchar(1000) not null,
    ACTOR_FULLNAME_LANG2 varchar(1000),
    ACTOR_FULLNAME_LANG3 varchar(1000),
    ACTOR_FULLNAME_LANG4 varchar(1000),
    ACTOR_FULLNAME_LANG5 varchar(1000),
    TO_FULLNAME_LANG1 varchar(1000),
    TO_FULLNAME_LANG2 varchar(1000),
    TO_FULLNAME_LANG3 varchar(1000),
    TO_FULLNAME_LANG4 varchar(1000),
    TO_FULLNAME_LANG5 varchar(1000),
    ACTION_DATE date not null,
    COMMENT_ varchar(4000) not null,
    --
    primary key (ID)
)^
-- end TSADV_PERSON_ACTION
-- begin TSADV_LMS_SLIDER
create table TSADV_LMS_SLIDER (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    POSITION_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_LMS_SLIDER
-- begin TSADV_TALENT_PROGRAM_EXCEPTIONS
create table TSADV_TALENT_PROGRAM_EXCEPTIONS (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    TALENT_PROGRAM_ID uuid,
    PERSON_GROUP_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_TALENT_PROGRAM_EXCEPTIONS
-- begin TSADV_RECOGNITION_PROVIDER
create table TSADV_RECOGNITION_PROVIDER (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PROVIDER_NAME_LANG1 varchar(350) not null,
    PROVIDER_NAME_LANG2 varchar(350),
    PROVIDER_NAME_LANG3 varchar(360),
    CONTACT_INFO varchar(350),
    ADDRESS_LANG1 varchar(350),
    ADDRESS_LANG2 varchar(350),
    ADDRESS_LANG3 varchar(350),
    --
    primary key (ID)
)^
-- end TSADV_RECOGNITION_PROVIDER
-- begin TSADV_BPROC_ACTORS
create table TSADV_BPROC_ACTORS (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ENTITY_ID uuid not null,
    HR_ROLE_ID uuid not null,
    USER_ID uuid not null,
    BPROC_USER_TASK_CODE varchar(255) not null,
    --
    primary key (ID)
)^
-- end TSADV_BPROC_ACTORS
-- begin TSADV_COIN_DISTRIBUTION_PERSON
create table TSADV_COIN_DISTRIBUTION_PERSON (
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
    COIN_DISTRIBUTION_RULE_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_COIN_DISTRIBUTION_PERSON
-- begin TSADV_SCORE_SETTING
create table TSADV_SCORE_SETTING (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PERFORMANCE_PLAN_ID uuid,
    MIN_PERCENT double precision,
    MAX_PERCENT double precision,
    FINAL_SCORE integer,
    --
    primary key (ID)
)^
-- end TSADV_SCORE_SETTING
-- begin TSADV_COIN_DISTRIBUTION_JOB
create table TSADV_COIN_DISTRIBUTION_JOB (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    JOB_GROUP_ID uuid not null,
    COIN_DISTRIBUTION_RULE_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_COIN_DISTRIBUTION_JOB
-- begin TSADV_GOODS_RECEIPT
create table TSADV_GOODS_RECEIPT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    GOODS_ID uuid not null,
    QUANTITY bigint not null,
    --
    primary key (ID)
)^
-- end TSADV_GOODS_RECEIPT
-- begin TSADV_GOODS_ORDER_HISTORY
create table TSADV_GOODS_ORDER_HISTORY (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    GOODS_ORDER_ID uuid not null,
    STATUS varchar(50) not null,
    DATE_TIME timestamp not null,
    PERSON_GROUP_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_GOODS_ORDER_HISTORY
-- begin TSADV_GOODS_WISH_LIST
create table TSADV_GOODS_WISH_LIST (
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
    GOODS_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_GOODS_WISH_LIST
-- begin TSADV_SAFETY_PLAN_EVENT
create table TSADV_SAFETY_PLAN_EVENT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PLAN_NAME varchar(255) not null,
    DESCRIPTION text,
    DATE_FROM date not null,
    DATE_TO date not null,
    ACTIVE boolean not null,
    ORGANIZATION_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_SAFETY_PLAN_EVENT
-- begin TSADV_TALENT_PROGRAM_REQUEST
create table TSADV_TALENT_PROGRAM_REQUEST (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    TALENT_PROGRAM_ID uuid,
    STATUS_ID uuid,
    PERSON_GROUP_ID uuid,
    REQUEST_DATE date,
    ESSAY text,
    CURRENT_STEP_ID uuid,
    CURRENT_STEP_STATUS_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_TALENT_PROGRAM_REQUEST
-- begin TSADV_RELATIONSHIP_TYPE_BENEFICIARY
create table TSADV_RELATIONSHIP_TYPE_BENEFICIARY (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PARENT_ID uuid not null,
    CHILD_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_RELATIONSHIP_TYPE_BENEFICIARY
-- begin TSADV_SELECTED_PERSON_AWARD
create table TSADV_SELECTED_PERSON_AWARD (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    AWARD_PROGRAM_ID uuid not null,
    DESCRIPTION_LANG_VALUE1 text,
    DESCRIPTION_LANG_VALUE2 text,
    DESCRIPTION_LANG_VALUE3 text,
    DESCRIPTION_LANG_VALUE4 text,
    DESCRIPTION_LANG_VALUE5 text,
    PERSON_GROUP_ID uuid not null,
    AWARDED boolean not null,
    --
    primary key (ID)
)^
-- end TSADV_SELECTED_PERSON_AWARD
-- begin TSADV_GENERATOR_EMPLOYEE_NUMBER_DEFINER
create table TSADV_GENERATOR_EMPLOYEE_NUMBER_DEFINER (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    GENERATOR_EMPLOYEE_NUMBER_ID uuid,
    PERSON_TYPE_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_GENERATOR_EMPLOYEE_NUMBER_DEFINER
-- begin TSADV_TRAINING_REQUEST
create table TSADV_TRAINING_REQUEST (
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
    LEARNING_TYPE_ID uuid,
    COURSE_ID uuid,
    BUDGET_ID uuid,
    START_DATE date,
    END_DATE date,
    STATUS integer,
    --
    primary key (ID)
)^
-- end TSADV_TRAINING_REQUEST
-- begin TSADV_ASSIGNMENT_SALARY_REQUEST
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
    ACTUAL_POSITION_GROUP_ID uuid,
    ATTACHMENT_ID uuid,
    REQUEST_NUMBER bigint,
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
    SUBSTITUTED_EMPLOYEE_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_ASSIGNMENT_SALARY_REQUEST
-- begin TSADV_COIN_DISTRIBUTION_GRADE
create table TSADV_COIN_DISTRIBUTION_GRADE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    GRADE_GROUP_ID uuid not null,
    COIN_DISTRIBUTION_RULE_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_COIN_DISTRIBUTION_GRADE
-- begin TSADV_PERSON_MEDAL
create table TSADV_PERSON_MEDAL (
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
    MEDAL_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_PERSON_MEDAL
-- begin TSADV_PARTY_CONTACT_PERSON
create table TSADV_PARTY_CONTACT_PERSON (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PARTY_EXT_ID uuid,
    FULL_NAME varchar(255),
    CONTACT_PERSON_TYPE_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_PARTY_CONTACT_PERSON
-- begin TSADV_LEARNER
create table TSADV_LEARNER (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    GROUP_ID uuid not null,
    PERSON_GROUP_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_LEARNER
-- begin TSADV_SECURITY_ORGANIZATION_ELIGIBILITY
create table TSADV_SECURITY_ORGANIZATION_ELIGIBILITY (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    SECURITY_GROUP_ID uuid not null,
    ORGANIZATION_GROUP_ID uuid not null,
    INCLUDE_ Boolean default true,
    --
    primary key (ID)
)^
-- end TSADV_SECURITY_ORGANIZATION_ELIGIBILITY
-- begin TSADV_TALENT_PROGRAM_STEP
create table TSADV_TALENT_PROGRAM_STEP (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    TALENT_PROGRAM_ID uuid,
    ORDER_NUM integer,
    STEP_ID uuid,
    NOTIFICATION_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_TALENT_PROGRAM_STEP
-- begin TSADV_BOOK_REVIEW
create table TSADV_BOOK_REVIEW (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    BOOK_ID uuid not null,
    AUTHOR_ID uuid,
    POST_DATE date,
    REVIEW_TEXT varchar(2000),
    RATING decimal(19, 2),
    --
    primary key (ID)
)^
-- end TSADV_BOOK_REVIEW
-- begin TSADV_GOODS_IMAGE_FOR_REPORT
create table TSADV_GOODS_IMAGE_FOR_REPORT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    GOOD_ID uuid not null,
    PRIMARY_ boolean not null,
    IMAGE_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_GOODS_IMAGE_FOR_REPORT
-- begin TSADV_PERSON_ACTION_LIKE
create table TSADV_PERSON_ACTION_LIKE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ACTION_ID uuid not null,
    LIKE_TYPE_ID uuid not null,
    PERSON_GROUP_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_PERSON_ACTION_LIKE
-- begin TSADV_ATTESTATION_PARTICIPANT
create table TSADV_ATTESTATION_PARTICIPANT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ATTESTATION_ID uuid not null,
    PERSON_GROUP_ID uuid not null,
    ATTESTATION_DATE date not null,
    PASSING_LANGUAGE varchar(50) not null,
    RESULT_ID uuid,
    EVENT_ID uuid,
    INTERVIEW_RESULT_ID uuid,
    COMMISSION_RECOMENDATION varchar(255),
    NOT_APPEARED BOOLEAN DEFAULT FALSE,
    NOT_APPEARED_REASON varchar(255),
    PROTOCOL varchar(255),
    ATTACHMENT_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_ATTESTATION_PARTICIPANT
-- begin TSADV_USER_EXT_JOB_REQUEST_SETING
create table TSADV_USER_EXT_JOB_REQUEST_SETING (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    VIEW_LATER boolean not null,
    USER_EXT_ID uuid,
    JOB_REQUEST_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_USER_EXT_JOB_REQUEST_SETING
-- begin TSADV_LEARNER_GROUP
create table TSADV_LEARNER_GROUP (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    CODE varchar(50) not null,
    ACTIVE boolean not null,
    DESCRIPTION varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_LEARNER_GROUP
-- begin TSADV_REST_INTEGRATION_LOG
create table TSADV_REST_INTEGRATION_LOG (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    REQUEST_ID text,
    LOGIN varchar(1000),
    METHOD_NAME text,
    PARAMS text,
    MESSAGE text,
    SUCCESS boolean not null,
    DATE_TIME timestamp,
    --
    primary key (ID)
)^
-- end TSADV_REST_INTEGRATION_LOG
-- begin TSADV_SECURITY_PERSON_TYPE
create table TSADV_SECURITY_PERSON_TYPE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    SECURITY_GROUP_ID uuid not null,
    PERSON_TYPE_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_SECURITY_PERSON_TYPE
-- begin TSADV_SECURITY_ORGANIZATION_LIST
create table TSADV_SECURITY_ORGANIZATION_LIST (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    SECURITY_GROUP_ID uuid not null,
    ORGANIZATION_GROUP_ID uuid not null,
    TRANSACTION_DATE date not null,
    --
    primary key (ID)
)^
-- end TSADV_SECURITY_ORGANIZATION_LIST
-- begin TSADV_PERSON_AWARD
create table TSADV_PERSON_AWARD (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    TYPE_ID uuid not null,
    AWARD_PROGRAM_ID uuid not null,
    DATE_ date not null,
    AUTHOR_ID uuid not null,
    RECEIVER_ID uuid not null,
    HISTORY varchar(2000) not null,
    WHY varchar(2000) not null,
    STATUS varchar(50) not null,
    --
    primary key (ID)
)^
-- end TSADV_PERSON_AWARD
-- begin TSADV_BPM_USER_SUBSTITUTION
create table TSADV_BPM_USER_SUBSTITUTION (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    SUBSTITUTED_USER_ID uuid not null,
    USER_ID uuid not null,
    START_DATE date not null,
    END_DATE date not null,
    --
    primary key (ID)
)^
-- end TSADV_BPM_USER_SUBSTITUTION
-- begin TSADV_ASSIGNMENT_REQUEST
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
    REQUEST_NUMBER bigint,
    POSITION_GROUP_ID uuid,
    GRADE_GROUP_ID uuid,
    ORGANIZATION_GROUP_ID uuid,
    JOB_GROUP_ID uuid,
    NOTE varchar(4000),
    PERSON_GROUP_ID uuid,
    ATTACHMENT_ID uuid,
    ACTUAL_POSITION_GROUP_ID uuid,
    SUBSTITUTED_EMPLOYEE_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_ASSIGNMENT_REQUEST
-- begin TSADV_ASSIGNED_PERFORMANCE_PLAN
create table TSADV_ASSIGNED_PERFORMANCE_PLAN (
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
    REQUEST_NUMBER bigint not null,
    STATUS_ID uuid not null,
    REQUEST_DATE date not null,
    COMMENT_ varchar(3000),
    --
    PERFORMANCE_PLAN_ID uuid not null,
    RESULT double precision,
    GZP decimal(19, 2),
    ASSIGNED_PERSON_ID uuid not null,
    ASSIGNED_BY_ID uuid,
    STEP_STAGE_STATUS varchar(50),
    START_DATE date,
    END_DATE date,
    KPI_SCORE double precision,
    EXTRA_POINT double precision,
    FINAL_SCORE double precision,
    COMPANY_BONUS double precision,
    PERSONAL_BONUS double precision,
    FINAL_BONUS double precision,
    MAX_BONUS decimal(19, 2),
    ADJUSTED_BONUS double precision,
    ADJUSTED_SCORE double precision,
    MAX_BONUS_PERCENT double precision,
    --
    primary key (ID)
)^
-- end TSADV_ASSIGNED_PERFORMANCE_PLAN
-- begin TSADV_CONTRACT_CONDITIONS
create table TSADV_CONTRACT_CONDITIONS (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    RELATIONSHIP_TYPE_ID uuid not null,
    AGE_MIN integer not null,
    AGE_MAX integer not null,
    IS_FREE boolean not null,
    COST_IN_KZT decimal(19, 2) not null,
    INSURANCE_CONTRACT_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_CONTRACT_CONDITIONS
-- begin TSADV_ADDRESS_REQUEST
create table TSADV_ADDRESS_REQUEST (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ADDRESS_TYPE_ID uuid,
    REQUEST_NUMBER bigint,
    ADDRESS varchar(255),
    COUNTRY_ID uuid,
    POSTAL_CODE varchar(255),
    CITY varchar(255),
    START_DATE date,
    END_DATE date,
    ATTACHMENT_ID uuid,
    STATUS_ID uuid,
    BASE_ADDRESS_ID uuid,
    PERSON_GROUP_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_ADDRESS_REQUEST
-- begin TSADV_IMPORT_LEARNING_LOG
create table TSADV_IMPORT_LEARNING_LOG (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PROCESSED integer,
    LOADING_DATE timestamp not null,
    SUCCESS boolean not null,
    FILE_ID uuid not null,
    ERROR_MESSAGE text,
    --
    primary key (ID)
)^
-- end TSADV_IMPORT_LEARNING_LOG
-- begin TSADV_RCG_QUESTION
create table TSADV_RCG_QUESTION (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    TEXT_LANG1 varchar(1000) not null,
    TEXT_LANG2 varchar(1000),
    TEXT_LANG3 varchar(1000),
    TEXT_LANG4 varchar(1000),
    TEXT_LANG5 varchar(1000),
    DESCRIPTION_LANG1 varchar(1000) not null,
    DESCRIPTION_LANG2 varchar(1000),
    DESCRIPTION_LANG3 varchar(1000),
    DESCRIPTION_LANG4 varchar(1000),
    DESCRIPTION_LANG5 varchar(1000),
    ACTIVE boolean not null,
    ANSWER_TYPE varchar(50) not null,
    COINS bigint,
    TEXT varchar(255),
    DESCRIPTION varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_RCG_QUESTION
-- begin TSADV_COIN_DISTRIBUTION_COST_CENTER
create table TSADV_COIN_DISTRIBUTION_COST_CENTER (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    COST_CENTER_ID uuid not null,
    COIN_DISTRIBUTION_RULE_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_COIN_DISTRIBUTION_COST_CENTER
-- begin TSADV_PERSON_MENTOR
create table TSADV_PERSON_MENTOR (
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
    MENTOR_ID uuid,
    START_DATE date not null,
    END_DATE date not null,
    ORGANIZATION_GROUP_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_PERSON_MENTOR
-- begin TSADV_ORG_ANALYTICS
create table TSADV_ORG_ANALYTICS (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    CALENDAR_ID uuid,
    WORKING_CONDITION_ID uuid,
    OFFSET_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_ORG_ANALYTICS
-- begin TSADV_INTERNSHIP
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
    PERSON_GROUP_ID uuid not null,
    COMMENT_ varchar(1000),
    INTERNSHIP_TYPE_ID uuid not null,
    SCHOOL_ID uuid,
    SPECIALIZATION varchar(255),
    REASON varchar(255),
    AGREEMENT_NUMBER varchar(255),
    AGREEMENT_DATE date,
    START_DATE date not null,
    END_DATE date not null,
    PAYABLE boolean not null,
    MAIN_MENTOR_ID uuid,
    MAIN_MENTOR_REASON varchar(255),
    INTERNSHIP_RATING_ID uuid,
    INTERNSHIP_REASON varchar(1000),
    ORGANIZATION_GROUP_ID uuid,
    ORDER_DATE date,
    MENTOR_ORDER_DATE date,
    --
    primary key (ID)
)^
-- end TSADV_INTERNSHIP
-- begin TSADV_TALENT_PROGRAM_STEP_SKILL
create table TSADV_TALENT_PROGRAM_STEP_SKILL (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ORDER_NUMBER integer,
    TALENT_PROGRAM_STEP_ID uuid,
    SKILL_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_TALENT_PROGRAM_STEP_SKILL
-- begin TSADV_ENROLLMENT_FOR_TRAINING_REQUEST
create table TSADV_ENROLLMENT_FOR_TRAINING_REQUEST (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    REASON varchar(1000),
    PERSON_GROUP_ID uuid,
    STATUS integer,
    DATE_ date,
    MONEY_IN_BUDGET boolean,
    TRAINING_REQUEST_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_ENROLLMENT_FOR_TRAINING_REQUEST
-- begin TSADV_ORGANIZATION_STRUCTURE
create table tsadv_organization_structure (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    HIERARCHY_ID uuid,
    PARENT_ID uuid,
    ELEMENT_TYPE integer,
    ORGANIZATION_GROUP_ID uuid,
    PARENT_ORGANIZATION_GROUP_ID uuid,
    PATH_ORG_NAME1 varchar(255),
    PATH_ORG_NAME2 varchar(255),
    PATH_ORG_NAME3 varchar(255),
    START_DATE date,
    END_DATE date,
    PATH varchar(255),
    _LEVEL integer,
    --
    primary key (ID)
)^
-- end TSADV_ORGANIZATION_STRUCTURE
-- begin TSADV_APP_PROPERTY_ENTITY_DESCRIPTION
create table TSADV_APP_PROPERTY_ENTITY_DESCRIPTION (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    DESCRIPTION varchar(512),
    APP_PROPERTY_NAME varchar(512) not null,
    --
    primary key (ID)
)^
-- end TSADV_APP_PROPERTY_ENTITY_DESCRIPTION
-- begin TSADV_MEDAL
create table TSADV_MEDAL (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    LANG_NAME1 varchar(255) not null,
    LANG_NAME2 varchar(255) not null,
    LANG_NAME5 varchar(255),
    ICON_ID uuid,
    TEMPLATE_ID uuid,
    LANG_NAME3 varchar(255),
    LANG_NAME4 varchar(255),
    SORT integer,
    LANGNAME varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_MEDAL
-- begin TSADV_PERSON_COIN_LOG
create table TSADV_PERSON_COIN_LOG (
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
    COMMENT_ varchar(2000),
    ANOTHER_PERSON_GROUP_ID uuid,
    COIN_TYPE varchar(50) not null,
    ACTION_TYPE varchar(50) not null,
    OPERATION_TYPE varchar(50) not null,
    DATE_ timestamp not null,
    RECOGNITION_ID uuid,
    GOODS_ID uuid,
    QUANTITY bigint not null,
    COIN_DISTRIBUTION_RULE_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_PERSON_COIN_LOG
-- begin TSADV_CORRECTION_COEFFICIENT
create table TSADV_CORRECTION_COEFFICIENT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    GROUP_EFFICIENCY_PERCENT double precision,
    COMPANY_RESULT double precision,
    PERFORMANCE_PLAN_ID uuid,
    COMPANY_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_CORRECTION_COEFFICIENT
-- begin TSADV_LMS_SLIDER_IMAGE
create table TSADV_LMS_SLIDER_IMAGE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    IMAGE_ID uuid,
    URL text,
    ORDER_ integer,
    SLIDER_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_LMS_SLIDER_IMAGE
-- begin TSADV_PERSON_ACTION_OBJECT
create table TSADV_PERSON_ACTION_OBJECT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ACTION_ID uuid not null,
    OBJECT_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_PERSON_ACTION_OBJECT
-- begin TSADV_SAFETY_EVENT
create table TSADV_SAFETY_EVENT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    CODE varchar(30) not null,
    NAME varchar(1000) not null,
    TYPE_ID uuid not null,
    UOM_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_SAFETY_EVENT
-- begin TSADV_IMPORT_HISTORY_LOG
create table TSADV_IMPORT_HISTORY_LOG (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    MESSAGE varchar(255),
    STACKTRACE text,
    LOGIN varchar(255),
    PARAMS text,
    SUCCESS boolean not null,
    DATE_TIME timestamp,
    ENTITIES_PROCESSED integer,
    LEVEL_ varchar(50),
    IMPORT_HISTORY_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_IMPORT_HISTORY_LOG
-- begin TSADV_INSURANCE_CONTRACT_ADMINISTRATOR
create table TSADV_INSURANCE_CONTRACT_ADMINISTRATOR (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NOTIFY_ABOUT_NEW_ATTACHMENTS boolean,
    EMPLOYEE_ID uuid not null,
    INSURANCE_CONTRACT_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_INSURANCE_CONTRACT_ADMINISTRATOR
-- begin TSADV_RECOGNITION_LIKE
create table TSADV_RECOGNITION_LIKE (
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
    RECOGNITION_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_RECOGNITION_LIKE
-- begin TSADV_ENROLLMENT_CERTIFICATE_FILE
create table TSADV_ENROLLMENT_CERTIFICATE_FILE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ENROLLMENT_ID uuid not null,
    CERTIFICATE_FILE_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_ENROLLMENT_CERTIFICATE_FILE
-- begin TSADV_BOOK
create table TSADV_BOOK (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    BOOK_NAME_LANG1 varchar(255),
    BOOK_DESCRIPTION_LANG1 varchar(2000),
    AUTHOR_LANG1 varchar(255),
    PUBLISH_DATE date,
    ISBN varchar(255),
    ACTIVE boolean,
    AVERAGE_SCORE decimal(19, 2),
    CATEGORY_ID uuid,
    IMAGE_ID uuid,
    LANGUAGE_ varchar(50),
    FB2_ID uuid,
    EPUB_ID uuid,
    MOBI_ID uuid,
    KF8_ID uuid,
    PDF_ID uuid,
    DJVU_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_BOOK
-- begin TSADV_GOODS_CART
create table TSADV_GOODS_CART (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    GOODS_ID uuid not null,
    ISSUED boolean not null,
    PERSON_GROUP_ID uuid not null,
    QUANTITY bigint not null,
    --
    primary key (ID)
)^
-- end TSADV_GOODS_CART
-- begin TSADV_ORG_STRUCTURE_REQUEST
create table TSADV_ORG_STRUCTURE_REQUEST (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    MODIFY_DATE date,
    REQUEST_NUMBER bigint not null,
    REQUEST_DATE date not null,
    REQUEST_STATUS_ID uuid not null,
    COMPANY_ID uuid not null,
    DEPARTMENT_ID uuid not null,
    AUTHOR_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_ORG_STRUCTURE_REQUEST
-- begin TSADV_HEALTH_DETERIORATION_WITNESSES
create table TSADV_HEALTH_DETERIORATION_WITNESSES (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PERSON_ID uuid not null,
    HEALTH_DETERIORATION_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_HEALTH_DETERIORATION_WITNESSES
-- begin TSADV_AGREEMENT_DOCUMENT
create table TSADV_AGREEMENT_DOCUMENT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    FILE_ID uuid,
    DESCRIPTION varchar(255),
    AGREEMENT_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_AGREEMENT_DOCUMENT
-- begin TSADV_RECOGNITION_COMMENT
create table TSADV_RECOGNITION_COMMENT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    TEXT varchar(2000) not null,
    TEXT_EN varchar(2000) not null,
    TEXT_RU varchar(2000) not null,
    PARENT_COMMENT_ID uuid,
    AUTHOR_ID uuid not null,
    RECOGNITION_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_RECOGNITION_COMMENT
-- begin TSADV_DIVISION_ELEMENTS_TO_GOAL
create table TSADV_DIVISION_ELEMENTS_TO_GOAL (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    primary key (ID)
)^
-- end TSADV_DIVISION_ELEMENTS_TO_GOAL
-- begin TSADV_TELEGRAM_COMMAND_VARIABLE
create table TSADV_TELEGRAM_COMMAND_VARIABLE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME_VARIABLE varchar(255),
    TELEGRAM_ANSWER_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_TELEGRAM_COMMAND_VARIABLE
-- begin TSADV_PERSON_QUESTION_ANSWER
create table TSADV_PERSON_QUESTION_ANSWER (
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
    QUESTION_ID uuid not null,
    ANSWER_ID uuid not null,
    DATE_ date not null,
    --
    primary key (ID)
)^
-- end TSADV_PERSON_QUESTION_ANSWER
-- begin TSADV_AWARD_PROGRAM
create table TSADV_AWARD_PROGRAM (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(500) not null,
    YEAR_ integer not null,
    ACTIVE boolean not null,
    ORDER_ integer not null,
    --
    primary key (ID)
)^
-- end TSADV_AWARD_PROGRAM
-- begin TSADV_IMPORT_HISTORY
create table TSADV_IMPORT_HISTORY (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    FILE_ID uuid,
    IMPORT_SCENARIO_ID uuid,
    STARTED timestamp,
    FINISHED timestamp,
    --
    primary key (ID)
)^
-- end TSADV_IMPORT_HISTORY
-- begin TSADV_COIN_DISTRIBUTION_POSITION
create table TSADV_COIN_DISTRIBUTION_POSITION (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    POSITION_GROUP_ID uuid not null,
    COIN_DISTRIBUTION_RULE_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_COIN_DISTRIBUTION_POSITION
-- begin TSADV_INSURANCE_CONTRACT
create table TSADV_INSURANCE_CONTRACT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    POLICY_NAME varchar(25) not null,
    CONTRACT varchar(10) not null,
    SIGN_DATE date not null,
    COMPANY_ID uuid not null,
    INSURER varchar(255),
    DEFAULT_DOCUMENT_TYPE_ID uuid,
    DEFAULT_ADDRESS_ID uuid,
    YEAR integer,
    START_DATE date not null,
    EXPIRATION_DATE date not null,
    AVAILABILITY_PERIOD_FROM date not null,
    AVAILABILITY_PERIOD_TO date not null,
    INSURANCE_PROGRAM varchar(500) not null,
    INSURER_CONTACTS varchar(100),
    NOTIFICATION_DATE date not null,
    ATTACHING_AN_EMPLOYEE integer not null,
    ATTACHING_FAMILY integer not null,
    COUNT_OF_FREE_MEMBERS integer,
    --
    primary key (ID)
)^
-- end TSADV_INSURANCE_CONTRACT
-- begin TSADV_REQUISITION_REQUIREMENT
create table TSADV_REQUISITION_REQUIREMENT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    REQUIREMENT_ID uuid not null,
    REQUISITION_ID uuid not null,
    REQUIREMENT_LEVEL_ID uuid not null,
    CRITICAL BOOLEAN DEFAULT TRUE,
    --
    primary key (ID)
)^
-- end TSADV_REQUISITION_REQUIREMENT
-- begin TSADV_COIN_DISTRIBUTION_RULE
create table TSADV_COIN_DISTRIBUTION_RULE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255) not null,
    DESCRIPTION varchar(2000),
    DISTRIBUTION_FREQUENCY integer not null,
    ZERO_FREQUENCY integer not null,
    ACTIVE boolean not null,
    COINS bigint not null,
    --
    primary key (ID)
)^
-- end TSADV_COIN_DISTRIBUTION_RULE
-- begin TSADV_BANNER
create table TSADV_BANNER (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PAGE varchar(255) not null,
    IMAGE_LANG1_ID uuid not null,
    IMAGE_LANG2_ID uuid,
    IMAGE_LANG3_ID uuid,
    ACTIVE boolean not null,
    --
    primary key (ID)
)^
-- end TSADV_BANNER
-- begin TSADV_SECURITY_HIERARCHY_NODE
create table TSADV_SECURITY_HIERARCHY_NODE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    SECURITY_GROUP_ID uuid not null,
    ORGANIZATION_GROUP_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_SECURITY_HIERARCHY_NODE
-- begin TSADV_GENERATOR_EMPLOYEE_NUMBER
create table TSADV_GENERATOR_EMPLOYEE_NUMBER (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255),
    PREFIX varchar(255),
    SUFFIX varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_GENERATOR_EMPLOYEE_NUMBER
-- begin TSADV_BPM_ROLES_DEFINER
create table TSADV_BPM_ROLES_DEFINER (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PROCESS_DEFINITION_KEY varchar(255),
    COMPANY_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_BPM_ROLES_DEFINER
-- begin TSADV_GOODS_IMAGE
create table TSADV_GOODS_IMAGE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    GOOD_ID uuid not null,
    PRIMARY_ boolean not null,
    IMAGE_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_GOODS_IMAGE
-- begin TSADV_COIN_DISTRIBUTION_ORGANIZATION
create table TSADV_COIN_DISTRIBUTION_ORGANIZATION (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PARENT_ORGANIZATION_GROUP_ID uuid,
    HIERARCHY_ID uuid,
    ORGANIZATION_GROUP_ID uuid,
    COIN_DISTRIBUTION_RULE_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_COIN_DISTRIBUTION_ORGANIZATION
-- begin TSADV_GOODS
create table TSADV_GOODS (
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
    NAME_LANG1 varchar(500) not null,
    CATEGORY_ID uuid not null,
    NAME_LANG2 varchar(500),
    NAME_LANG3 varchar(500),
    NAME_LANG4 varchar(500),
    NAME_LANG5 varchar(500),
    DESCRIPTION_LANG1 varchar(2000),
    DESCRIPTION_LANG2 varchar(2000),
    DESCRIPTION_LANG3 varchar(2000),
    DESCRIPTION_LANG4 varchar(2000),
    DESCRIPTION_LANG5 varchar(2000),
    PRICE double precision not null,
    ACTIVE boolean not null,
    RECOGNITION_PROVIDER_ID uuid,
    NAME varchar(255),
    DESCRIPTION varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_GOODS
-- begin TSADV_JOB_REQUEST_HISTORY
create table TSADV_JOB_REQUEST_HISTORY (
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
    JOB_REQUEST_ID uuid not null,
    JOB_REQUEST_STATUS integer not null,
    --
    primary key (ID)
)^
-- end TSADV_JOB_REQUEST_HISTORY
-- begin TSADV_ASSIGNMENT_SCHEDULE
create table TSADV_ASSIGNMENT_SCHEDULE (
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
    ASSIGNMENT_GROUP_ID uuid not null,
    SCHEDULE_ID uuid not null,
    OFFSET_ID uuid not null,
    START_DATE date not null,
    END_DATE date not null,
    COLORS_SET integer not null,
    END_POLICY_CODE varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_ASSIGNMENT_SCHEDULE
-- begin TSADV_PERSON_ADWARD_REQUEST
create table TSADV_PERSON_ADWARD_REQUEST (
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
    REQUEST_STATUS_ID uuid,
    ACADEMIC_DEGREE varchar(2000),
    SCIENTIFIC_WORKS_IVENTIONS varchar(2000),
    STATE_AWARDS varchar(2000),
    --
    primary key (ID)
)^
-- end TSADV_PERSON_ADWARD_REQUEST
-- begin TSADV_GOAL
create table TSADV_GOAL (
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
    GOAL_NAME varchar(1000) not null,
    SUCCESS_CRITERIA varchar(100),
    LIBRARY_ID uuid not null,
    MEASURE_TYPE_ID uuid,
    UOM_ID uuid,
    START_DATE date,
    END_DATE date,
    PARENT_GOAL_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_GOAL
-- begin TSADV_REQUISITION_TMP
create table TSADV_REQUISITION_TMP (
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
    REQUISITION_ID uuid not null,
    CODE varchar(255),
    REQUISITION_TYPE integer,
    REQUISITION_TEMPLATE_ID uuid,
    START_DATE date,
    END_DATE date,
    ORGANIZATION_GROUP_ID uuid,
    JOB_GROUP_ID uuid,
    POSITION_GROUP_ID uuid,
    MANAGER_PERSON_GROUP_ID uuid,
    RECRUITER_PERSON_GROUP_ID uuid,
    LOCATION_ID uuid,
    EMPLOYMENT_TYPE_ID uuid,
    OPENED_POSITIONS_COUNT double precision,
    REQUISITION_STATUS integer,
    FINAL_COLLECT_DATE date,
    NAME_FOR_SITE_LANG1 varchar(255),
    NAME_FOR_SITE_LANG2 varchar(255),
    NAME_FOR_SITE_LANG3 varchar(255),
    NAME_FOR_SITE_LANG4 varchar(255),
    NAME_FOR_SITE_LANG5 varchar(255),
    VIDEO_INTERVIEW_REQUIRED boolean,
    WITHOUT_OFFER boolean not null,
    DESCRIPTION_LANG1 text,
    DESCRIPTION_LANG2 text,
    DESCRIPTION_LANG3 text,
    DESCRIPTION_LANG4 text,
    DESCRIPTION_LANG5 text,
    FOR_SUBSTITUTION boolean not null,
    SUBSTITUTABLE_PERSON_GROUP_ID uuid,
    COST_CENTER_ID uuid,
    MANAGER_DESCRIPTION_LANG1 text,
    MANAGER_DESCRIPTION_LANG2 text,
    MANAGER_DESCRIPTION_LANG3 text,
    MANAGER_DESCRIPTION_LANG4 text,
    MANAGER_DESCRIPTION_LANG5 text,
    --
    primary key (ID)
)^
-- end TSADV_REQUISITION_TMP
-- begin TSADV_BUILDING_TECHNICAL_INSPECTIONS
create table TSADV_BUILDING_TECHNICAL_INSPECTIONS (
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
    INSPECTION_CATEGORY_ID uuid,
    INSPECTION_TYPE_ID uuid not null,
    INSPECTING_FULL_NAME_ID uuid not null,
    BUILD_TECHNICAL_STATUS_ID uuid not null,
    TECHNICAL_STATUS_BUILDSTRUCTURES_ID uuid not null,
    INSPECTION_DATE date not null,
    PRESCRIPTION_NUMBER bigint not null,
    DAMAGE_DESCRIPTION text,
    RECOMMENDATIONS text,
    TECHNICAL_JOURNAL_RECORD text,
    PREPARATION_DEADLINE date,
    ELIMINATE_EVENT boolean,
    ELIMINATION_DEADLINE date,
    ELIMINATION_OR_POSTPONEMENT_NOTIFICATION boolean,
    REPAIR_PROGRAM boolean,
    BUILDINGS_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_BUILDING_TECHNICAL_INSPECTIONS
-- begin TSADV_MICROTRAUMA_WITNESSES
create table TSADV_MICROTRAUMA_WITNESSES (
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
    PERSON_ID uuid not null,
    MICROTRAUM_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_MICROTRAUMA_WITNESSES
-- begin TSADV_RC_QUESTION
create table TSADV_RC_QUESTION (
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
    QUESTION_TYPE varchar(50) not null,
    ANSWER_TYPE varchar(50) not null,
    QUESTION_TEXT1 varchar(2000) not null,
    QUESTION_TEXT2 varchar(2000),
    QUESTION_TEXT3 varchar(2000),
    QUESTION_TEXT4 varchar(2000),
    QUESTION_TEXT5 varchar(2000),
    IS_ACTIVE boolean not null,
    QUESTION_CATEGORY_ID uuid,
    QUESTION_ACCESSIBILITY_ID uuid,
    QUESTIONTEXT varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_RC_QUESTION
-- begin TSADV_LEAVING_VACATION_REQUEST
create table TSADV_LEAVING_VACATION_REQUEST (
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
    REQUEST_NUMBER bigint not null,
    STATUS_ID uuid not null,
    REQUEST_DATE date not null,
    COMMENT_ varchar(3000),
    --
    VACATION_ID uuid,
    START_DATE date,
    END_DATE date,
    PLANNED_START_DATE date,
    ATTACHMENT_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_LEAVING_VACATION_REQUEST
-- begin TSADV_OFFER_TEMPLATE
create table TSADV_OFFER_TEMPLATE (
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
    REPORT_TEMPLATE_ID uuid,
    ORGANIZATION_ID uuid,
    POSITION_ID uuid,
    JOB_ID uuid,
    RC_JOB_GROUP_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_OFFER_TEMPLATE
-- begin TSADV_SHIFT_DETAIL
create table TSADV_SHIFT_DETAIL (
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
    NAME varchar(255) not null,
    TIME_FROM time not null,
    TIME_TO time not null,
    SHIFT_ID uuid,
    ELEMENT_TYPE_ID uuid not null,
    DAY_FROM integer,
    DAY_TO integer,
    --
    primary key (ID)
)^
-- end TSADV_SHIFT_DETAIL
-- begin TSADV_WORKED_HOURS_DETAILED
create table TSADV_WORKED_HOURS_DETAILED (
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
    WORKED_HOURS_SUMMARY_ID uuid not null,
    TIMECARD_HEADER_ID uuid not null,
    WORKED_DATE date not null,
    HOURS double precision not null,
    PLAN_HOURS double precision not null,
    TIME_IN timestamp,
    TIME_OUT timestamp,
    SCHEDULE_ELEMENT_TYPE_ID uuid not null,
    ACTUAL_TIME_IN timestamp,
    ACTUAL_TIME_OUT timestamp,
    --
    primary key (ID)
)^
-- end TSADV_WORKED_HOURS_DETAILED
-- begin TSADV_PERSON_QUESTIONNAIRE
create table TSADV_PERSON_QUESTIONNAIRE (
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
    APPRAISE_ID uuid not null,
    STATUS varchar(50) not null,
    OVERALL_SCORE integer,
    AVERAGE_SCORE double precision,
    APPRAISER_ID uuid not null,
    QUESTIONNAIRE_ID uuid not null,
    APPRAISAL_DATE date not null,
    --
    primary key (ID)
)^
-- end TSADV_PERSON_QUESTIONNAIRE
-- begin TSADV_PERSON_BANKDETAILS_REQUEST
create table TSADV_PERSON_BANKDETAILS_REQUEST (
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
    BANK_ID uuid,
    FULL_NAME_BANK_CARD varchar(2000),
    IBAN varchar(255),
    BIC_BANK varchar(255),
    PERSON_GROUP_ID uuid,
    REQUEST_STATUS_ID uuid,
    FILE_ID uuid,
    BANK_DETAILS_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_PERSON_BANKDETAILS_REQUEST
-- begin TSADV_COURSE_SECTION_SESSION
create table TSADV_COURSE_SECTION_SESSION (
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
    START_DATE timestamp not null,
    TRAINER_ID uuid,
    NAME varchar(255),
    END_DATE timestamp not null,
    LEARNING_CENTER_ID uuid not null,
    MAX_PERSON integer not null,
    COURSE_SECTION_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_COURSE_SECTION_SESSION
-- begin TSADV_CALENDAR
create table TSADV_CALENDAR (
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
    CALENDAR varchar(255) not null,
    DESCRIPTION varchar(500),
    START_DATE date not null,
    END_DATE date not null,
    --
    primary key (ID)
)^
-- end TSADV_CALENDAR
-- begin TSADV_IMPORT_LOG_RECORD
create table TSADV_IMPORT_LOG_RECORD (
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
    MESSAGE varchar(255) not null,
    FULL_NAME varchar(500),
    USER_MESSAGE varchar(500),
    SUCCESS boolean,
    LEVEL_ varchar(50) not null,
    TIME_ timestamp not null,
    STACKTRACE text,
    IMPORT_LOG_ID uuid,
    LINK_SCREEN varchar(255),
    LINK_ENTITY_NAME varchar(255),
    LINK_ENTITY_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_IMPORT_LOG_RECORD
-- begin TSADV_CHILD_DESCRIPTION
create table TSADV_CHILD_DESCRIPTION (
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
    HAVE_DISABLED_CHILD varchar(50),
    HAVE_LITTLE_CHILD varchar(50),
    --
    primary key (ID)
)^
-- end TSADV_CHILD_DESCRIPTION
-- begin TSADV_CERTIFICATION
create table TSADV_CERTIFICATION (
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
    NAME varchar(500) not null,
    COURSE_ID uuid not null,
    NOTIFY_DAY integer not null,
    LIFE_DAY integer not null,
    CALCULATE_TYPE integer not null,
    PERIOD integer not null,
    --
    primary key (ID)
)^
-- end TSADV_CERTIFICATION
-- begin TSADV_SUCCESSOR
create table TSADV_SUCCESSOR (
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
    SUCCESSION_ID uuid not null,
    PERSON_GROUP_ID uuid not null,
    START_DATE date not null,
    END_DATE date not null,
    READINESS_LEVEL_ID uuid,
    NOTE varchar(2000),
    --
    primary key (ID)
)^
-- end TSADV_SUCCESSOR
-- begin TSADV_INTERVIEW_QUESTIONNAIRE
create table TSADV_INTERVIEW_QUESTIONNAIRE (
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
    INTERVIEW_ID uuid not null,
    QUESTIONNAIRE_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_INTERVIEW_QUESTIONNAIRE
-- begin TSADV_PERSON_CRIMINAL_ADMINISTRATIVE_LIABILITY
create table TSADV_PERSON_CRIMINAL_ADMINISTRATIVE_LIABILITY (
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
    TYPE_ID uuid,
    START_DATE_HISTORY date,
    END_DATE_HISTORY date,
    HAVE_LIABILITY varchar(50),
    REASON_PERIOD varchar(2000),
    --
    primary key (ID)
)^
-- end TSADV_PERSON_CRIMINAL_ADMINISTRATIVE_LIABILITY
-- begin TSADV_PERSON_EDUCATION_REQUEST
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
)^
-- end TSADV_PERSON_EDUCATION_REQUEST
-- begin TSADV_ORDER_MASTER_ENTITY
create table TSADV_ORDER_MASTER_ENTITY (
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
    ENTITY_NAME varchar(255) not null,
    DEFAULT_ENTITY boolean not null,
    ENTITY_NAME_LANG1 varchar(255) not null,
    ENTITY_NAME_LANG2 varchar(255),
    ENTITY_NAME_LANG3 varchar(255),
    ENTITY_NAME_LANG4 varchar(255),
    ENTITY_NAME_LANG5 varchar(255),
    ORDER_ integer not null,
    ORDER_MASTER_ID uuid,
    ENTITYLANGNAME varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_ORDER_MASTER_ENTITY
-- begin TSADV_DISABILITY_REQUEST
create table TSADV_DISABILITY_REQUEST (
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
    DISABILITY_TYPE_ID uuid,
    ATTACHMENT_NAME varchar(255),
    ATTACHMENT bytea,
    DURATION_ID uuid,
    DATE_FROM date,
    DATE_TO date,
    PERSON_GROUP_EXT_ID uuid,
    HAVE_DISABILITY varchar(50),
    GROUP_ varchar(2000),
    REQUEST_STATUS_ID uuid,
    FILE_ID uuid,
    DISABILITY_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_DISABILITY_REQUEST
-- begin TSADV_ASSESSMENT_GOAL
create table TSADV_ASSESSMENT_GOAL (
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
    PARTICIPANT_ASSESSMENT_ID uuid not null,
    GOAL_ID uuid not null,
    OVERALL_RATING_ID uuid not null,
    COMMENT_ varchar(2000),
    WEIGHT double precision,
    --
    primary key (ID)
)^
-- end TSADV_ASSESSMENT_GOAL
-- begin TSADV_PERSON_QUALIFICATION
create table TSADV_PERSON_QUALIFICATION (
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
    TYPE_ID uuid not null,
    PERSON_GROUP_ID uuid not null,
    START_DATE date not null,
    END_DATE date,
    ASSIGN_VALIDATION_DATE date,
    ATTACHMENT_ID uuid,
    NOTE varchar(3000),
    EDUCATIONAL_INSTITUTION_NAME varchar(2000),
    DIPLOMA varchar(2000),
    TYPE_NAME varchar(2000),
    ISSUED_DATE date,
    START_DATE_HISTORY date,
    END_DATE_HISTORY date,
    --
    primary key (ID)
)^
-- end TSADV_PERSON_QUALIFICATION
-- begin TSADV_COURSE_SCHEDULE
create table TSADV_COURSE_SCHEDULE (
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
    COURSE_ID uuid,
    NAME varchar(255),
    START_DATE timestamp,
    END_DATE timestamp,
    LEARNING_CENTER_ID uuid,
    ADDRESS varchar(255),
    MAX_NUMBER_OF_PEOPLE integer,
    --
    primary key (ID)
)^
-- end TSADV_COURSE_SCHEDULE
-- begin TSADV_PERSON_BENEFIT_REQUEST
create table TSADV_PERSON_BENEFIT_REQUEST (
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
    PERSON_GROUP_ID uuid,
    REQUEST_STATUS_ID uuid,
    FILE_ID uuid,
    PERSON_BENEFIT_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_PERSON_BENEFIT_REQUEST
-- begin TSADV_ASSESSMENT
create table TSADV_ASSESSMENT (
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
    TEMPLATE_ID uuid not null,
    START_DATE date not null,
    END_DATE date not null,
    EMPLOYEE_PERSON_GROUP_ID uuid not null,
    STATUS_ID uuid not null,
    GOAL_RATING double precision not null,
    COMPETENCE_RATING double precision not null,
    OVERAL_RATING double precision not null,
    PERFORMANCE double precision,
    POTENTIAL double precision,
    RISK_OF_LOSS double precision,
    IMPACT_OF_LOSS double precision,
    ASSESSMENT_NAME varchar(255) not null,
    --
    primary key (ID)
)^
-- end TSADV_ASSESSMENT
-- begin TSADV_RE_LOCATION
create table TSADV_RE_LOCATION (
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
    CITY_ID uuid,
    DESCRIPTION varchar(4000),
    --
    primary key (ID)
)^
-- end TSADV_RE_LOCATION
-- begin TSADV_LEARNING_FEEDBACK_TEMPLATE_QUESTION
create table TSADV_LEARNING_FEEDBACK_TEMPLATE_QUESTION (
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
    FEEDBACK_TEMPLATE_ID uuid not null,
    FEEDBACK_QUESTION_ID uuid not null,
    ORDER_ integer not null,
    --
    primary key (ID)
)^
-- end TSADV_LEARNING_FEEDBACK_TEMPLATE_QUESTION
-- begin TSADV_RECOGNITION
create table TSADV_RECOGNITION (
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
    RECOGNITION_TYPE_ID uuid not null,
    NOTIFY_MANAGER boolean not null,
    RECOGNITION_DATE date not null,
    COMMENT_ varchar(2000) not null,
    COMMENT_EN varchar(2000) not null,
    COMMENT_RU varchar(2000) not null,
    COINS bigint,
    AUTHOR_ID uuid not null,
    RECEIVER_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_RECOGNITION
-- begin TSADV_LEARNING_EXPENSE
create table TSADV_LEARNING_EXPENSE (
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
    EXPENSE_TYPE_ID uuid not null,
    CURRENCY_ID uuid not null,
    AMOUNT integer not null,
    PERSON_LEARNING_CONTRACT_ID uuid,
    PERSON_ENROLLMENT_ID uuid,
    DIC_MONTH_ID uuid,
    YEAR_ integer,
    PERSON_LEARNING_HISTORY_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_LEARNING_EXPENSE
-- begin TSADV_INDIVIDUAL_DEVELOPMENT_PLAN
create table TSADV_INDIVIDUAL_DEVELOPMENT_PLAN (
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
    PLAN_NAME varchar(255) not null,
    STATUS varchar(50) not null,
    START_DATE date not null,
    END_DATE date not null,
    --
    primary key (ID)
)^
-- end TSADV_INDIVIDUAL_DEVELOPMENT_PLAN
-- begin TSADV_QUESTION_ANSWER
create table TSADV_QUESTION_ANSWER (
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
    QUESTION_ID uuid not null,
    ORDER_ integer,
    SCORE integer,
    ICON_ID uuid,
    ANSWER_LANG1 varchar(2000) not null,
    ANSWER_LANG2 varchar(2000),
    ANSWER_LANG3 varchar(2000),
    ANSWER_LANG4 varchar(2000),
    ANSWER_LANG5 varchar(2000),
    CORRECT_ANSWER boolean not null,
    --
    primary key (ID)
)^
-- end TSADV_QUESTION_ANSWER
-- begin TSADV_COURSE_SESSION_ENROLLMENT
create table TSADV_COURSE_SESSION_ENROLLMENT (
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
    ENROLLMENT_ID uuid not null,
    COURSE_SESSION_ID uuid,
    ENROLLMENT_DATE date not null,
    STATUS integer,
    COMMENT_ varchar(1000),
    --
    primary key (ID)
)^
-- end TSADV_COURSE_SESSION_ENROLLMENT
-- begin TSADV_CERTIFICATION_ENROLLMENT
create table TSADV_CERTIFICATION_ENROLLMENT (
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
    START_DATE date not null,
    NEXT_DATE date,
    CERTIFICATION_ID uuid not null,
    STATUS integer not null,
    --
    primary key (ID)
)^
-- end TSADV_CERTIFICATION_ENROLLMENT
-- begin TSADV_ACCIDENT_EVENT
create table TSADV_ACCIDENT_EVENT (
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
    PERSON_ID uuid,
    VIOLATIONS varchar(255) not null,
    ACCIDENT_REASON_REMOVAL varchar(255) not null,
    EXECUTION_PERIOD date not null,
    ATTACHMENT_ID uuid,
    ACCIDENTS_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_ACCIDENT_EVENT
-- begin TSADV_ASSIGNED_GOAL
create table TSADV_ASSIGNED_GOAL (
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
    GOAL_ID uuid,
    PERSON_GROUP_ID uuid,
    ORGANIZATION_GROUP_ID uuid,
    POSITION_GROUP_ID uuid,
    JOB_GROUP_ID uuid,
    PARENT_GOAL_ID uuid,
    TARGET_VALUE integer,
    ACTUAL_VALUE integer,
    SUCCESS_CRITETIA varchar(2000),
    ASSIGNED_BY_PERSON_GROUP_ID uuid,
    START_DATE date,
    END_DATE date,
    WEIGHT double precision,
    PRIORITY_ID uuid,
    ASSIGNED_PERFORMANCE_PLAN_ID uuid,
    CATEGORY_ID uuid,
    GOAL_STRING varchar(255),
    GOAL_TYPE varchar(50),
    RESULT double precision,
    PARENT_ASSIGNED_GOAL_ID uuid,
    GOAL_LIBRARY_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_ASSIGNED_GOAL
-- begin TSADV_DISMISSAL
create table TSADV_DISMISSAL (
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
    ASSIGNMENT_GROUP_ID uuid,
    ORD_ASSIGNMENT_ID uuid,
    ORDER_GROUP_ID uuid,
    DISMISSAL_DATE date not null,
    REQUEST_DATE date not null,
    LC_ARTICLE_ID uuid not null,
    DISMISSAL_REASON_ID uuid,
    STATUS_ID uuid not null,
    ORDER_ID uuid,
    ORDER_NUMBER varchar(255),
    ORDER_DATE date,
    FINAL_CALCULATION_DATE date,
    --
    primary key (ID)
)^
-- end TSADV_DISMISSAL
-- begin TSADV_BUDGET
create table TSADV_BUDGET (
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
    NAME varchar(255) not null,
    DESCRIPTION varchar(2000),
    BUDGET_START_DATE date not null,
    BUDGET_END_DATE date not null,
    REQUEST_START_DATE date not null,
    REQUEST_END_DATE date not null,
    STATUS_ID uuid not null,
    PREVIOUS_BUDGET_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_BUDGET
-- begin TSADV_GOAL_LIBRARY
create table TSADV_GOAL_LIBRARY (
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
    LIBRARY_NAME varchar(240) not null,
    CATEGORY_ID uuid not null,
    START_DATE date,
    END_DATE date,
    --
    primary key (ID)
)^
-- end TSADV_GOAL_LIBRARY
-- begin TSADV_PERSON_LEARNING_CONTRACT
create table TSADV_PERSON_LEARNING_CONTRACT (
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
    CONTRACT_NUMBER varchar(255),
    CONTRACT_DATE date,
    TERM_OF_SERVICE date,
    OTHER text,
    --
    primary key (ID)
)^
-- end TSADV_PERSON_LEARNING_CONTRACT
-- begin TSADV_IMPORT_LOG
create table TSADV_IMPORT_LOG (
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
    FILE_ID uuid not null,
    STARTED timestamp,
    FINISHED timestamp,
    ENTITIES_PROCESSED integer,
    IMPORT_SCENARIO_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_IMPORT_LOG
-- begin TSADV_BENEFICIARY_REQUEST
create table TSADV_BENEFICIARY_REQUEST (
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
    PERSON_GROUP_PARENT_ID uuid,
    PERSON_GROUP_CHILD_ID uuid,
    DATE_FROM date,
    DATE_TO date,
    GET_ALIMONY boolean,
    RELATIONSHIP_TYPE_ID uuid,
    LAST_NAME varchar(2000),
    FIRST_NAME varchar(2000),
    MIDDLE_NAME varchar(2000),
    BIRTH_DATE date,
    WORK_LOCATION varchar(2000),
    HOME_ADDRESS varchar(2000),
    ADDITIONAL_CONTACT varchar(2000),
    PERSON_GROUP_ID uuid,
    REQUEST_STATUS_ID uuid,
    BENEFICIARY_ID uuid,
    RELATED_PERSON_GROUP_ID uuid,
    RELATION_DEGREE_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_BENEFICIARY_REQUEST
-- begin TSADV_PERSON_PREFERENCE
create table TSADV_PERSON_PREFERENCE (
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
    PREFERENCE_TYPE_ID uuid not null,
    DESCRIPTION varchar(2000),
    DESCRIPTION_EN varchar(2000),
    DESCRIPTION_RU varchar(2000),
    --
    primary key (ID)
)^
-- end TSADV_PERSON_PREFERENCE
-- begin TSADV_LEARNING_FEEDBACK_QUESTION
create table TSADV_LEARNING_FEEDBACK_QUESTION (
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
    QUESTION_LANG_VALUE1 varchar(2000) not null,
    QUESTION_LANG_VALUE2 varchar(2000),
    QUESTION_LANG_VALUE3 varchar(2000),
    QUESTION_LANG_VALUE4 varchar(2000),
    QUESTION_LANG_VALUE5 varchar(2000),
    QUESTION_TYPE varchar(50) not null,
    DIC_QUESTION_TYPE_ID uuid,
    QUESTIONLANGVALUE varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_LEARNING_FEEDBACK_QUESTION
-- begin TSADV_HEALTH_DETERIORATION
create table TSADV_HEALTH_DETERIORATION (
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
    DATE_ date not null,
    SICK_PERSON_ID uuid not null,
    MANAGER_PERSON_ID uuid,
    ORGANIZATION_ID uuid not null,
    RESULT_ID uuid,
    TIME_ timestamp,
    START_SHIFT timestamp,
    END_SHIFT timestamp,
    WORK_PLACE_ID uuid,
    PRELIMINARY_DIAGNOSIS varchar(255),
    DESCRIPTION varchar(255),
    REASON varchar(255),
    STICKNESS_START_DATE date,
    STICKNESS_END_DATE date,
    FINAL_DIAGNOSIS varchar(255),
    RESULT_DATE date,
    --
    primary key (ID)
)^
-- end TSADV_HEALTH_DETERIORATION
-- begin TSADV_INTERVIEW
create table TSADV_INTERVIEW (
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
    JOB_REQUEST_ID uuid,
    ATTACHMENT_ID uuid,
    REQUISITION_HIRING_STEP_ID uuid not null,
    INTERVIEW_DATE date,
    INTERVIEW_STATUS integer not null,
    MAIN_INTERVIEWER_PERSON_GROUP_ID uuid,
    TIME_FROM time,
    TIME_TO time,
    SEND_INVITATION_TO_CANDIDATE boolean,
    INTERVIEW_REASON_ID uuid,
    REASON varchar(2000),
    PLACE_ID uuid,
    IS_SCHEDULED boolean not null,
    REQUISITION_ID uuid,
    MAX_CANDIDATES_COUNT integer,
    COMMENT_ text,
    IS_GROUP boolean not null,
    GROUP_INTERVIEW_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_INTERVIEW
-- begin TSADV_VACATION_SCHEDULE
create table TSADV_VACATION_SCHEDULE (
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
    START_DATE date,
    END_DATE date,
    ABSENCE_DAYS integer,
    REQUEST_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_VACATION_SCHEDULE
-- begin TSADV_REQUISITION
create table TSADV_REQUISITION (
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
    CODE varchar(255) not null,
    EXPECTED_SALARY decimal(19, 2),
    REQUISITION_TYPE integer not null,
    REQUISITION_TEMPLATE_ID uuid,
    START_DATE date not null,
    END_DATE date,
    ORGANIZATION_GROUP_ID uuid,
    JOB_GROUP_ID uuid,
    POSITION_GROUP_ID uuid,
    MANAGER_PERSON_GROUP_ID uuid,
    RECRUITER_PERSON_GROUP_ID uuid,
    LOCATION_ID uuid,
    EMPLOYMENT_TYPE_ID uuid,
    OPENED_POSITIONS_COUNT double precision not null,
    REQUISITION_STATUS integer not null,
    FINAL_COLLECT_DATE date,
    NAME_FOR_SITE_LANG1 varchar(255),
    NAME_FOR_SITE_LANG2 varchar(255),
    NAME_FOR_SITE_LANG3 varchar(255),
    NAME_FOR_SITE_LANG4 varchar(255),
    NAME_FOR_SITE_LANG5 varchar(255),
    VIDEO_INTERVIEW_REQUIRED boolean not null,
    WITHOUT_OFFER boolean not null,
    DESCRIPTION_LANG1 text,
    DESCRIPTION_LANG2 text,
    DESCRIPTION_LANG3 text,
    DESCRIPTION_LANG4 text,
    DESCRIPTION_LANG5 text,
    FOR_SUBSTITUTION boolean not null,
    SUBSTITUTABLE_PERSON_GROUP_ID uuid,
    SUBSTITUTABLE_DESCRIPTION varchar(500),
    COST_CENTER_ID uuid,
    MANAGER_DESCRIPTION_LANG1 text,
    MANAGER_DESCRIPTION_LANG2 text,
    MANAGER_DESCRIPTION_LANG3 text,
    MANAGER_DESCRIPTION_LANG4 text,
    MANAGER_DESCRIPTION_LANG5 text,
    VIEW_COUNT bigint not null,
    DESCRIPTIONLANG varchar(255),
    NAMEFORSITELANG varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_REQUISITION
-- begin TSADV_COURSE_TRAINER_ASSESSMENT
create table TSADV_COURSE_TRAINER_ASSESSMENT (
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
    TRAINER_ID uuid not null,
    COURSE_ID uuid not null,
    ASSESSMENT_DATE date not null,
    ASSESSOR_ID uuid,
    SCORE double precision not null,
    --
    primary key (ID)
)^
-- end TSADV_COURSE_TRAINER_ASSESSMENT
-- begin TSADV_INCIDENT
create table TSADV_INCIDENT (
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
    MANAGER_PERSON_ID uuid,
    ORGANIZATION_ID uuid not null,
    INCIDENT_TYPE_ID uuid not null,
    INCIDENT_DATE date not null,
    INCIDENT_TIME timestamp,
    START_SHIFT timestamp,
    END_SHIFT timestamp,
    INCIDENT_CONDITIONS varchar(255) not null,
    INCIDENT_REASON varchar(255),
    DAMAGE varchar(255),
    CORRECTIVE_ACTIONS varchar(255),
    STOPPED_PROCESS boolean,
    WORK_PLACE_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_INCIDENT
-- begin TSADV_COURSE_REQUEST
create table TSADV_COURSE_REQUEST (
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
    BUDGET_ID uuid not null,
    COURSE_ID uuid not null,
    ORGANIZATION_ID uuid not null,
    PERSON_COUNT integer not null,
    MONTH_ID uuid not null,
    INITIATOR_ID uuid not null,
    REASON varchar(2000),
    COURSE_NAME varchar(255),
    BUDGET_SUM bigint,
    --
    primary key (ID)
)^
-- end TSADV_COURSE_REQUEST
-- begin TSADV_ORD_ASSIGNMENT
create table TSADV_ORD_ASSIGNMENT (
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
    ASSIGNMENT_GROUP_ID uuid,
    ORDER_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_ORD_ASSIGNMENT
-- begin TSADV_COURSE_TRAINER
create table TSADV_COURSE_TRAINER (
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
    TRAINER_ID uuid not null,
    COURSE_ID uuid not null,
    DATE_FROM date,
    DATE_TO date,
    --
    primary key (ID)
)^
-- end TSADV_COURSE_TRAINER
-- begin TSADV_HARMFULL_FACTORS
create table TSADV_HARMFULL_FACTORS (
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
    ORGANIZATION_ID uuid not null,
    ENTRY_DATE date not null,
    LABORATORY varchar(255) not null,
    --
    primary key (ID)
)^
-- end TSADV_HARMFULL_FACTORS
-- begin TSADV_RECOGNITION_LOGIN_LOG
create table TSADV_RECOGNITION_LOGIN_LOG (
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
    LOGIN varchar(255) not null,
    SESSION_ID uuid not null,
    DATE_TIME timestamp not null,
    --
    primary key (ID)
)^
-- end TSADV_RECOGNITION_LOGIN_LOG
-- begin TSADV_IMPROVING_PROFESSIONAL_SKILLS_REQUEST
create table TSADV_IMPROVING_PROFESSIONAL_SKILLS_REQUEST (
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
    START_DATE_HISTORY date,
    END_DATE_HISTORY date,
    START_DATE date,
    END_DATE date,
    SPECIALTY varchar(2000),
    DIPLOMA varchar(2000),
    ISSUE_DATE date,
    REQUEST_STATUS_ID uuid,
    FILE_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_IMPROVING_PROFESSIONAL_SKILLS_REQUEST
-- begin TSADV_ATTESTATION_ORGANIZATION
create table TSADV_ATTESTATION_ORGANIZATION (
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
    ATTESTATION_ID uuid not null,
    ORGANIZATION_GROUP_ID uuid not null,
    INCLUDE_CHILD BOOLEAN DEFAULT TRUE,
    --
    primary key (ID)
)^
-- end TSADV_ATTESTATION_ORGANIZATION
-- begin TSADV_STANDARD_SCHEDULE
create table TSADV_STANDARD_SCHEDULE (
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
    SCHEDULE_NAME varchar(255) not null,
    DESCRIPTION varchar(500),
    START_DATE date not null,
    END_DATE date,
    PERIOD integer not null,
    SCHEDULE_TYPE varchar(50) not null,
    BASE_STANDARD_SCHEDULE_ID uuid,
    CALENDAR_ID uuid not null,
    IS_HOLIDAY_WORK_DAY boolean not null,
    --
    primary key (ID)
)^
-- end TSADV_STANDARD_SCHEDULE
-- begin TSADV_TIMECARD_LOG
create table TSADV_TIMECARD_LOG (
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
    INITIATOR_LOGIN varchar(255) not null,
    START_DATE timestamp not null,
    END_DATE timestamp not null,
    SUCCESS boolean not null,
    ERROR_TEXT text,
    DURATION_IN_SECONDS bigint,
    TIMECARDS_COUNT integer,
    --
    primary key (ID)
)^
-- end TSADV_TIMECARD_LOG
-- begin TSADV_POSITION_CHANGE_REQUEST
create table TSADV_POSITION_CHANGE_REQUEST (
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
    REQUEST_NUMBER bigint not null,
    REQUEST_DATE date not null,
    REQUEST_TYPE varchar(50) not null,
    ORGANIZATION_GROUP_ID uuid,
    POSITION_GROUP_ID uuid,
    JOB_NAME_LANG1 varchar(1000),
    JOB_NAME_LANG2 varchar(1000),
    JOB_NAME_LANG3 varchar(1000),
    LOCATION varchar(500),
    GRADE_GROUP_ID uuid,
    COST_CENTER_ID uuid,
    FTE integer,
    EFFECTIVE_DATE date,
    COMMENTS varchar(2000),
    PARENT_POSITION_GROUP_ID uuid,
    MATERIAL_LIABILITY_AGREEMENT_REQUIRED boolean not null,
    MRG_FORM_ID uuid,
    JOB_INSTRUCTION_ID uuid,
    ATTACHMENT_ID uuid,
    STATUS_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_POSITION_CHANGE_REQUEST
-- begin TSADV_RCG_FEEDBACK_ATTACHMENT
create table TSADV_RCG_FEEDBACK_ATTACHMENT (
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
    FILE_ID uuid not null,
    RCG_FEEDBACK_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_RCG_FEEDBACK_ATTACHMENT
-- begin TSADV_CALIBRATION_SESSION
create table TSADV_CALIBRATION_SESSION (
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
    NAME varchar(255) not null,
    DATE_ date not null,
    ADMINISTRATOR_ID uuid not null,
    STATUS varchar(255),
    TEMPLATE_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_CALIBRATION_SESSION
-- begin TSADV_PERSONAL_PROTECTION_VIOLATION
create table TSADV_PERSONAL_PROTECTION_VIOLATION (
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
    PERSONAL_PROTECTION_ID uuid not null,
    PERSONAL_PROTECTION_INSPECTOR_ID uuid not null,
    VIOLATION_DATE date not null,
    VIOLATION_INFO text,
    --
    primary key (ID)
)^
-- end TSADV_PERSONAL_PROTECTION_VIOLATION
-- begin TSADV_FLY_SUR_CHARGE
create table TSADV_FLY_SUR_CHARGE (
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
    POSITION_GROUP_ID_ID uuid,
    ALLOWED boolean,
    ASSIGNMENT_GROUP_ID_ID uuid,
    LEVEL_ varchar(255),
    FLYING_HOURS double precision,
    FLYING_SURCHARGE double precision,
    BONUS double precision,
    RATE_PER_HOUR double precision,
    DATE_FROM date,
    DATE_TO date,
    --
    primary key (ID)
)^
-- end TSADV_FLY_SUR_CHARGE
-- begin TSADV_CALIBRATION_COMISSION
create table TSADV_CALIBRATION_COMISSION (
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
    PERSON_ID uuid not null,
    CALIBRATION_SESSION_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_CALIBRATION_COMISSION
-- begin TSADV_PERSON_CLINIC_DISPANCER_REQUEST
create table TSADV_PERSON_CLINIC_DISPANCER_REQUEST (
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
    REQUEST_STATUS_ID uuid,
    FILE_ID uuid,
    PERSON_CLINIC_DISPANCER_ID uuid,
    HAVE_CLINIC_DISPANCER varchar(50),
    PERIOD_FROM varchar(2000),
    --
    primary key (ID)
)^
-- end TSADV_PERSON_CLINIC_DISPANCER_REQUEST
-- begin TSADV_HIRING_STEP_QUESTIONNAIRE
create table TSADV_HIRING_STEP_QUESTIONNAIRE (
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
    HIRING_STEP_ID uuid not null,
    QUESTIONNAIRE_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_HIRING_STEP_QUESTIONNAIRE
-- begin TSADV_ASSESSMENT_PARTICIPANT
create table TSADV_ASSESSMENT_PARTICIPANT (
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
    ASSESSMENT_ID uuid not null,
    PARTICIPANT_TYPE_ID uuid not null,
    PARTICIPANT_PERSON_GROUP_ID uuid not null,
    GOAL_RATING double precision not null,
    COMPETENCE_RATING double precision not null,
    OVERALL_RATING double precision not null,
    PERFORMANCE_ID uuid,
    POTENTIAL_ID uuid,
    RISK_OF_LOSS_ID uuid,
    IMPACT_OF_LOSS_ID uuid,
    OVERALL_COMMENT varchar(2000),
    --
    primary key (ID)
)^
-- end TSADV_ASSESSMENT_PARTICIPANT
-- begin TSADV_HIRING_STEP_MEMBER
create table TSADV_HIRING_STEP_MEMBER (
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
    HIRING_STEP_ID uuid not null,
    HIRING_MEMBER_TYPE_ID uuid not null,
    ROLE_ID uuid,
    USER_PERSON_GROUP_ID uuid,
    START_DATE date not null,
    END_DATE date,
    MAIN_INTERVIEWER boolean not null,
    --
    primary key (ID)
)^
-- end TSADV_HIRING_STEP_MEMBER
-- begin TSADV_ATTESTATION_JOB
create table TSADV_ATTESTATION_JOB (
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
    ATTESTATION_ID uuid not null,
    JOB_GROUP_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_ATTESTATION_JOB
-- begin TSADV_PERSON_QUESTIONNAIRE_ANSWER
create table TSADV_PERSON_QUESTIONNAIRE_ANSWER (
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
    PERSON_QUESTIONNAIRE_ID uuid not null,
    QUESTION_ID uuid not null,
    ANSWER_ID uuid not null,
    SCORE integer,
    TEXT_ANSWER varchar(2000),
    --
    primary key (ID)
)^
-- end TSADV_PERSON_QUESTIONNAIRE_ANSWER
-- begin TSADV_BUDGET_HEADER_HISTORY
create table TSADV_BUDGET_HEADER_HISTORY (
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
    BUDGET_HEADER_ID uuid not null,
    BUDGET_HEADER_STATUS varchar(50) not null,
    CHANGE_PERSON_ID uuid not null,
    COMMENT_ varchar(1000),
    --
    primary key (ID)
)^
-- end TSADV_BUDGET_HEADER_HISTORY
-- begin TSADV_IMPROVING_PROFESSIONAL_SKILLS
create table TSADV_IMPROVING_PROFESSIONAL_SKILLS (
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
    START_DATE_HISTORY date,
    END_DATE_HISTORY date,
    START_DATE date,
    END_DATE date,
    SPECIALTY varchar(2000),
    DIPLOMA varchar(2000),
    ISSUE_DATE date,
    --
    primary key (ID)
)^
-- end TSADV_IMPROVING_PROFESSIONAL_SKILLS
-- begin TSADV_ORDER_MASTER_ENTITY_PROPERTY
create table TSADV_ORDER_MASTER_ENTITY_PROPERTY (
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
    NAME varchar(255) not null,
    LANG_NAME1 varchar(255) not null,
    LANG_NAME2 varchar(255),
    LANG_NAME3 varchar(255),
    LANG_NAME4 varchar(255),
    LANG_NAME5 varchar(255),
    ORDER_MASTER_ENTITY_ID uuid,
    LANGNAME varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_ORDER_MASTER_ENTITY_PROPERTY
-- begin TSADV_POSITION_TEST
create table TSADV_POSITION_TEST (
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
    POSITION_GROUP_ID uuid not null,
    TEST_ID uuid,
    PURPOSE varchar(50) not null,
    START_DATE date not null,
    END_DATE date not null,
    --
    primary key (ID)
)^
-- end TSADV_POSITION_TEST
-- begin TSADV_PERSON_CONTACT
create table TSADV_PERSON_CONTACT (
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
    CONTACT_VALUE varchar(250) not null,
    END_DATE date not null,
    START_DATE date not null,
    TYPE_ID uuid not null,
    PERSON_GROUP_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_PERSON_CONTACT
-- begin TSADV_COURSE_COMPETENCE
create table TSADV_COURSE_COMPETENCE (
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
    COURSE_ID uuid not null,
    COMPETENCE_GROUP_ID uuid not null,
    SCALE_LEVEL_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_COURSE_COMPETENCE
-- begin TSADV_ATTESTATION
create table TSADV_ATTESTATION (
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
    ATTESTATION_NAME varchar(255),
    ATTESTATION_TYPE_ID uuid not null,
    START_DATE date,
    END_DATE date,
    ORGANIZATION_GROUP_ID uuid,
    POSITION_GROUP_ID uuid,
    JOB_GROUP_ID uuid,
    REASON varchar(1000),
    DOCUMENT_NUMBER varchar(255),
    DOCUMENT_DATE date,
    PERSON_GROUP_EXT_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_ATTESTATION
-- begin TSADV_DISABILITY
create table TSADV_DISABILITY (
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
    DISABILITY_TYPE_ID uuid,
    ATTACHMENT_NAME varchar(255),
    ATTACHMENT bytea,
    DURATION_ID uuid,
    DATE_FROM date,
    DATE_TO date,
    PERSON_GROUP_EXT_ID uuid,
    HAVE_DISABILITY varchar(50),
    GROUP_ varchar(2000),
    START_DATE_HISTORY date,
    END_DATE_HISTORY date,
    --
    primary key (ID)
)^
-- end TSADV_DISABILITY
-- begin TSADV_ENROLLMENT
create table TSADV_ENROLLMENT (
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
    COURSE_ID uuid not null,
    PERSON_GROUP_ID uuid not null,
    STATUS integer not null,
    DATE_ date not null,
    REASON varchar(1000),
    CERTIFICATION_ENROLLMENT_ID uuid,
    MONEY_IN_BUDGET boolean,
    REASON_FOR_LEARNING_ID uuid,
    COURSE_SCHEDULE_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_ENROLLMENT
-- begin TSADV_BUDGET_REQUEST_ITEM_DETAIL
create table TSADV_BUDGET_REQUEST_ITEM_DETAIL (
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
    BUDGET_REQUEST_DETAIL_ID uuid,
    BUDGET_REQUEST_ITEM_ID uuid not null,
    BUDGET_ITEM_ID uuid,
    AMOUNT double precision not null,
    CURRENCY_ID uuid,
    MONTH_ date not null,
    --
    primary key (ID)
)^
-- end TSADV_BUDGET_REQUEST_ITEM_DETAIL
-- begin TSADV_DIC_PROTECTION_EQUIPMENT_PHOTO
create table TSADV_DIC_PROTECTION_EQUIPMENT_PHOTO (
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
    DESCRIPTION text,
    ATTACHMENT_ID uuid,
    DIC_PROTECTION_EQUIPMENT_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_DIC_PROTECTION_EQUIPMENT_PHOTO
-- begin TSADV_JOB_PROTECTION_EQUIPMENT
create table TSADV_JOB_PROTECTION_EQUIPMENT (
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
    JOB_GROUP_ID uuid not null,
    PERSONAL_PROTECTION_EQUIPMENT_ID uuid not null,
    NORM_PER_YEAR integer not null,
    START_DATE date,
    END_DATE date,
    --
    primary key (ID)
)^
-- end TSADV_JOB_PROTECTION_EQUIPMENT
-- begin TSADV_PERSON_RELATIVES_HAVE_PROPERTY
create table TSADV_PERSON_RELATIVES_HAVE_PROPERTY (
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
    HAVE_OR_NOT varchar(50),
    PROPERTY varchar(2000),
    PERSON_GROUP_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_PERSON_RELATIVES_HAVE_PROPERTY
-- begin TSADV_REQUISITION_POSTING_CHANNEL
create table TSADV_REQUISITION_POSTING_CHANNEL (
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
    REQUISITION_ID uuid not null,
    POSTING_CHANNEL_ID uuid,
    START_DATE date,
    END_DATE date,
    --
    primary key (ID)
)^
-- end TSADV_REQUISITION_POSTING_CHANNEL
-- begin TSADV_COURSE_FEEDBACK_TEMPLATE
create table TSADV_COURSE_FEEDBACK_TEMPLATE (
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
    FEEDBACK_TEMPLATE_ID uuid not null,
    COURSE_ID uuid not null,
    START_DATE date not null,
    END_DATE date not null,
    --
    primary key (ID)
)^
-- end TSADV_COURSE_FEEDBACK_TEMPLATE
-- begin TSADV_ATTACHMENT
create table TSADV_ATTACHMENT (
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
    ATTACHMENT_ID uuid,
    ATTACHMENT_TYPE_ID uuid,
    BUILDINGS_ID uuid,
    ACCIDENTS_ID uuid,
    ACCIDEN_INJURED_ID uuid,
    MICROTRAUM_ID uuid,
    INCIDENT_ID uuid,
    HEALTH_DETERIORATION_ID uuid,
    OCCUPATIONAL_MEDICINE_ID uuid,
    SANITARY_HYGIENE_EVENT_ID uuid,
    HARMFULL_FACTORS_ID uuid,
    NOT_ALLOWED_PERSON_ID uuid,
    MEDICAL_INSPECTION_ID uuid,
    INSURANCE_CONTRACT_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_ATTACHMENT
-- begin TSADV_MICROTRAUMA_INJURED
create table TSADV_MICROTRAUMA_INJURED (
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
    PERSON_ID uuid not null,
    CORRECTIVE_ACTIONS varchar(255),
    NOTE_MICROTRAUMA_TO_ACCIDENT varchar(255),
    MICROTRAUM_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_MICROTRAUMA_INJURED
-- begin TSADV_WORK_PLACE
create table TSADV_WORK_PLACE (
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
    NAME varchar(255) not null,
    ORGANIZATION_ID uuid not null,
    JOB_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_WORK_PLACE
-- begin TSADV_QUESTIONNAIRE_RESULT_SCALE
create table TSADV_QUESTIONNAIRE_RESULT_SCALE (
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
    QUESTIONNAIRE_ID uuid not null,
    RESULT_LANG1 varchar(255) not null,
    RESULT_LANG2 varchar(255),
    RESULT_LANG3 varchar(255),
    RESULT_LANG4 varchar(255),
    RESULT_LANG5 varchar(255),
    MIN_ integer not null,
    MAX_ integer not null,
    --
    primary key (ID)
)^
-- end TSADV_QUESTIONNAIRE_RESULT_SCALE
-- begin TSADV_QUESTIONNAIRE
create table TSADV_QUESTIONNAIRE (
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
    DESCRIPTION_LANG1 varchar(4000),
    DESCRIPTION_LANG3 varchar(4000),
    DESCRIPTION_LANG4 varchar(4000),
    DESCRIPTION_LANG5 varchar(4000),
    DESCRIPTION_LANG2 varchar(4000),
    QUESTIONNAIRE_TYPE_ID uuid,
    STATUS_ID uuid not null,
    START_DATE date not null,
    END_DATE date not null,
    QUESTIONNAIRE_NAME_LANG1 varchar(255) not null,
    QUESTIONNAIRE_NAME_LANG2 varchar(255),
    QUESTIONNAIRE_NAME_LANG3 varchar(255),
    QUESTIONNAIRE_NAME_LANG4 varchar(255),
    QUESTIONNAIRE_NAME_LANG5 varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_QUESTIONNAIRE
-- begin TSADV_REQUISITION_QUESTIONNAIRE
create table TSADV_REQUISITION_QUESTIONNAIRE (
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
    REQUISITION_ID uuid not null,
    QUESTIONNAIRE_ID uuid not null,
    WEIGHT double precision,
    --
    primary key (ID)
)^
-- end TSADV_REQUISITION_QUESTIONNAIRE
-- begin TSADV_SANITARY_HYGIENE_EVENT
create table TSADV_SANITARY_HYGIENE_EVENT (
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
    DEVELOPED_EVENTS bigint not null,
    DONE_EVENTS bigint,
    OCCUPATIONAL_MEDICINE_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_SANITARY_HYGIENE_EVENT
-- begin TSADV_CANDIDATE_REQUIREMENT
create table TSADV_CANDIDATE_REQUIREMENT (
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
    REQUIREMENT_ID uuid,
    LEVEL_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_CANDIDATE_REQUIREMENT
-- begin TSADV_CONTRACT
create table TSADV_CONTRACT (
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
    GROUP_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_CONTRACT
-- begin TSADV_LEARNING_PATH_COURSE
create table TSADV_LEARNING_PATH_COURSE (
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
    ORDER_NUMBER integer not null,
    COURSE_ID uuid not null,
    LEARNING_PATH_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_LEARNING_PATH_COURSE
-- begin TSADV_ASSESSMENT_TEMPLATE
create table TSADV_ASSESSMENT_TEMPLATE (
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
    ASSESSMENT_TEMPLATE_NAME varchar(255) not null,
    PERFORMANCE_PLAN_ID uuid,
    SELF_ASSESSMENT boolean not null,
    MANAGER_ASSESSMENT boolean not null,
    PARTICIPANT_ASSESSMENT boolean not null,
    COMPETENCE_TEMPLATE_ID uuid,
    GOAL_WEIGHT double precision,
    COMPETENCE_WEIGHT double precision,
    MANAGER_TEST_ID uuid,
    WORKER_TEST_ID uuid,
    PARTICIPANT_TEST_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_ASSESSMENT_TEMPLATE
-- begin TSADV_RETIREMENT
create table TSADV_RETIREMENT (
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
    RETIREMENT_TYPE_ID uuid,
    ISSEU_DOC_DATE date,
    START_DATE_HISTORY date,
    END_DATE_HISTORY date,
    DOCUMENT_NUMBER varchar(255),
    DATE_FROM date,
    DATE_TO date,
    PERSON_GROUP_EXT_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_RETIREMENT
-- begin TSADV_ANALYTICS
create table TSADV_ANALYTICS (
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
    ANALYTICS_TYPE varchar(255) not null,
    DATA_TYPE varchar(50) not null,
    ACTIVE boolean,
    ORGANIZATION boolean,
    JOB boolean,
    GRADE boolean,
    POSITION_ boolean,
    JOB_CATEGORY boolean,
    PERSON_CATEGORY boolean,
    --
    primary key (ID)
)^
-- end TSADV_ANALYTICS
-- begin TSADV_SCHEDULE_HEADER
create table TSADV_SCHEDULE_HEADER (
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
    SCHEDULE_ID uuid not null,
    MONTH_ date,
    OFFSET_ID uuid,
    IS_LOCKED boolean not null,
    BASE_DAYS integer,
    BASE_HOURS double precision,
    PLAN_DAYS integer,
    PLAN_HOURS double precision,
    PLAN_HOURS_PART double precision,
    PLAN_HOURS_MONTH double precision,
    NIGHT_HOURS double precision,
    WEEKEND_DAYS integer,
    HOLIDAY_DAYS integer,
    HOLIDAY_WORK_DAYS integer,
    HOLIDAY_WORK_HOURS double precision,
    --
    primary key (ID)
)^
-- end TSADV_SCHEDULE_HEADER
-- begin TSADV_ABS_PURPOSE_SETTING
create table TSADV_ABS_PURPOSE_SETTING (
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
    ABSENCE_TYPE_ID uuid,
    ABSENCE_PURPOSE_ID uuid,
    ORDER_NUMBER integer,
    --
    primary key (ID)
)^
-- end TSADV_ABS_PURPOSE_SETTING
-- begin TSADV_FAQ_CONTENT
create table TSADV_FAQ_CONTENT (
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
    LANG_VALUE1 text not null,
    LANG_VALUE2 text,
    LANG_VALUE3 text,
    LANG_VALUE4 text,
    LANG_VALUE5 text,
    LANGVALUE varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_FAQ_CONTENT
-- begin TSADV_COMPLAINT
create table TSADV_COMPLAINT (
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
    COMPLAINTS varchar(255) not null,
    INDEX_ varchar(255),
    HEALTH_DETERIORATION_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_COMPLAINT
-- begin TSADV_PERSONAL_PROTECTION_INSPECTOR
create table TSADV_PERSONAL_PROTECTION_INSPECTOR (
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
    EMPLOYEE_ID uuid not null,
    ORGANIZATION_GROUP_ID uuid not null,
    ASSIGNMENT_DATE date not null,
    ASSIGNMENT_ORDER varchar(255),
    EMPLOYEEFULLNAME varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_PERSONAL_PROTECTION_INSPECTOR
-- begin TSADV_FLIGHT_TIME_RATE
create table TSADV_FLIGHT_TIME_RATE (
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
    VALUE_ double precision,
    POSITION_GROUP_NAME_ID uuid,
    DIC_RATE_TYPE_ID uuid,
    DATE_FROM date,
    DATE_TO date,
    HOURS_FROM double precision,
    HOURS_TO double precision,
    --
    primary key (ID)
)^
-- end TSADV_FLIGHT_TIME_RATE
-- begin TSADV_WITNESSES
create table TSADV_WITNESSES (
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
    PERSON_ID uuid not null,
    TYPE_ID uuid not null,
    ACCIDENTS_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_WITNESSES
-- begin TSADV_COURSE_FEEDBACK_PERSON_ANSWER_DETAIL
create table TSADV_COURSE_FEEDBACK_PERSON_ANSWER_DETAIL (
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
    FEEDBACK_TEMPLATE_ID uuid not null,
    QUESTION_ORDER integer,
    COURSE_ID uuid,
    COURSE_SECTION_SESSION_ID uuid,
    PERSON_GROUP_ID uuid not null,
    QUESTION_ID uuid not null,
    ANSWER_ID uuid,
    TEXT_ANSWER varchar(2000),
    SCORE integer not null,
    COURSE_FEEDBACK_PERSON_ANSWER_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_COURSE_FEEDBACK_PERSON_ANSWER_DETAIL
-- begin TSADV_INTERVIEW_ANSWER
create table TSADV_INTERVIEW_ANSWER (
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
    INTERVIEW_QUESTION_ID uuid not null,
    WEIGHT double precision,
    ANSWER_ID uuid,
    TEXT_ANSWER varchar(4000),
    DATE_ANSWER date,
    BOOLEAN_ANSWER boolean,
    ORDER_ integer,
    NUMBER_ANSWER double precision,
    --
    primary key (ID)
)^
-- end TSADV_INTERVIEW_ANSWER
-- begin TSADV_BUDGET_REQUEST
create table TSADV_BUDGET_REQUEST (
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
    BUDGET_ID uuid not null,
    REQUIRED_EDUCATION_ID uuid,
    EMPLOYEE_CATEGORY_ID uuid,
    TRAINING_METHOD_ID uuid,
    TRAINING_SUBJECT varchar(255),
    EDUCATION_ON_WORK boolean,
    COURSE_ID uuid,
    COURSE_NAME varchar(255),
    LEARNING_TYPE_ID uuid not null,
    EMPLOYEES_COUNT integer,
    MONTH_ date,
    LEARNING_COSTS double precision,
    TRIP_COSTS double precision,
    PROVIDER_COMPANY_ID uuid,
    COMMENT_ varchar(2000),
    INITIATOR_PERSON_GROUP_ID uuid,
    ORGANIZATION_GROUP_ID uuid,
    STATUS_ID uuid,
    BUDGET_HEADER_ID uuid not null,
    REASON varchar(255),
    CITY_ID uuid,
    DAY_ integer,
    HOUR_ integer,
    BUSINESS_TRIP_EMPLOYEE integer,
    BUDGET_ITEM_ID uuid,
    CALCCOURSENAME varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_BUDGET_REQUEST
-- begin TSADV_TIMECARD_HEADER
create table TSADV_TIMECARD_HEADER (
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
    ASSIGNMENT_SCHEDULE_ID uuid not null,
    ASSIGNMENT_GROUP_ID uuid not null,
    PAYROLL_ID uuid,
    PERIOD_START_DATE date,
    PERIOD_END_DATE date,
    EFFECTIVE_START_DATE date not null,
    EFFECTIVE_END_DATE date not null,
    DOCUMENT_VERSION integer,
    STATUS integer,
    TYPE_ varchar(50),
    TIMECARD_CORRECTION_ID uuid,
    BASE_WORK_HOURS double precision not null,
    BASE_WORK_DAYS integer not null,
    PLAN_WORK_DAYS integer not null,
    PLAN_WORK_HOURS double precision not null,
    PLAN_WORK_HOURS_PART double precision not null,
    FACT_WORK_DAYS integer not null,
    FACT_HOURS_WITHOUT_OVERTIME double precision not null,
    WEEKEND_HOURS double precision not null,
    WEEKEND_DAYS integer not null,
    DAY_HOURS double precision not null,
    NIGHT_HOURS double precision not null,
    HOLIDAY_HOURS double precision not null,
    HOLIDAY_DAYS integer not null,
    ANNUAL_VACATION_DAYS integer not null,
    UNPAID_VACATION_DAYS integer not null,
    MATERNITY_VACATION_DAYS integer not null,
    CHILDCARE_VACATION_DAYS integer not null,
    SICK_DAYS integer not null,
    ABSENCE_DAYS integer not null,
    TOTAL_FREE_DAYS integer not null,
    BUS_TRIP_DAYS integer not null,
    TOTAL_WORKED_DAYS integer not null,
    TOTAL_ABSENCE integer not null,
    GRAND_TOTAL_DAYS integer not null,
    OVERTIME_HOURS double precision not null,
    ATTRIBUTE1 varchar(255),
    ATTRIBUTE2 varchar(255),
    ATTRIBUTE3 varchar(255),
    ATTRIBUTE4 varchar(255),
    ATTRIBUTE5 varchar(255),
    ATTRIBUTE6 varchar(255),
    ATTRIBUTE7 varchar(255),
    ATTRIBUTE8 varchar(255),
    ATTRIBUTE9 varchar(255),
    ATTRIBUTE10 varchar(255),
    ATTRIBUTE11 varchar(255),
    ATTRIBUTE12 varchar(255),
    ATTRIBUTE13 varchar(255),
    ATTRIBUTE14 varchar(255),
    ATTRIBUTE15 varchar(255),
    ATTRIBUTE16 varchar(255),
    ATTRIBUTE17 varchar(255),
    ATTRIBUTE18 varchar(255),
    ATTRIBUTE19 varchar(255),
    ATTRIBUTE20 varchar(255),
    ATTRIBUTE21 varchar(255),
    ATTRIBUTE22 varchar(255),
    ATTRIBUTE23 varchar(255),
    ATTRIBUTE24 varchar(255),
    ATTRIBUTE25 varchar(255),
    ATTRIBUTE26 varchar(255),
    ATTRIBUTE27 varchar(255),
    ATTRIBUTE28 varchar(255),
    ATTRIBUTE29 varchar(255),
    ATTRIBUTE30 varchar(255),
    ATTRIBUTE31 varchar(255),
    ATTRIBUTE32 varchar(255),
    ATTRIBUTE33 varchar(255),
    ATTRIBUTE34 varchar(255),
    ATTRIBUTE35 varchar(255),
    ATTRIBUTE36 varchar(255),
    ATTRIBUTE37 varchar(255),
    ATTRIBUTE38 varchar(255),
    ATTRIBUTE39 varchar(255),
    ATTRIBUTE40 varchar(255),
    ATTRIBUTE41 varchar(255),
    ATTRIBUTE42 varchar(255),
    ATTRIBUTE43 varchar(255),
    ATTRIBUTE44 varchar(255),
    ATTRIBUTE45 varchar(255),
    ATTRIBUTE46 varchar(255),
    ATTRIBUTE47 varchar(255),
    ATTRIBUTE48 varchar(255),
    ATTRIBUTE49 varchar(255),
    ATTRIBUTE50 varchar(255),
    ATTRIBUTE51 varchar(255),
    ATTRIBUTE52 varchar(255),
    ATTRIBUTE53 varchar(255),
    ATTRIBUTE54 varchar(255),
    ATTRIBUTE55 varchar(255),
    ATTRIBUTE56 varchar(255),
    ATTRIBUTE57 varchar(255),
    ATTRIBUTE58 varchar(255),
    ATTRIBUTE59 varchar(255),
    ATTRIBUTE60 varchar(255),
    ATTRIBUTE61 varchar(255),
    ATTRIBUTE62 varchar(255),
    ATTRIBUTE63 varchar(255),
    ATTRIBUTE64 varchar(255),
    ATTRIBUTE65 varchar(255),
    ATTRIBUTE66 varchar(255),
    ATTRIBUTE67 varchar(255),
    ATTRIBUTE68 varchar(255),
    ATTRIBUTE69 varchar(255),
    ATTRIBUTE70 varchar(255),
    ATTRIBUTE71 varchar(255),
    ATTRIBUTE72 varchar(255),
    ATTRIBUTE73 varchar(255),
    ATTRIBUTE74 varchar(255),
    ATTRIBUTE75 varchar(255),
    ATTRIBUTE76 varchar(255),
    ATTRIBUTE77 varchar(255),
    ATTRIBUTE78 varchar(255),
    ATTRIBUTE79 varchar(255),
    ATTRIBUTE80 varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_TIMECARD_HEADER
-- begin TSADV_IDENTIFIED_VIOLATIONS
create table TSADV_IDENTIFIED_VIOLATIONS (
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
    ENTITY_TYPE varchar(255) not null,
    RISKS_VIOLATIONS_ID uuid,
    SUBSECTIONS_ID uuid,
    TYPE_ID uuid,
    RESPONSIBLE_ID uuid,
    DESCRIPTION text,
    VIOLATION_DATE date,
    ELIMINATION_PLAN_DATE date,
    ELIMINATION_FACT_DATE date,
    IDENTIFIE_BY_WORKERS boolean,
    --
    primary key (ID)
)^
-- end TSADV_IDENTIFIED_VIOLATIONS
-- begin TSADV_HR_USER_ROLE
create table TSADV_HR_USER_ROLE (
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
    ROLE_ID uuid not null,
    USER_ID uuid not null,
    DATE_FROM date not null,
    DATE_TO date not null,
    --
    primary key (ID)
)^
-- end TSADV_HR_USER_ROLE
-- begin TSADV_BUDGET_HEADER
create table TSADV_BUDGET_HEADER (
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
    BUDGET_ID uuid not null,
    RESPONSIBLE_PERSON_ID uuid,
    HEADER_NAME varchar(255) not null,
    ORGANIZATION_GROUP_ID uuid not null,
    STATUS varchar(50) not null,
    --
    primary key (ID)
)^
-- end TSADV_BUDGET_HEADER
-- begin TSADV_PERSON_DOCUMENT
create table TSADV_PERSON_DOCUMENT (
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
    ISSUE_DATE date not null,
    START_DATE date,
    END_DATE date,
    EXPIRED_DATE date not null,
    ISSUED_BY varchar(500),
    ISSUING_AUTHORITY_ID uuid,
    DESCRIPTION varchar(2000),
    DOCUMENT_TYPE_ID uuid not null,
    PERSON_GROUP_ID uuid,
    DOCUMENT_NUMBER varchar(255) not null,
    SERIES varchar(255),
    STATUS_ID uuid,
    FILE_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_PERSON_DOCUMENT
-- begin TSADV_PERSON_REASON_CHANGING_JOB
create table TSADV_PERSON_REASON_CHANGING_JOB (
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
    REASON text,
    TEL_FULL_NAME_HR varchar(2000),
    PERSON_GROUP_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_PERSON_REASON_CHANGING_JOB
-- begin TSADV_PERSON_ATTACHMENT
create table TSADV_PERSON_ATTACHMENT (
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
    CATEGORY_ID uuid not null,
    FILENAME varchar(255) not null,
    DESCRIPTION varchar(2000),
    ATTACHMENT_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_PERSON_ATTACHMENT
-- begin TSADV_SUR_CHARGE
create table TSADV_SUR_CHARGE (
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
    NAME_ID uuid not null,
    CURRENCY_ID uuid,
    REASON varchar(1000),
    GROSS_NET varchar(50),
    PERIOD integer not null,
    TYPE_ integer not null,
    DATE_FROM date,
    DATE_TO date,
    POSITION_GROUP_ID uuid,
    ASSIGNMENT_GROUP_ID uuid,
    VALUE_ double precision not null,
    --
    primary key (ID)
)^
-- end TSADV_SUR_CHARGE
-- begin TSADV_REQUISITION_COMPETENCE
create table TSADV_REQUISITION_COMPETENCE (
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
    REQUISITION_ID uuid not null,
    COMPETENCE_GROUP_ID uuid not null,
    SCALE_LEVEL_ID uuid not null,
    CRITICALNESS integer not null,
    --
    primary key (ID)
)^
-- end TSADV_REQUISITION_COMPETENCE
-- begin TSADV_SHIFT
create table TSADV_SHIFT (
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
    NAME varchar(255) not null,
    CODE varchar(255),
    DESCRIPTION varchar(500),
    DATE_FROM date not null,
    DATE_TO date not null,
    --
    primary key (ID)
)^
-- end TSADV_SHIFT
-- begin TSADV_SCALE_LEVEL
create table TSADV_SCALE_LEVEL (
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
    LEVEL_NUMBER integer not null,
    LEVEL_NAME_LANG1 varchar(1000) not null,
    LEVEL_NAME_LANG2 varchar(1000),
    LEVEL_NAME_LANG3 varchar(1000),
    LEVEL_NAME_LANG4 varchar(1000),
    LEVEL_NAME_LANG5 varchar(1000),
    LEVEL_DESCRIPTION_LANG1 varchar(4000),
    LEVEL_DESCRIPTION_LANG2 varchar(4000),
    LEVEL_DESCRIPTION_LANG3 varchar(4000),
    LEVEL_DESCRIPTION_LANG4 varchar(4000),
    LEVEL_DESCRIPTION_LANG5 varchar(4000),
    SCALE_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_SCALE_LEVEL
-- begin TSADV_ATTESTATION_POSITION
create table TSADV_ATTESTATION_POSITION (
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
    ATTESTATION_ID uuid not null,
    POSITION_GROUP_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_ATTESTATION_POSITION
-- begin TSADV_SCALE
create table TSADV_SCALE (
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
    SCALE_NAME varchar(250) not null,
    --
    primary key (ID)
)^
-- end TSADV_SCALE
-- begin TSADV_PERSON_EXPERIENCE
create table TSADV_PERSON_EXPERIENCE (
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
    UNTIL_NOW boolean,
    COMPANY varchar(255) not null,
    JOB varchar(255) not null,
    START_MONTH date not null,
    END_MONTH date,
    DESCRIPTION varchar(4000),
    LOCATION varchar(2000),
    PART_TIME boolean,
    MINING_EXPERIENCE boolean,
    GROUP_EXPERIENCE boolean,
    INDUSTRY_ID uuid,
    YEARS integer,
    MONTHS integer,
    DAYS integer,
    START_DATE_HISTORY date,
    END_DATE_HISTORY date,
    --
    primary key (ID)
)^
-- end TSADV_PERSON_EXPERIENCE
-- begin TSADV_PUNISHMENT_TB
create table TSADV_PUNISHMENT_TB (
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
    ORDER_DATE date,
    PUNISHMENT_TYPE_ID uuid not null,
    PERSON_ID uuid not null,
    ORDER_NUMBER bigint,
    PUNISHMENT_DATE date not null,
    DESCRIPTION varchar(255),
    ACCIDENTS_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_PUNISHMENT_TB
-- begin TSADV_RCG_FEEDBACK
create table TSADV_RCG_FEEDBACK (
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
    TYPE_ID uuid,
    DIRECTION varchar(50) not null,
    AUTHOR_ID uuid not null,
    RECEIVER_ID uuid not null,
    COMMENT_ varchar(2000) not null,
    COMMENT_EN varchar(2000) not null,
    COMMENT_RU varchar(2000) not null,
    FEEDBACK_DATE timestamp not null,
    THEME varchar(2000) not null,
    THEME_RU varchar(2000) not null,
    THEME_EN varchar(2000) not null,
    --
    primary key (ID)
)^
-- end TSADV_RCG_FEEDBACK
-- begin TSADV_PERSON_DOCUMENT_REQUEST
create table TSADV_PERSON_DOCUMENT_REQUEST (
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
    ISSUE_DATE date not null,
    EXPIRED_DATE date not null,
    ISSUED_BY varchar(500),
    ISSUING_AUTHORITY_ID uuid not null,
    DESCRIPTION varchar(2000),
    DOCUMENT_TYPE_ID uuid not null,
    PERSON_GROUP_ID uuid,
    DOCUMENT_NUMBER varchar(255) not null,
    SERIES varchar(255),
    STATUS_ID uuid not null,
    FILE_ID uuid,
    REQUEST_STATUS_ID uuid not null,
    EDITED_PERSON_DOCUMENT_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_PERSON_DOCUMENT_REQUEST
-- begin TSADV_ADDRESS
create table TSADV_ADDRESS (
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
    ADDRESS_TYPE_ID uuid not null,
    ADDRESS varchar(500) not null,
    COUNTRY_ID uuid not null,
    POSTAL_CODE varchar(255),
    CITY_NAME varchar(255),
    FACT_ADDRESS varchar(255),
    REGISTRATION_ADDRESS varchar(255),
    FACT_ADDRESS_KATO_CODE varchar(255),
    REGISTRATION_ADDRESS_KATO_CODE varchar(255),
    CITY_ID uuid,
    LANGUAGE_ID uuid,
    START_DATE date not null,
    END_DATE date not null,
    --
    primary key (ID)
)^
-- end TSADV_ADDRESS
-- begin TSADV_COMPETENCE_ELEMENT
create table TSADV_COMPETENCE_ELEMENT (
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
    COMPETENCE_GROUP_ID uuid not null,
    OBJECT_ID uuid,
    OBJECT_TYPE_ID uuid,
    PERSON_GROUP_ID uuid,
    ORGANIZATION_GROUP_ID uuid,
    POSITION_GROUP_ID uuid,
    SCALE_LEVEL_ID uuid,
    JOB_GROUP_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_COMPETENCE_ELEMENT
-- begin TSADV_SANITARY_REGULATIONS_CONTROL
create table TSADV_SANITARY_REGULATIONS_CONTROL (
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
    CONTROL_TYPE_ID uuid not null,
    TOTAL_CONTROL bigint,
    IDENTIFIED_INCONSISTENCIES bigint,
    ELIMINATED_INCONSISTENCIES bigint,
    OCCUPATIONAL_MEDICINE_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_SANITARY_REGULATIONS_CONTROL
-- begin TSADV_HARMFUL_FACTORS_DETAIL
create table TSADV_HARMFUL_FACTORS_DETAIL (
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
    HARMFUL_FACTOR_TYPE_ID uuid not null,
    TOTAL_SAMPLES bigint,
    MAX_PERMIS_CONCENTRATION bigint,
    MAX_SINGLE_CONCENTRATION bigint,
    HARMFULL_FACTORS_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_HARMFUL_FACTORS_DETAIL
-- begin TSADV_COMPETENCE_TEMPLATE_DETAIL
create table TSADV_COMPETENCE_TEMPLATE_DETAIL (
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
    COMPETENCE_TEMPLATE_ID uuid not null,
    COMPETENCE_GROUP_ID uuid not null,
    WEIGHT integer not null,
    --
    primary key (ID)
)^
-- end TSADV_COMPETENCE_TEMPLATE_DETAIL
-- begin TSADV_PERSON_EXPECTED_SALARY
create table TSADV_PERSON_EXPECTED_SALARY (
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
    ACTUAL_DATE date not null,
    AMOUNT double precision not null,
    GROSS_NET varchar(50) not null,
    CURRENCY_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_PERSON_EXPECTED_SALARY
-- begin TSADV_EDUCATION
create table TSADV_EDUCATION (
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
    PERSON_ID uuid not null,
    SCHOLL varchar(500) not null,
    FACULTY varchar(500),
    START_YEAR integer,
    END_YEAR integer,
    SPECIALIZATION varchar(500),
    QUALIFICATION varchar(500),
    EDUCATION_TYPE_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_EDUCATION
-- begin TSADV_PERSON_ANSWER
create table TSADV_PERSON_ANSWER (
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
    TEST_SECTION_ID uuid not null,
    CORRECT boolean not null,
    ANSWERED boolean not null,
    SCORE integer not null,
    QUESTION_ID uuid not null,
    ATTEMPT_ID uuid,
    ANSWER text,
    --
    primary key (ID)
)^
-- end TSADV_PERSON_ANSWER
-- begin TSADV_LEARNING_PATH_REVIEW
create table TSADV_LEARNING_PATH_REVIEW (
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
    LEARNING_PATH_ID uuid not null,
    RATE double precision,
    TEXT varchar(500) not null,
    --
    primary key (ID)
)^
-- end TSADV_LEARNING_PATH_REVIEW
-- begin TSADV_NOT_ALLOWED_PERSON
create table TSADV_NOT_ALLOWED_PERSON (
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
    NOT_ALLOWED_ID uuid not null,
    DISPENSARY_CONFIRMATION boolean,
    INTOXICATION_TYPE_ID uuid,
    HARMFULL_FACTORS_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_NOT_ALLOWED_PERSON
-- begin TSADV_FAQ
create table TSADV_FAQ (
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
    LANG_VALUE1 varchar(2000) not null,
    ORDER_ integer,
    LANG_VALUE2 varchar(2000),
    LANG_VALUE3 varchar(2000),
    LANG_VALUE4 varchar(2000),
    LANG_VALUE5 varchar(2000),
    CONTENT_LANG_VALUE1 text,
    CONTENT_LANG_VALUE2 text,
    CONTENT_LANG_VALUE3 text,
    CONTENT_LANG_VALUE4 text,
    CONTENT_LANG_VALUE5 text,
    LANGVALUE varchar(255),
    CONTENTLANGVALUE varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_FAQ
-- begin TSADV_BUILDINGS
create table TSADV_BUILDINGS (
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
    NAME varchar(255) not null,
    OBJECT_TYPE_ID uuid not null,
    ORGANIZATION_ID uuid not null,
    COMMISSIONING date not null,
    BUILDING_VOLUME bigint not null,
    BUILDING_AREA bigint not null,
    TOTAL_AREA bigint not null,
    TECHNICAL_PASSPORT boolean,
    BUILDING_PASSPORT boolean,
    TECHNICAL_JOURNAL boolean,
    INVENTORY_NUMBER varchar(255) not null,
    OLD_INVENTORY_NUMBER varchar(255) not null,
    --
    primary key (ID)
)^
-- end TSADV_BUILDINGS
-- begin TSADV_RC_JOB_GROUP
create table TSADV_RC_JOB_GROUP (
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
    GROUP_NAME varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_RC_JOB_GROUP
-- begin TSADV_PERSONAL_PROTECTION
create table TSADV_PERSONAL_PROTECTION (
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
    EMPLOYEE_ID uuid not null,
    PROTECTION_EQUIPMENT_ID uuid not null,
    QUANTITY integer not null,
    DATE_OF_ISSUE date not null,
    PLAN_CHANGE_DATE date,
    STATUS varchar(50) not null,
    CONDITION_ID uuid,
    IS_ACCEPTED_BY_PERSON boolean not null,
    WRITTEN_OF_REASON varchar(255),
    WRITTEN_OF_DATE date,
    REPLACEMENT_DATE date,
    --
    primary key (ID)
)^
-- end TSADV_PERSONAL_PROTECTION
-- begin TSADV_INCIDENT_WITNESSES
create table TSADV_INCIDENT_WITNESSES (
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
    PERSON_ID uuid not null,
    INCIDENT_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_INCIDENT_WITNESSES
-- begin TSADV_VACATION_SCHEDULE_REQUEST
create table TSADV_VACATION_SCHEDULE_REQUEST (
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
    REQUEST_NUMBER bigint not null,
    STATUS_ID uuid not null,
    REQUEST_DATE date not null,
    COMMENT_ varchar(3000),
    --
    PERSON_GROUP_ID uuid not null,
    START_DATE date,
    END_DATE date,
    ABSENCE_DAYS integer,
    BALANCE integer,
    SENT_TO_ORACLE boolean not null,
    ATTACHMENT_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_VACATION_SCHEDULE_REQUEST
-- begin TSADV_PERSON_EDUCATION
create table TSADV_PERSON_EDUCATION (
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
    EDUCATIONAL_ESTABLISHMENT_ID uuid,
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
    START_DATE_HISTORY date,
    END_DATE_HISTORY date,
    --
    primary key (ID)
)^
-- end TSADV_PERSON_EDUCATION
-- begin TSADV_PERSON_QUALIFICATION_REQUEST
create table TSADV_PERSON_QUALIFICATION_REQUEST (
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
    TYPE_ID uuid,
    PERSON_GROUP_ID uuid not null,
    START_DATE date not null,
    END_DATE date,
    ASSIGN_VALIDATION_DATE date,
    ATTACHMENT_ID uuid,
    NOTE varchar(3000),
    EDUCATIONAL_INSTITUTION_NAME varchar(2000),
    DIPLOMA varchar(2000),
    TYPE_NAME varchar(2000),
    ISSUED_DATE date,
    REQUEST_STATUS_ID uuid,
    FILE_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_PERSON_QUALIFICATION_REQUEST
-- begin TSADV_FIRE_WATER_SUPPLY
create table TSADV_FIRE_WATER_SUPPLY (
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
    EQUIPMENT varchar(120) not null,
    INVENTORY_NUMBER varchar(30),
    IS_WORKING boolean not null,
    BUILDING_FIRE_SAFETY_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_FIRE_WATER_SUPPLY
-- begin TSADV_AGREEMENT
create table TSADV_AGREEMENT (
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
    AGREEMENT_NUMBER varchar(255) not null,
    SUSPENSION_DATE_FROM date,
    SUSPENSION_DATE_TO date,
    AGREEMENT_TYPE_ID uuid not null,
    DATE_FROM date not null,
    DATE_TO date not null,
    STATUS_ID uuid not null,
    PERSON_GROUP_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_AGREEMENT
-- begin TSADV_ASSESSMENT_COMPETENCE
create table TSADV_ASSESSMENT_COMPETENCE (
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
    PARTICIPANT_ASSESSMENT_ID uuid not null,
    COMPETENCE_GROUP_ID uuid not null,
    OVERALL_RATING_ID uuid not null,
    COMMENT_ varchar(2000),
    WEIGHT double precision,
    --
    primary key (ID)
)^
-- end TSADV_ASSESSMENT_COMPETENCE
-- begin TSADV_CASE_TYPE
create table TSADV_CASE_TYPE (
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
    NAME varchar(255) not null,
    CODE varchar(255) not null,
    LANGUAGE_ varchar(255) not null,
    QUESTION varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_CASE_TYPE
-- begin TSADV_CASE
create table TSADV_CASE (
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
    LONG_NAME varchar(1000),
    SHORT_NAME varchar(1000),
    LANGUAGE_ varchar(255) not null,
    CASE_TYPE_ID uuid not null,
    JOB_GROUP_ID uuid,
    ORGANIZATION_GROUP_ID uuid,
    PERSON_GROUP_ID uuid,
    POSITION_GROUP_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_CASE
-- begin TSADV_AWARDS_REQUEST
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
)^
-- end TSADV_AWARDS_REQUEST
-- begin TSADV_MEDICAL_SERVICE
create table TSADV_MEDICAL_SERVICE (
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
    AGREEMENT_DATE date not null,
    AGREEMENT_NUMBER bigint not null,
    MEDICAL_CENTER varchar(255),
    DATE_FROM date,
    DATE_TO date,
    COST decimal(19, 2),
    MEDICAL_INSPECTION_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_MEDICAL_SERVICE
-- begin TSADV_PERSON_LENGTH_OF_SERVICE
create table TSADV_PERSON_LENGTH_OF_SERVICE (
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
    LENGTH_OF_SERVICE_TYPE_ID uuid,
    RANGE_ID uuid,
    EFFECTIVE_DATE date,
    VALUE_ double precision,
    --
    primary key (ID)
)^
-- end TSADV_PERSON_LENGTH_OF_SERVICE
-- begin TSADV_REQUISITION_MEMBER
create table TSADV_REQUISITION_MEMBER (
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
    REQUISITION_ID uuid not null,
    PERSON_GROUP_ID uuid not null,
    ACCESS_LEVEL integer not null,
    --
    primary key (ID)
)^
-- end TSADV_REQUISITION_MEMBER
-- begin TSADV_PUNISHMENT
create table TSADV_PUNISHMENT (
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
    DATE_ date,
    ACCIDENT_CAUSE varchar(255),
    RESPONSIBLE_EMPLOYEE_ID uuid,
    NOTIFY_EMPLOYEES boolean,
    LAW_ARTICLE_ID uuid,
    EARLY_TERMINATION_REASON varchar(255),
    TYPE_ID uuid,
    ACCIDENT varchar(255),
    OFFENCE_TYPE_ID uuid,
    REASON varchar(255),
    IS_SAFETY_ENGINEERING boolean,
    PERIOD date,
    ORDER_NUMBER varchar(255),
    ORDER_DATE date,
    DEPRESSION integer,
    LED_TO_ACCIDENT boolean,
    ADDITIONAL_EDUCATION_ID uuid,
    EXAM_RESULTS_ID uuid,
    ACCEPTED_ACTION_ID uuid,
    REMOVING_DATE date,
    REMOVING_ORDER_NUM varchar(255),
    REMOVING_ORDER_DATE date,
    ASSIGNMENT_GROUP_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_PUNISHMENT
-- begin TSADV_BUDGET_REQUEST_ITEM
create table TSADV_BUDGET_REQUEST_ITEM (
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
    BUDGET_REQUEST_ID uuid not null,
    FIRST_DAY_OF_MONTH date not null,
    NAME varchar(255) not null,
    BUDGET_ITEM_ID uuid,
    AMOUNT double precision,
    CURRENCY_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_BUDGET_REQUEST_ITEM
-- begin TSADV_BUILDING_MAINTENANCE
create table TSADV_BUILDING_MAINTENANCE (
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
    MANAGER_FULL_NAME_ID uuid not null,
    RESPONSIBLE_FULL_NAME_ID uuid not null,
    INSPECTION_REPORT varchar(255),
    TECHNICAL_RESOLUTION varchar(255),
    MAINTENANCE_PROHIBITION boolean,
    TECHNICAL_JOURNAL_RECORD varchar(255),
    CURRENT_REPAIRS varchar(255),
    REBUILDING varchar(255),
    CONTRACTING_ORGANIZATION varchar(255),
    ON_BALANCE boolean,
    DISMANTLED boolean,
    UNMAINTENANCE boolean,
    ON_CONSERVATION boolean,
    WRITE_OF_DATE date,
    DISMANTLING_DATE date,
    BUILDINGS_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_BUILDING_MAINTENANCE
-- begin TSADV_ORDER_MASTER
create table TSADV_ORDER_MASTER (
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
    ORDER_TYPE_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_ORDER_MASTER
-- begin TSADV_RC_QUESTIONNAIRE
create table TSADV_RC_QUESTIONNAIRE (
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
    NAME varchar(255) not null,
    NAME2 varchar(255),
    NAME3 varchar(255),
    NAME4 varchar(255),
    NAME5 varchar(255),
    PASSING_SCORE double precision,
    INSTRUCTION text not null,
    STATUS_ID uuid not null,
    CATEGORY_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_RC_QUESTIONNAIRE
-- begin TSADV_GOODS_WAREHOUSE
create table TSADV_GOODS_WAREHOUSE (
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
    GOODS_ID uuid not null,
    QUANTITY bigint not null,
    --
    primary key (ID)
)^
-- end TSADV_GOODS_WAREHOUSE
-- begin TSADV_TRANSFER_REQUEST
create table TSADV_TRANSFER_REQUEST (
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
    ASSIGNMENT_GROUP_ID uuid not null,
    REQUEST_DATE date not null,
    TRANSFER_DATE date not null,
    NEW_POSITION_GROUP_ID uuid not null,
    NEW_ORGANIZATION_GROUP_ID uuid not null,
    REQUEST_STATUS_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_TRANSFER_REQUEST
-- begin TSADV_PERSON_BENEFIT
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
)^
-- end TSADV_PERSON_BENEFIT
-- begin TSADV_BUDGET_REQUEST_DETAIL
create table TSADV_BUDGET_REQUEST_DETAIL (
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
    BUDGET_REQUEST_ID uuid not null,
    MONTH_ID uuid not null,
    EMPLOYEES_COUNT integer not null,
    BUSINESS_TRIP_EMPLOYEE integer,
    --
    primary key (ID)
)^
-- end TSADV_BUDGET_REQUEST_DETAIL
-- begin TSADV_MEDICAL_INSPECTION_RESULT
create table TSADV_MEDICAL_INSPECTION_RESULT (
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
    WITH_DISEASES integer not null,
    RISK_GROUP integer,
    TRADEUNION_CENTER_SENT integer,
    TEMPORARY_UNFIT integer,
    CONSTANTLY_UNFIT integer,
    MEDICAL_INSPECTION_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_MEDICAL_INSPECTION_RESULT
-- begin TSADV_INJURED_ORGAN
create table TSADV_INJURED_ORGAN (
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
    INJURED_ORGAN varchar(255),
    MICROTRAUMA_INJURED_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_INJURED_ORGAN
-- begin TSADV_QUESTION_IN_SECTION
create table TSADV_QUESTION_IN_SECTION (
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
    TEST_SECTION_ID uuid not null,
    QUESTION_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_QUESTION_IN_SECTION
-- begin TSADV_ACCIDEN_INJURED
create table TSADV_ACCIDEN_INJURED (
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
    PERSON_ID uuid not null,
    DISABILITY_GROUP_ID uuid,
    TRAUMA_LEVEL_ID uuid not null,
    INDUCTION_DATE date,
    RE_INTRODUCTORY_DATE date,
    KNOWLEDGE_TEST_DATE date,
    MEDICAL_EXAMINATION_DATE date,
    WORKING_HOURS bigint not null,
    PHYSICAL_CONDITION varchar(255) not null,
    DIAGNOSIS varchar(255),
    PRODUCTION_CONNECTION boolean,
    REASON_NO_PRODUCTION_CONNECTION_ID uuid,
    SPECIAL_OPINION boolean,
    EMPLOYEE_GUILT bigint,
    EMPLOYER_GUILT bigint,
    DISABILITY_PERCENT bigint,
    SICKNESS_START_DATE date,
    SICKNESS_END_DATE date,
    RETRAINING_PROFESSION varchar(255),
    REABILITATION_ID uuid,
    SICKNESS_DAYS bigint,
    START_WORK_DATE date,
    ACCIDENTS_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_ACCIDEN_INJURED
-- begin TSADV_SCHEDULE_SUMMARY
create table TSADV_SCHEDULE_SUMMARY (
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
    HEADER_ID uuid not null,
    DAY_ integer not null,
    DAY_DATE date not null,
    SHIFT_ID uuid,
    HOURS double precision not null,
    BASE_HOURS double precision not null,
    START_TIME time,
    END_TIME time,
    ELEMENT_TYPE_ID uuid not null,
    CORRECTION_FLAG boolean not null,
    DISPLAY_VALUE varchar(255),
    SHIFTNAME varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_SCHEDULE_SUMMARY
-- begin TSADV_AWARDS
create table TSADV_AWARDS (
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
    START_DATE_HISTORY date,
    END_DATE_HISTORY date,
    --
    primary key (ID)
)^
-- end TSADV_AWARDS
-- begin TSADV_ORGANIZATION_HR_USER
create table TSADV_ORGANIZATION_HR_USER (
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
    ORGANIZATION_GROUP_ID uuid not null,
    HR_ROLE_ID uuid not null,
    USER_ID uuid,
    DATE_FROM date not null,
    DATE_TO date not null,
    REQUESTED_TS timestamp,
    COUNTER integer,
    --
    primary key (ID)
)^
-- end TSADV_ORGANIZATION_HR_USER
-- begin TSADV_ASSESSMENT_PERSON_ANSWER
create table TSADV_ASSESSMENT_PERSON_ANSWER (
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
    PERSON_ID uuid not null,
    QUESTION_ID uuid not null,
    ANSWER text,
    ASSESSMENT_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_ASSESSMENT_PERSON_ANSWER
-- begin TSADV_ABSENCE_TO_WORKED_HOURS_SUMMARY
create table TSADV_ABSENCE_TO_WORKED_HOURS_SUMMARY (
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
    ABSENCE_ID uuid,
    WORKED_HOURS_SUMMARY_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_ABSENCE_TO_WORKED_HOURS_SUMMARY
-- begin TSADV_PERSON_EXPERIENCE_REQUEST
create table TSADV_PERSON_EXPERIENCE_REQUEST (
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
    UNTIL_NOW boolean,
    COMPANY varchar(255) not null,
    JOB varchar(255) not null,
    START_MONTH date not null,
    END_MONTH date,
    DESCRIPTION varchar(4000),
    LOCATION varchar(2000),
    PART_TIME boolean,
    MINING_EXPERIENCE boolean,
    GROUP_EXPERIENCE boolean,
    INDUSTRY_ID uuid,
    YEARS integer,
    MONTHS integer,
    DAYS integer,
    REQUEST_STATUS_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_PERSON_EXPERIENCE_REQUEST
-- begin TSADV_SCHEDULE_DETAIL
create table TSADV_SCHEDULE_DETAIL (
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
    SUMMARY_ID uuid not null,
    DAY_ integer not null,
    DAY_DATE date not null,
    HOURS double precision not null,
    TIME_IN timestamp,
    TIME_OUT timestamp,
    ELEMENT_TYPE_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_SCHEDULE_DETAIL
-- begin TSADV_COURSE
create table TSADV_COURSE (
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
    NAME varchar(255) not null,
    IS_ISSUED_CERTIFICATE boolean not null,
    PARTY_ID uuid,
    DESCRIPTION text,
    LOGO bytea,
    CATEGORY_ID uuid not null,
    TARGET_AUDIENCE varchar(1000),
    ACTIVE_FLAG boolean,
    SHORT_DESCRIPTION varchar(2000),
    SELF_ENROLLMENT boolean,
    LEARNING_TYPE_ID uuid,
    IS_ONLINE boolean not null,
    EDUCATION_PERIOD bigint,
    EDUCATION_DURATION bigint,
    --
    primary key (ID)
)^
-- end TSADV_COURSE
-- begin TSADV_BUSINESS_TRIP
create table TSADV_BUSINESS_TRIP (
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
    DATE_FROM date not null,
    REASON varchar(255),
    ORDER_ID uuid,
    ORDER_NUM varchar(255),
    ORDER_DATE date,
    STATUS varchar(50),
    TYPE_TRIP varchar(50),
    CANCEL_ORDER_NUMBER varchar(255),
    CANCEL_ORDER_DATE date,
    PARENT_BUSINESS_TRIP_ID uuid,
    DATE_TO date not null,
    PURPOSE varchar(1000),
    TYPE_ID uuid not null,
    ORD_ASSIGNMENT_ID uuid,
    PERSON_GROUP_ID uuid,
    ROUTE varchar(255),
    ABSENCEDAYS integer,
    BUSINESSTRIPWITHDATE varchar(255),
    PARENTBUSINESSTRIPCAPTION varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_BUSINESS_TRIP
-- begin TSADV_INSTRUCTIONS_KPI
create table TSADV_INSTRUCTIONS_KPI (
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
    INSTRUCTION text,
    PERFORMANCE_PLAN_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_INSTRUCTIONS_KPI
-- begin TSADV_QUESTION_BANK
create table TSADV_QUESTION_BANK (
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
    BANK_NAME varchar(255) not null,
    DESCRIPTION varchar(4000) not null,
    --
    primary key (ID)
)^
-- end TSADV_QUESTION_BANK
-- begin TSADV_ADDITIONAL_FILE
create table TSADV_ADDITIONAL_FILE (
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
    FILE_ID uuid not null,
    RELATION_ENTITY_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_ADDITIONAL_FILE
-- begin TSADV_WORKED_HOURS_SUMMARY
create table TSADV_WORKED_HOURS_SUMMARY (
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
    TIMECARD_HEADER_ID uuid not null,
    DISPLAY_VALUE varchar(255),
    WORKED_DATE date not null,
    HOURS double precision not null,
    TIME_IN date,
    TIME_OUT date,
    SHIFT_ID uuid,
    SCHEDULE_ELEMENT_TYPE_ID uuid,
    CORRECTION_FLAG boolean,
    BUSSINESS_TRIP_ID uuid,
    ORDER_ID uuid,
    ABSENCE_ID uuid,
    TIMECARDREPRESENTATION varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_WORKED_HOURS_SUMMARY
-- begin TSADV_INTERVIEW_HISTORY
create table TSADV_INTERVIEW_HISTORY (
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
    INTERVIEW_ID uuid not null,
    INTERVIEW_STATUS integer not null,
    --
    primary key (ID)
)^
-- end TSADV_INTERVIEW_HISTORY
-- begin TSADV_PERSON_LEARNING_HISTORY
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
    ORGANIZATION_BIN varchar(255),
    INTEGRATION_USER_LOGIN varchar(255),
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
    COURSE_NAME varchar(500),
    ENROLLMENT_ID uuid,
    INCOMPLETE boolean not null,
    BUDGET_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_PERSON_LEARNING_HISTORY
-- begin TSADV_CALENDAR_HOLIDAY
create table TSADV_CALENDAR_HOLIDAY (
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
    CALENDAR_ID uuid not null,
    NAME varchar(100) not null,
    START_DATE date not null,
    END_DATE date not null,
    ACTION_DATE_FROM date not null,
    ACTION_DATE_TO date not null,
    STATE boolean not null,
    DAY_TYPE varchar(50) not null,
    TRANSFER_START_DATE date,
    TRANSFER_END_DATE date,
    --
    primary key (ID)
)^
-- end TSADV_CALENDAR_HOLIDAY
-- begin TSADV_TEST
create table TSADV_TEST (
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
    NAME varchar(120) not null,
    DESCRIPTION varchar(2000),
    TYPE_ID uuid not null,
    ACTIVE boolean not null,
    MAX_ATTEMPT integer not null,
    DAYS_BETWEEN_ATTEMPTS integer not null,
    TIMER integer not null,
    SECTION_ORDER integer not null,
    INSTRUCTION varchar(4000),
    TARGET_SCORE integer,
    SHOW_RESULTS boolean not null,
    SHOW_SECTION_NEW_PAGE boolean,
    QUESTION_PER_PAGE integer,
    COURSE_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_TEST
-- begin TSADV_PERSON_CONVICTION
create table TSADV_PERSON_CONVICTION (
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
    HAVE_CONVICTION varchar(50),
    REASON varchar(2000),
    --
    primary key (ID)
)^
-- end TSADV_PERSON_CONVICTION
-- begin TSADV_TECHNICAL_STATUS
create table TSADV_TECHNICAL_STATUS (
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
    BUILD_TECHNICAL_STATUS_ID uuid not null,
    TECHNICAL_STATUS_DATE date not null,
    TECHNICAL_STATUS_BUILDSTRUCTURES_ID uuid not null,
    BUILDINGS_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_TECHNICAL_STATUS
-- begin TSADV_ALLOWABLE_SCHEDULE_FOR_POSITION
create table TSADV_ALLOWABLE_SCHEDULE_FOR_POSITION (
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
    POSITION_GROUP_ID uuid not null,
    SCHEDULE_ID uuid not null,
    START_DATE date not null,
    END_DATE date not null,
    --
    primary key (ID)
)^
-- end TSADV_ALLOWABLE_SCHEDULE_FOR_POSITION
-- begin TSADV_LEARNING_FEEDBACK_ANSWER
create table TSADV_LEARNING_FEEDBACK_ANSWER (
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
    ORDER_ integer not null,
    SCORE integer not null,
    ANSWER_LANG_VALUE1 varchar(2000) not null,
    ANSWER_LANG_VALUE2 varchar(2000),
    ANSWER_LANG_VALUE3 varchar(2000),
    ANSWER_LANG_VALUE4 varchar(2000),
    ANSWER_LANG_VALUE5 varchar(2000),
    FEEDBACK_QUESTION_ID uuid not null,
    ANSWERLANGVALUE varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_LEARNING_FEEDBACK_ANSWER
-- begin TSADV_RC_QUESTIONNAIRE_ANSWER
create table TSADV_RC_QUESTIONNAIRE_ANSWER (
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
    QUESTIONNAIRE_QUESTION_ID uuid not null,
    ANSWER_ID uuid not null,
    WEIGHT double precision,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_RC_QUESTIONNAIRE_ANSWER
-- begin TSADV_PERSON_ACTION_COMMENT
create table TSADV_PERSON_ACTION_COMMENT (
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
    ACTION_ID uuid not null,
    COMMENT_ varchar(2000),
    PARENT_COMMENT_ID uuid,
    PERSON_GROUP_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_PERSON_ACTION_COMMENT
-- begin TSADV_LEARNING_OBJECT
create table TSADV_LEARNING_OBJECT (
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
    OBJECT_NAME varchar(255) not null,
    DESCRIPTION varchar(4000) not null,
    URL varchar(4000),
    CONTENT_TYPE varchar(50) not null,
    FILE_ID uuid,
    HTML text,
    TEXT text,
    --
    primary key (ID)
)^
-- end TSADV_LEARNING_OBJECT
-- begin TSADV_COURSE_FEEDBACK_PERSON_ANSWER
create table TSADV_COURSE_FEEDBACK_PERSON_ANSWER (
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
    FEEDBACK_TEMPLATE_ID uuid not null,
    COURSE_ID uuid,
    COURSE_SECTION_SESSION_ID uuid,
    COMPLETE_DATE date not null,
    RESPONSIBLE_ROLE varchar(50) not null,
    PERSON_GROUP_ID uuid not null,
    SUM_SCORE bigint,
    AVG_SCORE double precision,
    --
    primary key (ID)
)^
-- end TSADV_COURSE_FEEDBACK_PERSON_ANSWER
-- begin TSADV_GRADE_RULE
create table TSADV_GRADE_RULE (
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
    RULE_NAME varchar(1000) not null,
    CURRENCY_ID uuid not null,
    GROSS_NET varchar(50),
    --
    primary key (ID)
)^
-- end TSADV_GRADE_RULE
-- begin TSADV_HIRING_STEP
create table TSADV_HIRING_STEP (
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
    STEP_NAME varchar(255) not null,
    IS_JOB_TEST boolean not null,
    TYPE_ varchar(50),
    ATTEMPTS_CONTROL_LEVEL integer,
    PERIOD integer,
    ATTEMPTS bigint,
    NUMBER_BETWEEN_ATTEMPTS bigint,
    STEP_DESCRIPTION varchar(2000) not null,
    START_DATE date not null,
    END_DATE date not null,
    TEST_ID uuid,
    DEFAULT_ BOOLEAN DEFAULT FALSE,
    ORDER_DEFAULT integer,
    --
    primary key (ID)
)^
-- end TSADV_HIRING_STEP
-- begin TSADV_PERSON_LEARNING_PATH
create table TSADV_PERSON_LEARNING_PATH (
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
    LEARNING_PATH_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_PERSON_LEARNING_PATH
-- begin TSADV_GOODS_ISSUE
create table TSADV_GOODS_ISSUE (
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
    GOODS_ID uuid not null,
    QUANTITY bigint not null,
    ORDER_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_GOODS_ISSUE
-- begin TSADV_ABSENCE_BALANCE
create table TSADV_ABSENCE_BALANCE (
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
    OVERALL_BALANCE_DAYS integer,
    DATE_FROM date not null,
    DATE_TO date not null,
    BALANCE_DAYS integer not null,
    ADDITIONAL_BALANCE_DAYS integer not null,
    DAYS_SPENT integer,
    DAYS_LEFT integer,
    EXTRA_DAYS_SPENT integer,
    EXTRA_DAYS_LEFT integer,
    LONG_ABSENCE_DAYS integer,
    ADD_BALANCE_DAYS_AIMS integer,
    --
    primary key (ID)
)^
-- end TSADV_ABSENCE_BALANCE
-- begin TSADV_CERTIFICATE_TEMPLATE
create table TSADV_CERTIFICATE_TEMPLATE (
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
    RECEIVING_TYPE_ID uuid not null,
    CERTIFICATE_TYPE_ID uuid not null,
    ORGANIZATION_ID uuid,
    LANGUAGE_ID uuid not null,
    SHOW_SALARY boolean not null,
    SIGNER_ID uuid,
    REPORT_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_CERTIFICATE_TEMPLATE
-- begin TSADV_BUILDING_COST
create table TSADV_BUILDING_COST (
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
    COST_DATE date not null,
    BALANCE_COST bigint not null,
    RESIDUAL_VALUE bigint not null,
    BUILDINGS_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_BUILDING_COST
-- begin TSADV_QUESTION
create table TSADV_QUESTION (
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
    BANK_ID uuid not null,
    TEXT varchar(2000),
    TYPE_ integer not null,
    SCORE integer not null,
    --
    primary key (ID)
)^
-- end TSADV_QUESTION
-- begin TSADV_STANDARD_SHIFT
create table TSADV_STANDARD_SHIFT (
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
    STANDARD_SCHEDULE_ID uuid not null,
    NUMBER_IN_SHIFT integer not null,
    SHIFT_ID uuid,
    SHIFT_DISPLAY varchar(255),
    SHIFT_DISPLAY_DAY integer not null,
    --
    primary key (ID)
)^
-- end TSADV_STANDARD_SHIFT
-- begin TSADV_ORGANIZATION_GROUP_GOAL_LINK
create table TSADV_ORGANIZATION_GROUP_GOAL_LINK (
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
    WEIGHT integer,
    TARGET_VALUE integer,
    GOAL_ID uuid not null,
    ORGANIZATION_GROUP_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_ORGANIZATION_GROUP_GOAL_LINK
-- begin TSADV_TRADE_UNION
create table TSADV_TRADE_UNION (
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
    JOING_TRADE_UNION date,
    DATE_TO date,
    DIC_TRADE_UNION_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_TRADE_UNION
-- begin TSADV_BUSINESS_TRIP_COST
create table TSADV_BUSINESS_TRIP_COST (
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
    AMOUNT decimal(19, 2) not null,
    COST_TYPE_ID uuid not null,
    CURRENCY_ID uuid not null,
    BUSINESS_TRIP_LINES_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_BUSINESS_TRIP_COST
-- begin TSADV_SALARY_REQUEST
create table TSADV_SALARY_REQUEST (
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
    ASSIGNMENT_GROUP_ID uuid,
    ATTACHMENT_ID uuid,
    NOTE varchar(3000),
    REQUEST_NUMBER bigint,
    STATUS_ID uuid not null,
    OLD_SALARY uuid,
    AMOUNT double precision not null,
    CHANGE_PERCENT double precision,
    NET_GROSS varchar(50) not null,
    REASON_ID uuid,
    CURRENCY_ID uuid not null,
    ORD_ASSIGNMENT_ID uuid,
    START_DATE date not null,
    END_DATE date not null,
    ORDER_GROUP_ID uuid,
    AGREEMENT_ID uuid,
    TYPE_ varchar(50) not null,
    DIFFERENCE double precision,
    --
    primary key (ID)
)^
-- end TSADV_SALARY_REQUEST
-- begin TSADV_DIC_JOB_GROUP
create table TSADV_DIC_JOB_GROUP (
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
    JOB_GROUP_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_DIC_JOB_GROUP
-- begin TSADV_RETIREMENT_REQUEST
create table TSADV_RETIREMENT_REQUEST (
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
    RETIREMENT_TYPE_ID uuid,
    ISSEU_DOC_DATE date,
    DOCUMENT_NUMBER varchar(255),
    DATE_FROM date,
    DATE_TO date,
    PERSON_GROUP_EXT_ID uuid,
    RETIREMENT_ID uuid,
    FILE_ID uuid,
    REQUEST_STATUS_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_RETIREMENT_REQUEST
-- begin TSADV_LEARNING_FEEDBACK_TEMPLATE
create table TSADV_LEARNING_FEEDBACK_TEMPLATE (
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
    NAME varchar(500) not null,
    ACTIVE boolean not null,
    DESCRIPTION varchar(2000),
    USAGE_TYPE varchar(50) not null,
    EMPLOYEE boolean not null,
    MANAGER boolean not null,
    TRAINER boolean not null,
    --
    primary key (ID)
)^
-- end TSADV_LEARNING_FEEDBACK_TEMPLATE
-- begin TSADV_PERSON_CRIMINAL_ADMINISTRATIVE_LIABILITY_REQUEST
create table TSADV_PERSON_CRIMINAL_ADMINISTRATIVE_LIABILITY_REQUEST (
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
    TYPE_ID uuid,
    HAVE_LIABILITY varchar(50),
    REASON_PERIOD varchar(2000),
    REQUEST_STATUS_ID uuid,
    FILE_ID uuid,
    LIABILITY_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_PERSON_CRIMINAL_ADMINISTRATIVE_LIABILITY_REQUEST
-- begin TSADV_REQUISITION_HIRING_STEP
create table TSADV_REQUISITION_HIRING_STEP (
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
    REQUISITION_ID uuid not null,
    REQUIRED boolean not null,
    HIRING_STEP_ID uuid not null,
    ORDER_ integer not null,
    --
    primary key (ID)
)^
-- end TSADV_REQUISITION_HIRING_STEP
-- begin TSADV_POSITION_GROUP_GOAL_LINK
create table TSADV_POSITION_GROUP_GOAL_LINK (
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
    WEIGHT integer,
    TARGET_VALUE integer,
    POSITION_GROUP_ID uuid,
    GOAL_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_POSITION_GROUP_GOAL_LINK
-- begin TSADV_PERSON_REVIEW
create table TSADV_PERSON_REVIEW (
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
    AUTHOR_ID uuid not null,
    TEXT text,
    DATE_TIME timestamp not null,
    LIKING varchar(255),
    PERSON_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_PERSON_REVIEW
-- begin TSADV_RC_QUESTIONNAIRE_QUESTION
create table TSADV_RC_QUESTIONNAIRE_QUESTION (
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
    QUESTIONNAIRE_ID uuid not null,
    QUESTION_ID uuid not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_RC_QUESTIONNAIRE_QUESTION
-- begin TSADV_ACCIDENTS
create table TSADV_ACCIDENTS (
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
    ACCIDENT_TIME time not null,
    ORGANIZATION_ID uuid not null,
    INVESTIGATION_CONDUCTED_ID uuid not null,
    DIRECT_REASON_ID uuid not null,
    EXCAVATION_ID uuid,
    ACCIDENT_TYPE_ID uuid not null,
    ACCIDENT_ACT_NUMBER varchar(255),
    ACCIDENT_ACT_DATE date,
    ACCIDENT_CONDITIONS varchar(255) not null,
    ACCIDENT_DATE date not null,
    WORK_PLACE_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_ACCIDENTS
-- begin TSADV_IMPORT_SCENARIO
create table TSADV_IMPORT_SCENARIO (
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
    NAME varchar(255) not null,
    TEMPLATE_ID uuid not null,
    COMMENT_ text,
    IMPORTER_BEAN_NAME varchar(255) not null,
    --
    primary key (ID)
)^
-- end TSADV_IMPORT_SCENARIO
-- begin TSADV_PERSON_HEALTH
create table TSADV_PERSON_HEALTH (
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
    HEALTH_STATUS varchar(2000),
    CONTRAINDICATIONS varchar(2000),
    PERSON_GROUP_ID uuid,
    START_DATE_HISTORY date,
    END_DATE_HISTORY date,
    --
    primary key (ID)
)^
-- end TSADV_PERSON_HEALTH
-- begin TSADV_INTERVIEW_QUESTION
create table TSADV_INTERVIEW_QUESTION (
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
    INTERVIEW_QUESTIONNAIRE_ID uuid not null,
    QUESTION_ID uuid not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_INTERVIEW_QUESTION
-- begin TSADV_TIMECARD_DEVIATION
create table TSADV_TIMECARD_DEVIATION (
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
    ASSIGNMENT_GROUP_ID uuid not null,
    HOURS double precision not null,
    DATE_FROM date not null,
    DATE_TO date not null,
    IS_CHANGES_FACT_HOURS boolean not null,
    IS_CHANGES_PLAN_HOURS boolean not null,
    IS_CHANGES_DETAILS_FROM_BEGIN boolean not null,
    CHANGES_WEEKENDS boolean not null,
    --
    primary key (ID)
)^
-- end TSADV_TIMECARD_DEVIATION
-- begin TSADV_QUESTIONNAIRE_QUESTION
create table TSADV_QUESTIONNAIRE_QUESTION (
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
    QUESTIONNAIRE_ID uuid not null,
    ORDER_ integer,
    SECTION_ID uuid,
    QUESTION_TEXT_LANG1 varchar(2000),
    QUESTION_TEXT_LANG2 varchar(2000),
    QUESTION_TEXT_LANG3 varchar(2000),
    QUESTION_TEXT_LANG4 varchar(2000),
    QUESTION_TEXT_LANG5 varchar(2000),
    QUESTION_TYPE integer not null,
    SCORE integer,
    --
    primary key (ID)
)^
-- end TSADV_QUESTIONNAIRE_QUESTION
-- begin TSADV_COURSE_SECTION_OBJECT
create table tsadv_course_section_object (
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
    OBJECT_TYPE_ID uuid,
    CONTENT_ID uuid,
    TEST_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_COURSE_SECTION_OBJECT
-- begin TSADV_ANSWER
create table TSADV_ANSWER (
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
    ANSWER varchar(2000),
    CORRECT boolean not null,
    QUESTION_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_ANSWER
-- begin TSADV_STANDARD_OFFSET
create table TSADV_STANDARD_OFFSET (
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
    STANDARD_SCHEDULE_ID uuid not null,
    OFFSET_DISPLAY varchar(255) not null,
    OFFSET_DISPLAY_DAYS integer not null,
    START_DATE date not null,
    END_DATE date,
    OFFSETSCHEDULENAME varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_STANDARD_OFFSET
-- begin TSADV_CALIBRATION_MEMBER
create table TSADV_CALIBRATION_MEMBER (
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
    PERSON_ID uuid not null,
    POTENCIAL integer,
    PERFORMANCE integer,
    RISK_OF_LOSS integer,
    IMPACT_OF_LOSS integer,
    COMPETENCE_OVERALL integer,
    GOAL_OVERALL integer,
    SESSION_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_CALIBRATION_MEMBER
-- begin TSADV_PERSON_LANGUAGE
create table TSADV_PERSON_LANGUAGE (
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
    LANGUAGE_ID uuid,
    LANGUAGE_LEVEL_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_PERSON_LANGUAGE
-- begin TSADV_TIMECARD_CORRECTION
create table TSADV_TIMECARD_CORRECTION (
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
    ASSIGNMENT_GROUP_ID uuid not null,
    DATE_FROM date not null,
    DATE_TO date not null,
    --
    primary key (ID)
)^
-- end TSADV_TIMECARD_CORRECTION
-- begin TSADV_REPAIRS_DISMANTLING
create table TSADV_REPAIRS_DISMANTLING (
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
    WORK_TYPE varchar(255) not null,
    WORK_NUMBER bigint not null,
    WORK_COST bigint not null,
    BUILDINGS_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_REPAIRS_DISMANTLING
-- begin TSADV_MICROTRAUM
create table TSADV_MICROTRAUM (
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
    MANAGER_PERSON_ID uuid,
    REPORTED_PERSON_ID uuid,
    ORGANIZATION_ID uuid not null,
    MICROTRAUMA_DATE date not null,
    MICROTRAUMA_TIME timestamp,
    MICROTRAUMA_REASON varchar(255) not null,
    MICRATRAUMA_CONDITIONS varchar(255),
    SHIFT_START timestamp,
    SHIFT_END timestamp,
    WORK_PLACE_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_MICROTRAUM
-- begin TSADV_PERSON_BANK_DETAILS
create table TSADV_PERSON_BANK_DETAILS (
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
    BANK_ID uuid,
    FULL_NAME_BANK_CARD varchar(2000),
    IBAN varchar(255),
    BIC_BANK varchar(255),
    PERSON_GROUP_ID uuid,
    START_DATE_HISTORY date,
    END_DATE_HISTORY date,
    --
    primary key (ID)
)^
-- end TSADV_PERSON_BANK_DETAILS
-- begin TSADV_BUSINESS_TRIP_LINES
create table TSADV_BUSINESS_TRIP_LINES (
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
    CITY_TO_ID uuid,
    CITY_FROM_ID uuid,
    NUMBER_ varchar(255),
    DATE_FROM date not null,
    DATE_TO date not null,
    BUSINESS_TRIP_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_BUSINESS_TRIP_LINES
-- begin TSADV_LOOKUP_VALUE
create table TSADV_LOOKUP_VALUE (
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
    LOOKUP_TYPE_ID uuid not null,
    LOOKUP_TYPE_CODE varchar(255) not null,
    MEANING_LANG1 varchar(255) not null,
    MEANING_LANG2 varchar(255),
    MEANING_LANG3 varchar(255),
    DESCRIPTION_LANG1 varchar(255),
    DESCRIPTION_LANG2 varchar(255),
    DESCRIPTION_LANG3 varchar(255),
    ENABLED_FLAG boolean,
    START_DATE date,
    END_DATE date,
    TAG varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_LOOKUP_VALUE
-- begin TSADV_BUILDING_FIRE_SAFETY
create table TSADV_BUILDING_FIRE_SAFETY (
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
    FS_BUILDING_CATEGORY_ID uuid not null,
    FS_OUTDOOR_CATEGORY_ID uuid,
    AGREEMENT text,
    FIRE_RESISTANCE_ID uuid,
    FIRE_AUTOMATION_REQUIRED boolean not null,
    BUILDING_TECHNICAL_INSPECTIONS_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_BUILDING_FIRE_SAFETY
-- begin TSADV_MILITARY_FORM
create table TSADV_MILITARY_FORM (
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
    DATE_FROM date,
    DATE_TO date,
    MILITARY_DOCUMENT_TYPE_ID uuid,
    MILITARY_DOCUMENT_TYPE_NAME varchar(2000),
    UDO_ID uuid,
    DOCUMENT_NUMBER varchar(100),
    MILITARY_TYPE_ID uuid,
    MILITARY_TYPE_NAME varchar(2000),
    ATTITUDE_TO_MILITARY_ID uuid,
    ATTITUDE_TO_MILITARY_NAME varchar(2000),
    TROOPS_STRUCTURE_ID uuid,
    COMPOSITION_MILITARY_REGISTRATION varchar(2000),
    MILITARY_RANK_ID uuid,
    MILITARY_RANK_NAME varchar(2000),
    OFFICER_TYPE_ID uuid,
    OFFICER_TYPE_NAME varchar(2000),
    SUITABILITY_TO_MILITARY_ID uuid,
    SPECIALIZATION varchar(255),
    ISSUE_DOC_DATE date,
    ISSUING_AUTHORITY varchar(2000),
    DELY boolean,
    DELY_DESCRIPTION varchar(255),
    REGISTER_GROUP_ID uuid,
    REGISTER_CATEGORY_ID uuid,
    DATE_POST date,
    PERSON_GROUP_ID uuid,
    START_DATE_HISTORY date,
    END_DATE_HISTORY date,
    --
    primary key (ID)
)^
-- end TSADV_MILITARY_FORM
-- begin TSADV_JOB_TEST
create table TSADV_JOB_TEST (
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
    JOB_GROUP_ID uuid not null,
    TEST_ID uuid,
    PURPOSE varchar(50) not null,
    START_DATE date not null,
    END_DATE date not null,
    --
    primary key (ID)
)^
-- end TSADV_JOB_TEST
-- begin TSADV_RC_ANSWER
create table TSADV_RC_ANSWER (
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
    QUESTION_ID uuid not null,
    ORDER_ integer not null,
    ANSWER_TEXT1 varchar(2000) not null,
    ANSWER_TEXT2 varchar(2000),
    ANSWER_TEXT3 varchar(2000),
    ANSWER_TEXT4 varchar(2000),
    ANSWER_TEXT5 varchar(2000),
    ANSWER_RESULT varchar(50),
    POSITIVE_ boolean not null,
    ANSWERTEXT varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_RC_ANSWER
-- begin TSADV_COMPETENCE_TEMPLATE
create table TSADV_COMPETENCE_TEMPLATE (
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
    START_DATE date not null,
    END_DATE date not null,
    USE_POSITION_COMPETENCE boolean not null,
    POSITION_COMPETENCE_WEIGHT integer not null,
    COMPETENCE_TEMPLATE_NAME varchar(255) not null,
    --
    primary key (ID)
)^
-- end TSADV_COMPETENCE_TEMPLATE
-- begin TSADV_PERSONAL_DATA_REQUEST
create table TSADV_PERSONAL_DATA_REQUEST (
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
    LAST_NAME varchar(255),
    REQUEST_NUMBER bigint,
    FIRST_NAME varchar(255),
    MIDDLE_NAME varchar(255),
    LAST_NAME_LATIN varchar(255),
    FIRST_NAME_LATIN varchar(255),
    MIDDLE_NAME_LATIN varchar(255),
    MARITAL_STATUS_ID uuid,
    DATE_OF_BIRTH date,
    ATTACHMENT_ID uuid,
    STATUS_ID uuid,
    PERSON_GROUP_ID uuid,
    NATIONALITY_ID uuid,
    CITIZENSHIP_ID uuid,
    NATIONAL_IDENTIFIER varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_PERSONAL_DATA_REQUEST
-- begin TSADV_BENEFICIARY
create table TSADV_BENEFICIARY (
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
    PERSON_GROUP_PARENT_ID uuid,
    PERSON_GROUP_CHILD_ID uuid,
    DATE_FROM date,
    DATE_TO date,
    GET_ALIMONY boolean,
    RELATIONSHIP_TYPE_ID uuid,
    LAST_NAME varchar(2000),
    FIRST_NAME varchar(2000),
    MIDDLE_NAME varchar(2000),
    BIRTH_DATE date,
    WORK_LOCATION varchar(2000),
    HOME_ADDRESS varchar(2000),
    ADDITIONAL_CONTACT varchar(2000),
    START_DATE_HISTORY date,
    END_DATE_HISTORY date,
    PERSON_GROUP_ID uuid,
    RELATED_PERSON_GROUP_ID uuid,
    RELATION_DEGREE_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_BENEFICIARY
-- begin TSADV_JOB_GROUP_GOAL_LINK
create table TSADV_JOB_GROUP_GOAL_LINK (
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
    WEIGHT integer,
    TARGET_VALUE integer,
    JOB_GROUP_ID uuid,
    GOAL_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_JOB_GROUP_GOAL_LINK
-- begin TSADV_PRE_AND_POST_SHIFT_INSPECTION
create table TSADV_PRE_AND_POST_SHIFT_INSPECTION (
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
    ORGANIZATION_ID uuid not null,
    ENTRY_DATE date not null,
    INSPECTION_TYPE_ID uuid not null,
    INSPECTED_NUMBER bigint,
    PASSED_PRE_SHIFT_INSPECTION bigint,
    PASSED_POST_SHIFT_INSPECTION bigint,
    HARMFULL_FACTORS_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_PRE_AND_POST_SHIFT_INSPECTION
-- begin TSADV_WORKPLACE_MSDS
create table TSADV_WORKPLACE_MSDS (
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
    DEVELOP_PLAN_DATE date not null,
    DEVELOP_FACT_DATE date not null,
    MSDS_ID uuid,
    DOCUMENT_ID uuid,
    STATUS_ID uuid not null,
    DONE_PLAN_DATE date,
    DONE_FACT_DATE date,
    WORK_PLACE_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_WORKPLACE_MSDS
-- begin TSADV_INTERVIEW_DETAIL
create table TSADV_INTERVIEW_DETAIL (
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
    INTERVIEW_ID uuid not null,
    INTERVIEWER_PERSON_GROUP_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_INTERVIEW_DETAIL
-- begin TSADV_WORK_PLACE_MONITORING
create table TSADV_WORK_PLACE_MONITORING (
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
    MONITORING_DATE date not null,
    INSPECTOR_FULL_NAME_ID uuid not null,
    CONTROL_STAGE_ID uuid not null,
    WORK_PLACE_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_WORK_PLACE_MONITORING
-- begin TSADV_COURSE_PRE_REQUISITION
create table TSADV_COURSE_PRE_REQUISITION (
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
    COURSE_ID uuid not null,
    REQUISITION_COURSE_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_COURSE_PRE_REQUISITION
-- begin TSADV_MILITARY_FORM_REQUEST
create table TSADV_MILITARY_FORM_REQUEST (
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
    DATE_FROM date,
    DATE_TO date,
    MILITARY_DOCUMENT_TYPE_ID uuid,
    MILITARY_DOCUMENT_TYPE_NAME varchar(2000),
    UDO_ID uuid,
    DOCUMENT_NUMBER varchar(100),
    MILITARY_TYPE_ID uuid,
    MILITARY_TYPE_NAME varchar(2000),
    ATTITUDE_TO_MILITARY_ID uuid,
    TROOPS_STRUCTURE_ID uuid,
    COMPOSITION_MILITARY_REGISTRATION varchar(2000),
    MILITARY_RANK_ID uuid,
    MILITARY_RANK_NAME varchar(2000),
    OFFICER_TYPE_ID uuid,
    OFFICER_TYPE_NAME varchar(2000),
    SUITABILITY_TO_MILITARY_ID uuid,
    SPECIALIZATION varchar(255),
    ISSUE_DOC_DATE date,
    ISSUING_AUTHORITY varchar(2000),
    DELY boolean,
    DELY_DESCRIPTION varchar(255),
    REGISTER_GROUP_ID uuid,
    REGISTER_CATEGORY_ID uuid,
    DATE_POST date,
    PERSON_GROUP_ID uuid,
    REQUEST_STATUS_ID uuid,
    FILE_ID uuid,
    MILITARY_FORM_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_MILITARY_FORM_REQUEST
-- begin TSADV_OFFER_HISTORY
create table TSADV_OFFER_HISTORY (
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
    STATUS_CHANGE_DATE date,
    STATUS integer,
    CHANGE_REASON text,
    DECLINE_REASON text,
    CHANGED_BY_ID uuid,
    OFFER_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_OFFER_HISTORY
-- begin TSADV_TRAINER
create table TSADV_TRAINER (
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
    EMPLOYEE_ID uuid not null,
    ADD_PAYMENT_AMOUNT integer,
    ORDER_NUMBER varchar(255),
    ORDER_DATE date,
    PARTY_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_TRAINER
-- begin TSADV_RCG_FEEDBACK_COMMENT
create table TSADV_RCG_FEEDBACK_COMMENT (
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
    TEXT varchar(2000) not null,
    PARENT_COMMENT_ID uuid,
    TEXT_EN varchar(2000) not null,
    TEXT_RU varchar(2000) not null,
    AUTHOR_ID uuid not null,
    RCG_FEEDBACK_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_RCG_FEEDBACK_COMMENT
-- begin TSADV_ASSIGNED_EVENT
create table TSADV_ASSIGNED_EVENT (
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
    FACT bigint,
    PARENT_EVENT_ID uuid,
    SAFETY_EVENT_ID uuid,
    ASSIGNMENT_ID uuid,
    ASSIGNED_ID uuid,
    STATUS_ID uuid not null,
    DEADLINE date not null,
    INVESTMENT_PLAN bigint,
    BUDGET_PLAN bigint,
    INVESTMENT_FACT bigint,
    BUDGET_FACT bigint,
    SAFETY_PLAN_EVENT_ID uuid,
    ORGANIZATION_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_ASSIGNED_EVENT
-- begin TSADV_JOB_REQUEST
create table TSADV_JOB_REQUEST (
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
    REQUISITION_ID uuid not null,
    SOURCE_ID uuid,
    OTHER_SOURCE varchar(255),
    CANDIDATE_PERSON_GROUP_ID uuid not null,
    REQUEST_DATE date not null,
    REQUEST_STATUS integer not null,
    JOB_REQUEST_REASON_ID uuid,
    REASON varchar(200),
    VIDEO_FILE_ID uuid,
    IS_RESERVED boolean not null,
    SELECTED_BY_MANAGER boolean not null,
    SENT boolean not null,
    NAME varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_JOB_REQUEST
-- begin TSADV_STUDENT_GRANT
create table TSADV_STUDENT_GRANT (
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
    NUMBER_CONTRACT varchar(255),
    DATE_CONTRACT date,
    LEARNING_CENTER varchar(255),
    SPECIALIZATION varchar(500),
    LEARNING_FORM_ID uuid,
    LEARN_END_DATE date,
    REASON varchar(500),
    ORDER_REQUISITION varchar(1000),
    PERSON_GROUP_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_STUDENT_GRANT
-- begin TSADV_REQUISITION_HISTORY
create table TSADV_REQUISITION_HISTORY (
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
    REQUISITION_ID uuid not null,
    STATUS integer not null,
    REASON varchar(2000),
    OPENED_POSITIONS_COUNT double precision not null,
    --
    primary key (ID)
)^
-- end TSADV_REQUISITION_HISTORY
-- begin TSADV_OCCUPATIONAL_MEDICINE
create table TSADV_OCCUPATIONAL_MEDICINE (
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
    ORGANIZATION_ID uuid not null,
    ENTRY_DATE date not null,
    --
    primary key (ID)
)^
-- end TSADV_OCCUPATIONAL_MEDICINE
-- begin TSADV_LOOKUP_TYPE
create table TSADV_LOOKUP_TYPE (
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
    LOOKUP_TYPE varchar(255) not null,
    LOOKUP_NAME_LANG1 varchar(255) not null,
    LOOKUP_NAME_LANG2 varchar(255),
    LOOKUP_NAME_LANG3 varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_LOOKUP_TYPE
-- begin TSADV_COURSE_SECTION
create table TSADV_COURSE_SECTION (
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
    COURSE_ID uuid not null,
    MANDATORY boolean not null,
    SECTION_NAME varchar(255) not null,
    ORDER_ integer not null,
    SECTION_OBJECT_ID uuid,
    FORMAT_ID uuid not null,
    DESCRIPTION text,
    --
    primary key (ID)
)^
-- end TSADV_COURSE_SECTION
-- begin TSADV_IDP_DETAIL
create table TSADV_IDP_DETAIL (
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
    IDP_ID uuid,
    COMPETENCE_ID uuid,
    SCALE_LEVEL_ID uuid,
    COURSE_ID uuid,
    EDUCATION_TYPE_ID uuid,
    DESCRIPTION text,
    TARGET_DATE date not null,
    DONE boolean not null,
    COMMENT_ text,
    REASON varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_IDP_DETAIL
-- begin TSADV_TALENT_PROGRAM_PERSON_STEP
create table TSADV_TALENT_PROGRAM_PERSON_STEP (
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
    DIC_TALENT_PROGRAM_STEP_ID uuid not null,
    TALENT_PROGRAM_REQUEST_ID uuid,
    PERSON_GROUP_ID uuid not null,
    ADDRESS_RU varchar(255),
    ADDRESS_EN varchar(255),
    DATE_FROM timestamp,
    DATE_TO timestamp,
    STATUS_ID uuid,
    COMMENT_ varchar(255),
    RESULT_ varchar(255),
    FILE_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_TALENT_PROGRAM_PERSON_STEP
-- begin TSADV_COURSE_SECTION_ATTEMPT
create table TSADV_COURSE_SECTION_ATTEMPT (
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
    ATTEMPT_DATE date,
    TEST_ID uuid,
    TEST_RESULT integer,
    TEST_RESULT_PERCENT integer,
    TIME_SPENT bigint,
    ACTIVE_ATTEMPT boolean,
    SUCCESS boolean not null,
    ENROLLMENT_ID uuid not null,
    COURSE_SECTION_ID uuid,
    COURSE_SECTION_SESSION_ID uuid,
    COURSESECTIONFORMAT varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_COURSE_SECTION_ATTEMPT
-- begin TSADV_VIDEO_FILE_FOR_PLAY
create table TSADV_VIDEO_FILE_FOR_PLAY (
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
    SOURCE_ID uuid not null,
    OUTPUT_FILE_ID uuid,
    STATUS varchar(50) not null,
    --
    primary key (ID)
)^
-- end TSADV_VIDEO_FILE_FOR_PLAY
-- begin TSADV_MEDICAL_INSPECTION
create table TSADV_MEDICAL_INSPECTION (
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
    ENTRY_DATE date not null,
    ORGANIZATION_ID uuid,
    FACT_EMP_NUMBER bigint,
    SUBJECT_INSPECTION bigint,
    SUBJECT_INSPECTION_WOMAN bigint,
    PASSED_INSPECTION bigint,
    --
    primary key (ID)
)^
-- end TSADV_MEDICAL_INSPECTION
-- begin TSADV_OFFER
create table TSADV_OFFER (
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
    JOB_REQUEST_ID uuid not null,
    CANDIDATE_COMMENTARY varchar(1000),
    PROPOSED_SALARY double precision,
    CURRENCY_ID uuid,
    EXPIRE_DATE date,
    PROPOSED_START_DATE date,
    STATUS integer,
    OFFER_TEMPLATE_ID uuid,
    FILE_ID uuid,
    NEED_BUISNESS_PARTNER_APPROVE boolean not null,
    --
    primary key (ID)
)^
-- end TSADV_OFFER
-- begin TSADV_PERFORMANCE_PLAN
create table TSADV_PERFORMANCE_PLAN (
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
    PERFORMANCE_PLAN_NAME varchar(240) not null,
    PREVIOUS_PLAN_ID uuid,
    DESCRIPTION varchar(2000),
    ADMINISTRATOR_PERSON_GROUP_ID uuid,
    START_DATE date not null,
    END_DATE date not null,
    ACCESSIBILITY_START_DATE date,
    ACCESSIBILITY_END_DATE date,
    PERFORMANCE_PLAN_NAME_KZ varchar(255),
    PERFORMANCE_PLAN_NAME_EN varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_PERFORMANCE_PLAN
-- begin TSADV_TEST_SECTION
create table TSADV_TEST_SECTION (
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
    TEST_ID uuid not null,
    DYNAMIC_LOAD boolean not null,
    GENERATE_COUNT integer,
    SECTION_NAME varchar(255) not null,
    QUESTION_ORDER integer not null,
    QUESTION_BANK_ID uuid not null,
    QUESTION_PER_PAGE integer not null,
    ANSWER_ORDER integer not null,
    POINTS_PER_QUESTION integer,
    --
    primary key (ID)
)^
-- end TSADV_TEST_SECTION
-- begin TSADV_SUCCESSION_PLANNING
create table TSADV_SUCCESSION_PLANNING (
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
    POSITION_GROUP_ID uuid not null,
    START_DATE date not null,
    END_DATE date not null,
    DESCRIPTION text,
    PERSON_GROUP_ID uuid,
    POSITIONNAME varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_SUCCESSION_PLANNING
-- begin TSADV_PERSON_CLINIC_DISPANCER
create table TSADV_PERSON_CLINIC_DISPANCER (
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
    START_DATE_HISTORY date,
    END_DATE_HISTORY date,
    HAVE_CLINIC_DISPANCER varchar(50),
    PERIOD_FROM varchar(2000),
    --
    primary key (ID)
)^
-- end TSADV_PERSON_CLINIC_DISPANCER
-- begin TSADV_ABSENCE
create table TSADV_ABSENCE (
    ID uuid,
    CATEGORY_ID uuid,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    VERSION integer not null,
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NOTIFICATION_DATE date,
    FILE_ID uuid,
    PARENT_ABSENCE_ID uuid,
    ORDER_NUM varchar(255),
    ORDER_DATE date,
    DATE_FROM date not null,
    DATE_TO date not null,
    ABSENCE_DAYS integer not null,
    ADDITIONAL_DAYS integer,
    ORDER_ID uuid,
    TYPE_ID uuid not null,
    ABSENCE_STATUS_ID uuid,
    ORD_ASSIGNMENT_ID uuid,
    ABSENCE_REQUEST_ID uuid,
    PERSON_GROUP_ID uuid,
    LEGACY_ID varchar(255),
    PERIOD_START date,
    PERIOD_END date,
    USE_IN_BALANCE boolean,
    PURPOSE_TEXT varchar(2000),
    PURPOSE_ID uuid,
    TIME_OF_STARTING timestamp,
    TIME_OF_FINISHING timestamp,
    TOTAL_HOURS integer,
    COMPENCATION boolean,
    VACATION_DAY boolean,
    ACQUAINTED boolean,
    AGREE boolean,
    TRANSFER_PERIOD_START timestamp,
    TRANSFER_PERIOD_END timestamp,
    --
    primary key (ID)
)^
-- end TSADV_ABSENCE
-- begin TSADV_ORDER
create table TSADV_ORDER (
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
    START_DATE date,
    END_DATE date,
    WRITE_HISTORY boolean,
    --
    ORDER_NUMBER varchar(255) not null,
    ORDER_DATE date not null,
    PARENT_ID uuid,
    GROUP_ID uuid,
    ORDER_TYPE_ID uuid not null,
    ORDER_STATUS_ID uuid,
    CANCEL_ORDER_NUMBER integer,
    CANCEL_ORDER_DATE date,
    CANCEL_ORDER_REASON_ID uuid,
    APPROVER_PERSON_GROUP_ID uuid,
    ORDER_REASON_ID uuid,
    CAPTION varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_ORDER
-- begin TSADV_INFO_SALARY_MARKET
create table TSADV_INFO_SALARY_MARKET (
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
    START_DATE date,
    END_DATE date,
    WRITE_HISTORY boolean,
    --
    MIN_ integer not null,
    MID integer not null,
    MAX_ integer not null,
    MEDIANA integer,
    KVART1 integer,
    KVART2 integer,
    KVART3 integer,
    KVART4 integer,
    JOB_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_INFO_SALARY_MARKET
-- begin TSADV_GRADE
create table TSADV_GRADE (
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
    START_DATE date,
    END_DATE date,
    WRITE_HISTORY boolean,
    --
    GRADE_NAME varchar(500) not null,
    RECOGNITION_NOMINATE boolean not null,
    GROUP_ID uuid,
    BONUS_PERCENT double precision,
    --
    primary key (ID)
)^
-- end TSADV_GRADE
-- begin TSADV_GRADE_RULE_VALUE
create table TSADV_GRADE_RULE_VALUE (
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
    START_DATE date,
    END_DATE date,
    WRITE_HISTORY boolean,
    --
    VALUE_ double precision,
    MIN_ double precision,
    MID double precision,
    MAX_ double precision,
    GRADE_GROUP_ID uuid not null,
    GROUP_ID uuid not null,
    GRADE_RULE_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_GRADE_RULE_VALUE
-- begin TSADV_JOB
create table TSADV_JOB (
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
    START_DATE date,
    END_DATE date,
    WRITE_HISTORY boolean,
    --
    JOB_NAME_LANG1 varchar(1000) not null,
    JOB_NAME_LANG2 varchar(1000),
    JOB_NAME_LANG3 varchar(1000),
    JOB_NAME_LANG4 varchar(1000),
    JOB_NAME_LANG5 varchar(1000),
    GROUP_ID uuid,
    INSTRUCTION bytea,
    INSTRUCTION_NAME varchar(255),
    EMPLOYEE_CATEGORY_ID uuid,
    CANDIDATE_REQUIREMENTS_LANG1 text,
    CANDIDATE_REQUIREMENTS_LANG2 text,
    CANDIDATE_REQUIREMENTS_LANG3 text,
    CANDIDATE_REQUIREMENTS_LANG4 text,
    CANDIDATE_REQUIREMENTS_LANG5 text,
    JOB_DESCRIPTION_LANG1 text,
    JOB_DESCRIPTION_LANG2 text,
    JOB_DESCRIPTION_LANG3 text,
    JOB_DESCRIPTION_LANG4 text,
    JOB_DESCRIPTION_LANG5 text,
    JOB_CATEGORY_ID uuid,
    CANDIDATEREQUIREMENTS varchar(255),
    JOBDESCRIPTION varchar(255),
    --
    primary key (ID)
)^
-- end TSADV_JOB
-- begin TSADV_VACATION_CONDITIONS
create table TSADV_VACATION_CONDITIONS (
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
    START_DATE date,
    END_DATE date,
    WRITE_HISTORY boolean,
    --
    ADDITIONAL_DAYS integer not null,
    VACATION_DURATION_TYPE varchar(50),
    MAIN_DAYS_NUMBER bigint,
    DAYS_TYPE_ID uuid,
    GROUP_ID uuid,
    POSITION_GROUP_ID uuid,
    JOB_GROUP_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_VACATION_CONDITIONS
-- begin TSADV_SALARY
create table TSADV_SALARY (
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
    START_DATE date,
    END_DATE date,
    WRITE_HISTORY boolean,
    --
    ASSIGNMENT_GROUP_ID uuid,
    SALARY_REQUEST_ID uuid,
    AMOUNT double precision,
    NET_GROSS varchar(50) not null,
    REASON_ID uuid,
    CURRENCY_ID uuid not null,
    ORD_ASSIGNMENT_ID uuid,
    ORDER_GROUP_ID uuid,
    AGREEMENT_ID uuid,
    TYPE_ varchar(50) not null,
    --
    primary key (ID)
)^
-- end TSADV_SALARY
-- begin TSADV_GLOBAL_VALUE
create table TSADV_GLOBAL_VALUE (
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
    START_DATE date,
    END_DATE date,
    WRITE_HISTORY boolean,
    --
    NAME varchar(255),
    VALUE_ varchar(255),
    CODE varchar(255) not null,
    GROUP_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_GLOBAL_VALUE
-- begin TSADV_LEARNING_PATH
create table TSADV_LEARNING_PATH (
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
    START_DATE date,
    END_DATE date,
    WRITE_HISTORY boolean,
    --
    NAME varchar(1000) not null,
    CATEGORY_ID uuid not null,
    DESCRIPTION text not null,
    --
    primary key (ID)
)^
-- end TSADV_LEARNING_PATH
-- begin TSADV_COMPETENCE
create table TSADV_COMPETENCE (
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
    START_DATE date,
    END_DATE date,
    WRITE_HISTORY boolean,
    --
    COMPETENCE_NAME_LANG1 varchar(1000) not null,
    COMPETENCE_NAME_LANG2 varchar(1000),
    COMPETENCE_NAME_LANG3 varchar(1000),
    COMPETENCE_NAME_LANG4 varchar(1000),
    COMPETENCE_NAME_LANG5 varchar(1000),
    COMPETECE_TYPE_ID uuid,
    SCALE_ID uuid not null,
    GROUP_ID uuid,
    ATTACHMENT_ID uuid,
    IS_RC_AVAILABLE boolean,
    --
    primary key (ID)
)^
-- end TSADV_COMPETENCE
-- begin TSADV_ABSENCE_REQUEST
create table TSADV_ABSENCE_REQUEST (
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
    REQUEST_NUMBER bigint not null,
    STATUS_ID uuid not null,
    REQUEST_DATE date not null,
    COMMENT_ varchar(3000),
    --
    ASSIGNMENT_GROUP_ID uuid,
    REASON varchar(2000),
    ATTACHMENT_ID uuid,
    DATE_FROM date,
    DATE_TO date,
    ABSENCE_DAYS integer,
    TYPE_ID uuid,
    DISTANCE_WORKING_CONFIRM boolean not null,
    PERSON_GROUP_ID uuid,
    PURPOSE_ID uuid,
    PURPOSE_TEXT varchar(2000),
    TIME_OF_STARTING timestamp,
    ORIGINAL_SHEET boolean,
    SCHEDULE_START_DATE date,
    SCHEDULE_END_DATE date,
    ADD_NEXT_YEAR boolean,
    NEW_START_DATE date,
    NEW_END_DATE date,
    PERIOD_DATE_FROM date,
    PERIOD_DATE_TO date,
    TIME_OF_FINISHING timestamp,
    TOTAL_HOURS integer,
    COMPENCATION boolean,
    VACATION_DAY boolean,
    ACQUAINTED boolean,
    AGREE boolean,
    VACATION_SCHEDULE_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_ABSENCE_REQUEST
-- begin TSADV_SCHEDULE_OFFSETS_REQUEST
create table TSADV_SCHEDULE_OFFSETS_REQUEST (
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
    REQUEST_NUMBER bigint not null,
    STATUS_ID uuid not null,
    REQUEST_DATE date not null,
    COMMENT_ varchar(3000),
    --
    PERSON_GROUP_ID uuid,
    PURPOSE_ID uuid,
    PURPOSE_TEXT varchar(2000),
    CURRENT_SCHEDULE_ID uuid,
    NEW_SCHEDULE_ID uuid,
    DATE_OF_NEW_SCHEDULE date,
    DATE_OF_START_NEW_SCHEDULE date,
    DETAILS_OF_ACTUAL_WORK varchar(2000),
    AGREE boolean,
    ACQUAINTED boolean,
    --
    primary key (ID)
)^
-- end TSADV_SCHEDULE_OFFSETS_REQUEST
-- begin TSADV_ABSENCE_FOR_RECALL
create table TSADV_ABSENCE_FOR_RECALL (
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
    REQUEST_NUMBER bigint not null,
    STATUS_ID uuid not null,
    REQUEST_DATE date not null,
    COMMENT_ varchar(3000),
    --
    ABSENCE_TYPE_ID uuid,
    EMPLOYEE_ID uuid,
    VACATION_ID uuid,
    RECALL_DATE_FROM date,
    RECALL_DATE_TO date,
    DATE_FROM date,
    DATE_TO date,
    PURPOSE_ID uuid,
    PURPOSE_TEXT varchar(255),
    IS_AGREE boolean not null,
    IS_FAMILIARIZATION boolean not null,
    LEAVE_OTHER_TIME boolean not null,
    COMPENSATION_PAYMENT boolean not null,
    --
    primary key (ID)
)^
-- end TSADV_ABSENCE_FOR_RECALL
-- begin TSADV_CERTIFICATE_REQUEST
create table TSADV_CERTIFICATE_REQUEST (
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
    REQUEST_NUMBER bigint not null,
    STATUS_ID uuid not null,
    REQUEST_DATE date not null,
    COMMENT_ varchar(3000),
    --
    PERSON_GROUP_ID uuid not null,
    CERTIFICATE_TYPE_ID uuid not null,
    RECEIVING_TYPE_ID uuid not null,
    LANGUAGE_ID uuid not null,
    SHOW_SALARY boolean not null,
    NUMBER_OF_COPY integer not null,
    FILE_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_CERTIFICATE_REQUEST
-- begin TSADV_ORDER_GROUP
create table TSADV_ORDER_GROUP (
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
    primary key (ID)
)^
-- end TSADV_ORDER_GROUP
-- begin TSADV_JOB_GROUP
create table TSADV_JOB_GROUP (
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
    JOB_NAME_LANG1 varchar(1000),
    JOB_NAME_LANG2 varchar(1000),
    JOB_NAME_LANG3 varchar(1000),
    JOB_NAME_LANG4 varchar(1000),
    JOB_NAME_LANG5 varchar(1000),
    EMPLOYEE_CATEGORY_ID uuid,
    JOB_CATEGORY_ID uuid,
    COMPANY_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_JOB_GROUP
-- begin TSADV_GRADE_GROUP
create table TSADV_GRADE_GROUP (
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
    COMPANY_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_GRADE_GROUP
-- begin TSADV_GLOBAL_VALUE_GROUP
create table TSADV_GLOBAL_VALUE_GROUP (
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
    primary key (ID)
)^
-- end TSADV_GLOBAL_VALUE_GROUP
-- begin TSADV_HIERARCHY_ELEMENT_GROUP
create table TSADV_HIERARCHY_ELEMENT_GROUP (
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
    primary key (ID)
)^
-- end TSADV_HIERARCHY_ELEMENT_GROUP
-- begin TSADV_COMPETENCE_GROUP
create table TSADV_COMPETENCE_GROUP (
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
    primary key (ID)
)^
-- end TSADV_COMPETENCE_GROUP
-- begin TSADV_VACATION_CONDITIONS_GROUP
create table TSADV_VACATION_CONDITIONS_GROUP (
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
    primary key (ID)
)^
-- end TSADV_VACATION_CONDITIONS_GROUP
-- begin TSADV_CONTRACT_GROUP
create table TSADV_CONTRACT_GROUP (
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
    primary key (ID)
)^
-- end TSADV_CONTRACT_GROUP
-- begin TSADV_GRADE_RULE_VALUE_GROUP
create table TSADV_GRADE_RULE_VALUE_GROUP (
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
    primary key (ID)
)^
-- end TSADV_GRADE_RULE_VALUE_GROUP
-- begin TSADV_PERSON_EDUCATION_REQUEST_FILE_DESCRIPTOR_LINK
create table TSADV_PERSON_EDUCATION_REQUEST_FILE_DESCRIPTOR_LINK (
    PERSON_EDUCATION_REQUEST_ID uuid,
    FILE_DESCRIPTOR_ID uuid,
    primary key (PERSON_EDUCATION_REQUEST_ID, FILE_DESCRIPTOR_ID)
)^
-- end TSADV_PERSON_EDUCATION_REQUEST_FILE_DESCRIPTOR_LINK
-- begin TSADV_ORG_STRUCTURE_REQUEST_FILE_DESCRIPTOR_LINK
create table TSADV_ORG_STRUCTURE_REQUEST_FILE_DESCRIPTOR_LINK (
    ORG_STRUCTURE_REQUEST_ID uuid,
    FILE_DESCRIPTOR_ID uuid,
    primary key (ORG_STRUCTURE_REQUEST_ID, FILE_DESCRIPTOR_ID)
)^
-- end TSADV_ORG_STRUCTURE_REQUEST_FILE_DESCRIPTOR_LINK
-- begin TSADV_MILITARY_FORM_FILE_DESCRIPTOR_LINK
create table TSADV_MILITARY_FORM_FILE_DESCRIPTOR_LINK (
    MILITARY_FORM_ID uuid,
    FILE_DESCRIPTOR_ID uuid,
    primary key (MILITARY_FORM_ID, FILE_DESCRIPTOR_ID)
)^
-- end TSADV_MILITARY_FORM_FILE_DESCRIPTOR_LINK
-- begin TSADV_PERSON_DOCUMENT_FILE_DESCRIPTOR_LINK
create table TSADV_PERSON_DOCUMENT_FILE_DESCRIPTOR_LINK (
    PERSON_DOCUMENT_ID uuid,
    FILE_DESCRIPTOR_ID uuid,
    primary key (PERSON_DOCUMENT_ID, FILE_DESCRIPTOR_ID)
)^
-- end TSADV_PERSON_DOCUMENT_FILE_DESCRIPTOR_LINK
-- begin TSADV_PERSON_DOCUMENT_REQUEST_FILE_DESCRIPTOR_LINK
create table TSADV_PERSON_DOCUMENT_REQUEST_FILE_DESCRIPTOR_LINK (
    PERSON_DOCUMENT_REQUEST_ID uuid,
    FILE_DESCRIPTOR_ID uuid,
    primary key (PERSON_DOCUMENT_REQUEST_ID, FILE_DESCRIPTOR_ID)
)^
-- end TSADV_PERSON_DOCUMENT_REQUEST_FILE_DESCRIPTOR_LINK
-- begin TSADV_PERSON_QUALIFICATION_FILE_DESCRIPTOR_LINK
create table TSADV_PERSON_QUALIFICATION_FILE_DESCRIPTOR_LINK (
    PERSON_QUALIFICATION_ID uuid,
    FILE_DESCRIPTOR_ID uuid,
    primary key (PERSON_QUALIFICATION_ID, FILE_DESCRIPTOR_ID)
)^
-- end TSADV_PERSON_QUALIFICATION_FILE_DESCRIPTOR_LINK
-- begin TSADV_PERFORMANCE_PLAN_POSITION_GROUP_LINK
create table TSADV_PERFORMANCE_PLAN_POSITION_GROUP_LINK (
    POSITION_GROUP_ID uuid,
    PERFORMANCE_PLAN_ID uuid,
    primary key (POSITION_GROUP_ID, PERFORMANCE_PLAN_ID)
)^
-- end TSADV_PERFORMANCE_PLAN_POSITION_GROUP_LINK
-- begin TSADV_PERSONAL_DATA_REQUEST_FILE_DESCRIPTOR_LINK
create table TSADV_PERSONAL_DATA_REQUEST_FILE_DESCRIPTOR_LINK (
    PERSONAL_DATA_REQUEST_ID uuid,
    FILE_DESCRIPTOR_ID uuid,
    primary key (PERSONAL_DATA_REQUEST_ID, FILE_DESCRIPTOR_ID)
)^
-- end TSADV_PERSONAL_DATA_REQUEST_FILE_DESCRIPTOR_LINK
-- begin TSADV_IMPROVING_PROFESSIONAL_SKILLS_FILE_DESCRIPTOR_LINK
create table TSADV_IMPROVING_PROFESSIONAL_SKILLS_FILE_DESCRIPTOR_LINK (
    IMPROVING_PROFESSIONAL_SKILLS_ID uuid,
    FILE_DESCRIPTOR_ID uuid,
    primary key (IMPROVING_PROFESSIONAL_SKILLS_ID, FILE_DESCRIPTOR_ID)
)^
-- end TSADV_IMPROVING_PROFESSIONAL_SKILLS_FILE_DESCRIPTOR_LINK
-- begin TSADV_PERSON_EXPERIENCE_REQUEST_FILE_DESCRIPTOR_LINK
create table TSADV_PERSON_EXPERIENCE_REQUEST_FILE_DESCRIPTOR_LINK (
    PERSON_EXPERIENCE_REQUEST_ID uuid,
    FILE_DESCRIPTOR_ID uuid,
    primary key (PERSON_EXPERIENCE_REQUEST_ID, FILE_DESCRIPTOR_ID)
)^
-- end TSADV_PERSON_EXPERIENCE_REQUEST_FILE_DESCRIPTOR_LINK
-- begin TSADV_ABSENCE_FOR_RECALL_FILE_DESCRIPTOR_LINK
create table TSADV_ABSENCE_FOR_RECALL_FILE_DESCRIPTOR_LINK (
    ABSENCE_FOR_RECALL_ID uuid,
    FILE_DESCRIPTOR_ID uuid,
    primary key (ABSENCE_FOR_RECALL_ID, FILE_DESCRIPTOR_ID)
)^
-- end TSADV_ABSENCE_FOR_RECALL_FILE_DESCRIPTOR_LINK
-- begin TSADV_PERSON_QUALIFICATION_REQUEST_FILE_DESCRIPTOR_LINK
create table TSADV_PERSON_QUALIFICATION_REQUEST_FILE_DESCRIPTOR_LINK (
    PERSON_QUALIFICATION_REQUEST_ID uuid,
    FILE_DESCRIPTOR_ID uuid,
    primary key (PERSON_QUALIFICATION_REQUEST_ID, FILE_DESCRIPTOR_ID)
)^
-- end TSADV_PERSON_QUALIFICATION_REQUEST_FILE_DESCRIPTOR_LINK
-- begin TSADV_INSURED_PERSON_FILE_DESCRIPTOR_LINK
create table TSADV_INSURED_PERSON_FILE_DESCRIPTOR_LINK (
    INSURED_PERSON_ID uuid,
    FILE_DESCRIPTOR_ID uuid,
    primary key (INSURED_PERSON_ID, FILE_DESCRIPTOR_ID)
)^
-- end TSADV_INSURED_PERSON_FILE_DESCRIPTOR_LINK
-- begin TSADV_ADDRESS_FILE_DESCRIPTOR_LINK
create table TSADV_ADDRESS_FILE_DESCRIPTOR_LINK (
    ADDRESS_ID uuid,
    FILE_DESCRIPTOR_ID uuid,
    primary key (ADDRESS_ID, FILE_DESCRIPTOR_ID)
)^
-- end TSADV_ADDRESS_FILE_DESCRIPTOR_LINK
-- begin TSADV_MILITARY_FORM_REQUEST_FILE_DESCRIPTOR_LINK
create table TSADV_MILITARY_FORM_REQUEST_FILE_DESCRIPTOR_LINK (
    MILITARY_FORM_REQUEST_ID uuid,
    FILE_DESCRIPTOR_ID uuid,
    primary key (MILITARY_FORM_REQUEST_ID, FILE_DESCRIPTOR_ID)
)^
-- end TSADV_MILITARY_FORM_REQUEST_FILE_DESCRIPTOR_LINK
-- begin TSADV_PERFORMANCE_PLAN_JOB_GROUP_LINK
create table TSADV_PERFORMANCE_PLAN_JOB_GROUP_LINK (
    JOB_GROUP_ID uuid,
    PERFORMANCE_PLAN_ID uuid,
    primary key (JOB_GROUP_ID, PERFORMANCE_PLAN_ID)
)^
-- end TSADV_PERFORMANCE_PLAN_JOB_GROUP_LINK
-- begin TSADV_IMPROVING_PROFESSIONAL_SKILLS_REQ_FILE_DESCRIPTOR_LINK
create table TSADV_IMPROVING_PROFESSIONAL_SKILLS_REQ_FILE_DESCRIPTOR_LINK (
    IMPROVING_PROFESSIONAL_SKILLS_REQUEST_ID uuid,
    FILE_DESCRIPTOR_ID uuid,
    primary key (IMPROVING_PROFESSIONAL_SKILLS_REQUEST_ID, FILE_DESCRIPTOR_ID)
)^
-- end TSADV_IMPROVING_PROFESSIONAL_SKILLS_REQ_FILE_DESCRIPTOR_LINK
-- begin TSADV_RC_JOB_GROUP_JOB_GROUP_LINK
create table TSADV_RC_JOB_GROUP_JOB_GROUP_LINK (
    JOB_GROUP_ID uuid,
    RC_JOB_GROUP_ID uuid,
    primary key (JOB_GROUP_ID, RC_JOB_GROUP_ID)
)^
-- end TSADV_RC_JOB_GROUP_JOB_GROUP_LINK
-- begin TSADV_PERFORMANCE_PLAN_ORGANIZATION_GROUP_LINK
create table TSADV_PERFORMANCE_PLAN_ORGANIZATION_GROUP_LINK (
    ORGANIZATION_GROUP_ID uuid,
    PERFORMANCE_PLAN_ID uuid,
    primary key (ORGANIZATION_GROUP_ID, PERFORMANCE_PLAN_ID)
)^
-- end TSADV_PERFORMANCE_PLAN_ORGANIZATION_GROUP_LINK
-- begin TSADV_DISABILITY_REQUEST_FILE_DESCRIPTOR_LINK
create table TSADV_DISABILITY_REQUEST_FILE_DESCRIPTOR_LINK (
    DISABILITY_REQUEST_ID uuid,
    FILE_DESCRIPTOR_ID uuid,
    primary key (DISABILITY_REQUEST_ID, FILE_DESCRIPTOR_ID)
)^
-- end TSADV_DISABILITY_REQUEST_FILE_DESCRIPTOR_LINK
-- begin TSADV_PERSON_EDUCATION_FILE_DESCRIPTOR_LINK
create table TSADV_PERSON_EDUCATION_FILE_DESCRIPTOR_LINK (
    PERSON_EDUCATION_ID uuid,
    FILE_DESCRIPTOR_ID uuid,
    primary key (PERSON_EDUCATION_ID, FILE_DESCRIPTOR_ID)
)^
-- end TSADV_PERSON_EDUCATION_FILE_DESCRIPTOR_LINK
-- begin TSADV_PERSON_EXT_FILE_DESCRIPTOR_LINK
create table TSADV_PERSON_EXT_FILE_DESCRIPTOR_LINK (
    PERSON_EXT_ID uuid,
    FILE_DESCRIPTOR_ID uuid,
    primary key (PERSON_EXT_ID, FILE_DESCRIPTOR_ID)
)^
-- end TSADV_PERSON_EXT_FILE_DESCRIPTOR_LINK
-- begin TSADV_PERSON_EXPERIENCE_FILE_DESCRIPTOR_LINK
create table TSADV_PERSON_EXPERIENCE_FILE_DESCRIPTOR_LINK (
    PERSON_EXPERIENCE_ID uuid,
    FILE_DESCRIPTOR_ID uuid,
    primary key (PERSON_EXPERIENCE_ID, FILE_DESCRIPTOR_ID)
)^
-- end TSADV_PERSON_EXPERIENCE_FILE_DESCRIPTOR_LINK
-- begin SEC_USER
alter table SEC_USER add column PERSON_GROUP_ID uuid ^
alter table SEC_USER add column FULLNAMEWITHLOGIN varchar(255) ^
alter table SEC_USER add column IMAGE_ID uuid ^
alter table SEC_USER add column ATS_CODE varchar(255) ^
alter table SEC_USER add column INNER_NUMBER varchar(255) ^
alter table SEC_USER add column AVAILABILITY boolean ^
update SEC_USER set AVAILABILITY = false where AVAILABILITY is null ^
alter table SEC_USER alter column AVAILABILITY set not null ^
alter table SEC_USER add column MOBILE_PHONE varchar(255) ^
alter table SEC_USER add column TELEGRAM_CODE varchar(255) ^
alter table SEC_USER add column TELEGRAM_CHAT_ID varchar(255) ^
alter table SEC_USER add column PASSWORD_CHANGE_DATE date ^
alter table SEC_USER add column SHORT_NAME varchar(100) ^
alter table SEC_USER add column FULL_NAME varchar(100) ^
-- end SEC_USER
-- begin BASE_ASSIGNMENT
-- alter table BASE_ASSIGNMENT add column PERSON_GROUP_ID uuid ^
-- update BASE_ASSIGNMENT set PERSON_GROUP_ID = <default_value> ^
-- alter table BASE_ASSIGNMENT alter column PERSON_GROUP_ID set not null ^
alter table BASE_ASSIGNMENT add column PERSON_GROUP_ID uuid not null ^
alter table BASE_ASSIGNMENT add column SUBSTITUTE_EMPLOYEE_ID uuid ^
alter table BASE_ASSIGNMENT add column ORDER_NUMBER varchar(255) ^
alter table BASE_ASSIGNMENT add column ORDER_DATE date ^
alter table BASE_ASSIGNMENT add column DURATION_PROBATION_PERIOD integer ^
alter table BASE_ASSIGNMENT add column UNIT integer ^
alter table BASE_ASSIGNMENT add column PROBATION_END_DATE date ^
alter table BASE_ASSIGNMENT add column ASSIGNMENT_STATUS_ID uuid ^
alter table BASE_ASSIGNMENT add column ASSIGNMENT_CATEGORY_ID uuid ^
alter table BASE_ASSIGNMENT add column COST_CENTER_ID uuid ^
alter table BASE_ASSIGNMENT add column JOB_GROUP_ID uuid ^
alter table BASE_ASSIGNMENT add column GRADE_GROUP_ID uuid ^
alter table BASE_ASSIGNMENT add column ORGANIZATION_GROUP_ID uuid ^
alter table BASE_ASSIGNMENT add column PRIMARY_FLAG boolean ^
alter table BASE_ASSIGNMENT add column FTE double precision ^
alter table BASE_ASSIGNMENT add column GROUP_ID uuid ^
alter table BASE_ASSIGNMENT add column POSITION_GROUP_ID uuid ^
alter table BASE_ASSIGNMENT add column TEMPORARY_END_DATE date ^
alter table BASE_ASSIGNMENT add column DTYPE varchar(31) ^
update BASE_ASSIGNMENT set DTYPE = 'base$AssignmentExt' where DTYPE is null ^
-- end BASE_ASSIGNMENT
-- begin BASE_PERSON
alter table BASE_PERSON add column CATEGORY_ID uuid ^
alter table BASE_PERSON add column NONRESIDENT_TYPE_ID uuid ^
alter table BASE_PERSON add column DATE_OF_DEATH date ^
alter table BASE_PERSON add column GROUP_ID uuid ^
alter table BASE_PERSON add column MARITAL_STATUS_ID uuid ^
-- alter table BASE_PERSON add column TYPE_ID uuid ^
-- update BASE_PERSON set TYPE_ID = <default_value> ^
-- alter table BASE_PERSON alter column TYPE_ID set not null ^
alter table BASE_PERSON add column TYPE_ID uuid not null ^
alter table BASE_PERSON add column NATIONALITY_ID uuid ^
alter table BASE_PERSON add column CITIZENSHIP_ID uuid ^
alter table BASE_PERSON add column FULL_NAME_CYRILLIC varchar(255) ^
alter table BASE_PERSON add column FULL_NAME_NUMBER_CYRILLIC varchar(255) ^
alter table BASE_PERSON add column BIRTH_PLACE varchar(2000) ^
alter table BASE_PERSON add column ACADEMIC_DEGREE varchar(2000) ^
alter table BASE_PERSON add column SCIENTIFIC_WORKS_IVENTIONS varchar(2000) ^
alter table BASE_PERSON add column STATE_AWARDS varchar(2000) ^
alter table BASE_PERSON add column HAVE_CRIPPLE_CHILD varchar(50) ^
alter table BASE_PERSON add column HAVE_CHILD_WITHOUT_PARENT varchar(50) ^
alter table BASE_PERSON add column PREV_JOB_NDA varchar(50) ^
alter table BASE_PERSON add column PREV_JOB_OBLIGATION varchar(50) ^
alter table BASE_PERSON add column COMMITMENTS_FROM_PREV_JOB varchar(50) ^
alter table BASE_PERSON add column COMMITMENTS_LOAN boolean ^
alter table BASE_PERSON add column COMMITMENTS_CREDIT boolean ^
alter table BASE_PERSON add column COMMITMENTS_NOT_SUR_MAT_VALUES boolean ^
alter table BASE_PERSON add column HAVE_NDA varchar(50) ^
alter table BASE_PERSON add column REASON_FOR_DISMISSAL varchar(255) ^
alter table BASE_PERSON add column PREV_JOB_HR varchar(255) ^
alter table BASE_PERSON add column HAVE_CONVICTION varchar(50) ^
alter table BASE_PERSON add column REGISTERED_DISPENSARY varchar(50) ^
alter table BASE_PERSON add column DISPENSARY_PERIOD varchar(255) ^
alter table BASE_PERSON add column DISABILITY varchar(50) ^
alter table BASE_PERSON add column DISABILITY_GROUP varchar(255) ^
alter table BASE_PERSON add column CONTRAINDICATIONS_HEALTH varchar(50) ^
alter table BASE_PERSON add column CONTRAINDICATIONS_HEALTH_TEXT varchar(255) ^
alter table BASE_PERSON add column CHILD_UNDER18_WITHOUT_FATHER_OR_MOTHER varchar(50) ^
alter table BASE_PERSON add column CHILD_UNDER14_WITHOUT_FATHER_OR_MOTHER varchar(50) ^
alter table BASE_PERSON add column CRIMINAL_ADMINISTRATIVE_LIABILITY varchar(50) ^
alter table BASE_PERSON add column CRIMINAL_ADMINISTRATIVE_LIABILITY_PERIOID_REASON varchar(255) ^
alter table BASE_PERSON add column DTYPE varchar(31) ^
update BASE_PERSON set DTYPE = 'base$PersonExt' where DTYPE is null ^
-- end BASE_PERSON
-- begin BASE_HIERARCHY_ELEMENT
alter table BASE_HIERARCHY_ELEMENT add column POSITION_GROUP_ID uuid ^
alter table BASE_HIERARCHY_ELEMENT add column ORGANIZATION_GROUP_ID uuid ^
alter table BASE_HIERARCHY_ELEMENT add column PARENT_ID uuid ^
alter table BASE_HIERARCHY_ELEMENT add column PARENT_GROUP_ID uuid ^
alter table BASE_HIERARCHY_ELEMENT add column GROUP_ID uuid ^
alter table BASE_HIERARCHY_ELEMENT add column DTYPE varchar(31) ^
update BASE_HIERARCHY_ELEMENT set DTYPE = 'base$HierarchyElementExt' where DTYPE is null ^
-- end BASE_HIERARCHY_ELEMENT
-- begin BASE_POSITION
alter table BASE_POSITION add column COST_CENTER_ID uuid ^
alter table BASE_POSITION add column CANDIDATE_REQUIREMENTS_LANG1 text ^
alter table BASE_POSITION add column CANDIDATE_REQUIREMENTS_LANG2 text ^
alter table BASE_POSITION add column CANDIDATE_REQUIREMENTS_LANG3 text ^
alter table BASE_POSITION add column CANDIDATE_REQUIREMENTS_LANG4 text ^
alter table BASE_POSITION add column CANDIDATE_REQUIREMENTS_LANG5 text ^
alter table BASE_POSITION add column JOB_DESCRIPTION_LANG1 text ^
alter table BASE_POSITION add column JOB_DESCRIPTION_LANG2 text ^
alter table BASE_POSITION add column JOB_DESCRIPTION_LANG3 text ^
alter table BASE_POSITION add column JOB_DESCRIPTION_LANG4 text ^
alter table BASE_POSITION add column JOB_DESCRIPTION_LANG5 text ^
alter table BASE_POSITION add column BAZA varchar(255) ^
alter table BASE_POSITION add column EXTRA varchar(255) ^
-- alter table BASE_POSITION add column JOB_GROUP_ID uuid ^
-- update BASE_POSITION set JOB_GROUP_ID = <default_value> ^
-- alter table BASE_POSITION alter column JOB_GROUP_ID set not null ^
alter table BASE_POSITION add column JOB_GROUP_ID uuid not null ^
alter table BASE_POSITION add column GRADE_GROUP_ID uuid ^
alter table BASE_POSITION add column GROUP_ID uuid ^
alter table BASE_POSITION add column PAYROLL_ID uuid ^
alter table BASE_POSITION add column POSITION_STATUS_ID uuid ^
alter table BASE_POSITION add column GRADE_RULE_ID uuid ^
alter table BASE_POSITION add column ORGANIZATION_GROUP_EXT_ID uuid ^
alter table BASE_POSITION add column EMPLOYEE_CATEGORY_ID uuid ^
alter table BASE_POSITION add column DTYPE varchar(31) ^
update BASE_POSITION set DTYPE = 'base$PositionExt' where DTYPE is null ^
-- end BASE_POSITION
-- begin BASE_ORGANIZATION
alter table BASE_ORGANIZATION add column GROUP_ID uuid ^
alter table BASE_ORGANIZATION add column COST_CENTER_ID uuid ^
alter table BASE_ORGANIZATION add column PAYROLL_ID uuid ^
alter table BASE_ORGANIZATION add column INTERNAL boolean ^
alter table BASE_ORGANIZATION add column DTYPE varchar(31) ^
update BASE_ORGANIZATION set DTYPE = 'base$OrganizationExt' where DTYPE is null ^
-- end BASE_ORGANIZATION
-- begin BASE_PARTY
alter table BASE_PARTY add column TRAINING_PROVIDER boolean ^
update BASE_PARTY set TRAINING_PROVIDER = false where TRAINING_PROVIDER is null ^
alter table BASE_PARTY alter column TRAINING_PROVIDER set not null ^
alter table BASE_PARTY add column BIN varchar(255) ^
alter table BASE_PARTY add column RNN varchar(255) ^
alter table BASE_PARTY add column SIGNER varchar(255) ^
alter table BASE_PARTY add column JOB varchar(255) ^
alter table BASE_PARTY add column REASON varchar(255) ^
alter table BASE_PARTY add column DTYPE varchar(31) ^
update BASE_PARTY set DTYPE = 'base$PartyExt' where DTYPE is null ^
-- end BASE_PARTY
-- begin BASE_ORGANIZATION_GROUP
alter table BASE_ORGANIZATION_GROUP add column COMPANY_ID uuid ^
alter table BASE_ORGANIZATION_GROUP add column COST_CENTER_ID uuid ^
alter table BASE_ORGANIZATION_GROUP add column PAYROLL_ID uuid ^
alter table BASE_ORGANIZATION_GROUP add column IS_INTERNAL boolean ^
alter table BASE_ORGANIZATION_GROUP add column ORGANIZATION_NAME_LANG1 varchar(1000) ^
alter table BASE_ORGANIZATION_GROUP add column ORGANIZATION_NAME_LANG2 varchar(1000) ^
alter table BASE_ORGANIZATION_GROUP add column ORGANIZATION_NAME_LANG3 varchar(1000) ^
alter table BASE_ORGANIZATION_GROUP add column ORGANIZATION_NAME_LANG4 varchar(1000) ^
alter table BASE_ORGANIZATION_GROUP add column ORGANIZATION_NAME_LANG5 varchar(1000) ^
alter table BASE_ORGANIZATION_GROUP add column LOCATION_ID uuid ^
alter table BASE_ORGANIZATION_GROUP add column ORGANIZATION_TYPE_ID uuid ^
alter table BASE_ORGANIZATION_GROUP add column ANALYTICS_ID uuid ^
alter table BASE_ORGANIZATION_GROUP add column DTYPE varchar(31) ^
update BASE_ORGANIZATION_GROUP set DTYPE = 'base$OrganizationGroupExt' where DTYPE is null ^
-- end BASE_ORGANIZATION_GROUP
-- begin BASE_PERSON_GROUP
alter table BASE_PERSON_GROUP add column LINKEDIN_ACCESS_TOKEN varchar(1000) ^
alter table BASE_PERSON_GROUP add column LINKEDIN_PROFILE_LINK varchar(1000) ^
alter table BASE_PERSON_GROUP add column LINKEDIN_TOKEN_EXPIRES_DATE timestamp ^
alter table BASE_PERSON_GROUP add column COMPANY_ID uuid ^
alter table BASE_PERSON_GROUP add column DTYPE varchar(31) ^
update BASE_PERSON_GROUP set DTYPE = 'base$PersonGroupExt' where DTYPE is null ^
-- end BASE_PERSON_GROUP
-- begin BASE_ASSIGNMENT_GROUP
alter table BASE_ASSIGNMENT_GROUP add column ASSIGNMENT_NUMBER varchar(255) ^
update BASE_ASSIGNMENT_GROUP set ASSIGNMENT_NUMBER = '' where ASSIGNMENT_NUMBER is null ^
alter table BASE_ASSIGNMENT_GROUP alter column ASSIGNMENT_NUMBER set not null ^
alter table BASE_ASSIGNMENT_GROUP add column PERSON_GROUP_ID uuid ^
alter table BASE_ASSIGNMENT_GROUP add column JOB_GROUP_ID uuid ^
alter table BASE_ASSIGNMENT_GROUP add column GRADE_GROUP_ID uuid ^
alter table BASE_ASSIGNMENT_GROUP add column COMPANY_ID uuid ^
alter table BASE_ASSIGNMENT_GROUP add column ORGANIZATION_GROUP_ID uuid ^
alter table BASE_ASSIGNMENT_GROUP add column POSITION_GROUP_ID uuid ^
alter table BASE_ASSIGNMENT_GROUP add column ANALYTICS_ID uuid ^
alter table BASE_ASSIGNMENT_GROUP add column DTYPE varchar(100) ^
update BASE_ASSIGNMENT_GROUP set DTYPE = 'base$AssignmentGroupExt' where DTYPE is null ^
-- end BASE_ASSIGNMENT_GROUP
-- begin BASE_POSITION_GROUP
alter table BASE_POSITION_GROUP add column ANALYTICS_ID uuid ^
alter table BASE_POSITION_GROUP add column ADMIN_APPROVE_ID uuid ^
alter table BASE_POSITION_GROUP add column DTYPE varchar(31) ^
update BASE_POSITION_GROUP set DTYPE = 'base$PositionGroupExt' where DTYPE is null ^
-- end BASE_POSITION_GROUP

-- begin TSADV_ABSENCE_RVD_REQUEST
create table TSADV_ABSENCE_RVD_REQUEST (
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
    REQUEST_NUMBER bigint not null,
    STATUS_ID uuid not null,
    REQUEST_DATE date not null,
    COMMENT_ varchar(3000),
    --
    PERSON_GROUP_ID uuid,
    TYPE_ID uuid,
    PURPOSE_ID uuid,
    PURPOSE_TEXT varchar(2000),
    TIME_OF_STARTING timestamp,
    TIME_OF_FINISHING timestamp,
    TOTAL_HOURS integer,
    COMPENCATION boolean,
    VACATION_DAY boolean,
    ACQUAINTED boolean,
    AGREE boolean,
    --
    primary key (ID)
)^
-- end TSADV_ABSENCE_RVD_REQUEST

-- begin TSADV_ABSENCE_REQUEST_FILE_DESCRIPTOR_LINK
create table TSADV_ABSENCE_REQUEST_FILE_DESCRIPTOR_LINK (
    ABSENCE_REQUEST_ID uuid,
    FILE_DESCRIPTOR_ID uuid,
    primary key (ABSENCE_REQUEST_ID, FILE_DESCRIPTOR_ID)



-- begin TSADV_ABSENCE_REQUEST_FILE_DESCRIPTOR_LINK
create table TSADV_ABSENCE_REQUEST_FILE_DESCRIPTOR_LINK (
    ABSENCE_REQUEST_ID uuid,
    FILE_DESCRIPTOR_ID uuid,
    primary key (ABSENCE_REQUEST_ID, FILE_DESCRIPTOR_ID)
)^
-- end TSADV_ABSENCE_REQUEST_FILE_DESCRIPTOR_LINK
-- begin TSADV_DIC_PURPOSE_ABSENCE
create table TSADV_DIC_PURPOSE_ABSENCE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_PURPOSE_ABSENCE
-- begin TSADV_CHANGE_ABSENCE_DAYS_REQUEST
create table TSADV_CHANGE_ABSENCE_DAYS_REQUEST (
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
    REQUEST_NUMBER bigint not null,
    STATUS_ID uuid not null,
    REQUEST_DATE date not null,
    COMMENT_ varchar(3000),
    --
    REQUEST_TYPE_ID uuid,
    EMPLOYEE_ID uuid,
    VACATION_ID uuid,
    SCHEDULE_START_DATE date,
    SCHEDULE_END_DATE date,
    NEW_START_DATE date,
    NEW_END_DATE date,
    PERIOD_START_DATE date,
    PERIOD_END_DATE date,
    AGREE boolean not null,
    FAMILIARIZATION boolean not null,
    PURPOSE_TEXT text,
    PURPOSE_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_CHANGE_ABSENCE_DAYS_REQUEST
-- begin TSADV_CHANGE_ABSENCE_DAYS_REQUEST_FILE_DESCRIPTOR_LINK
create table TSADV_CHANGE_ABSENCE_DAYS_REQUEST_FILE_DESCRIPTOR_LINK (
    CHANGE_ABSENCE_DAYS_REQUEST_ID uuid,
    FILE_DESCRIPTOR_ID uuid,
    primary key (CHANGE_ABSENCE_DAYS_REQUEST_ID, FILE_DESCRIPTOR_ID)
)^
-- end TSADV_CHANGE_ABSENCE_DAYS_REQUEST_FILE_DESCRIPTOR_LINK
-- begin TSADV_POSITION_HARMFUL_CONDITION
create table TSADV_POSITION_HARMFUL_CONDITION (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    LEGACY_ID varchar(255),
    POSITION_GROUP_ID uuid not null,
    END_DATE date not null,
    DAYS integer not null,
    START_DATE date not null,
    --
    primary key (ID)
)^
-- end TSADV_POSITION_HARMFUL_CONDITION
-- begin TSADV_NEWS
create table TSADV_NEWS (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NEWS_LANG1 varchar(2000) not null,
    NEWS_LANG2 varchar(2000),
    NEWS_LANG3 varchar(2000),
    TITLE_LANG1 varchar(256) not null,
    TITLE_LANG2 varchar(256),
    TITLE_LANG3 varchar(256),
    IS_PUBLISHED boolean not null,
    BANNER_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_NEWS
-- begin TSADV_NEWS_COMMENT
create table TSADV_NEWS_COMMENT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NEWSID_ID uuid not null,
    COMMENT_LANG1 varchar(2000) not null,
    COMMENT_LANG2 varchar(2000),
    COMMENT_LANG3 varchar(2000),
    --
    primary key (ID)
)^
-- end TSADV_NEWS_COMMENT
-- begin TSADV_NUMBER_OF_VIEW
create table TSADV_NUMBER_OF_VIEW (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ENTITY_NAME varchar(255) not null,
    ENTITY_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_NUMBER_OF_VIEW
-- begin TSADV_NEWS_LIKE
create table TSADV_NEWS_LIKE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NEWS_ID_ID uuid not null,
    --
    primary key (ID)
)^
-- end TSADV_NEWS_LIKE
-- begin TSADV_ALL_ABSENCE_REQUEST
create table TSADV_ALL_ABSENCE_REQUEST (
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
    REQUEST_NUMBER bigint not null,
    STATUS_ID uuid not null,
    REQUEST_DATE date not null,
    COMMENT_ varchar(3000),
    --
    TYPE_ID uuid,
    PERSON_GROUP_ID uuid,
    ENTITY_NAME varchar(255),
    start_date date,
    end_date date,
    ABSENCE_DAYS integer,
    --
    primary key (ID)
)^
-- end TSADV_ALL_ABSENCE_REQUEST
-- begin TSADV_STUDENT_HOMEWORK
create table TSADV_STUDENT_HOMEWORK (
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
    HOMEWORK_ID uuid not null,
    PERSON_GROUP_ID uuid not null,
    ANSWER text,
    ANSWER_FILE_ID uuid,
    IS_DONE boolean not null,
    TRAINER_COMMENT text,
    TRAINER_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_STUDENT_HOMEWORK
-- begin TSADV_HOMEWORK
create table TSADV_HOMEWORK (
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
    COURSE_ID uuid not null,
    INSTRUCTIONS varchar(255) not null,
    INSTRUCTION_FILE_ID uuid,
    --
    primary key (ID)
)^
-- end TSADV_HOMEWORK
-- begin TSADV_DIC_LMS_SLIDER_POSITION
create table TSADV_DIC_LMS_SLIDER_POSITION (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_LMS_SLIDER_POSITION
-- begin TSADV_DIC_PORTAL_FEEDBACK_QUESTION
create table TSADV_DIC_PORTAL_FEEDBACK_QUESTION (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_PORTAL_FEEDBACK_QUESTION
-- begin TSADV_PORTAL_FEEDBACK_QUESTIONS
create table TSADV_PORTAL_FEEDBACK_QUESTIONS (
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
    USER_ID uuid not null,
    PORTAL_FEEDBACK_ID uuid not null,
    TOPIC varchar(255) not null,
    TEXT text not null,
    --
    primary key (ID)
)^
-- end TSADV_PORTAL_FEEDBACK_QUESTIONS
-- begin TSADV_PORTAL_FEEDBACK
create table TSADV_PORTAL_FEEDBACK (
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
    COMPANY_ID uuid not null,
    CATEGORY_ID uuid not null,
    EMAIL varchar(255) not null,
    --
    primary key (ID)
)^
-- end TSADV_PORTAL_FEEDBACK
-- begin TSADV_DIC_ASSESSMENT_EVENTS
create table TSADV_DIC_ASSESSMENT_EVENTS (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_ASSESSMENT_EVENTS
-- begin TSADV_DIC_ASSESSMENT_RESULT
create table TSADV_DIC_ASSESSMENT_RESULT (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_ASSESSMENT_RESULT
-- begin TSADV_DIC_ASSESSMENT_TYPE
create table TSADV_DIC_ASSESSMENT_TYPE (
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
    COMPANY_ID uuid not null,
    LANG_VALUE1 varchar(255) not null,
    DESCRIPTION1 varchar(2000),
    LANG_VALUE2 varchar(255),
    DESCRIPTION2 varchar(2000),
    LANG_VALUE3 varchar(255),
    DESCRIPTION3 varchar(2000),
    LANG_VALUE4 varchar(255),
    DESCRIPTION4 varchar(2000),
    LANG_VALUE5 varchar(255),
    DESCRIPTION5 varchar(2000),
    START_DATE date,
    END_DATE date,
    CODE varchar(255),
    IS_SYSTEM_RECORD boolean not null,
    ACTIVE boolean not null,
    IS_DEFAULT boolean not null,
    ORDER_ integer,
    --
    primary key (ID)
)^
-- end TSADV_DIC_ASSESSMENT_TYPE
-- begin TSADV_COURSE_PERSON_NOTE
create table TSADV_COURSE_PERSON_NOTE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    COURSE_ID uuid not null,
    PERSON_GROUP_ID uuid not null,
    NOTE varchar(2000) not null,
    --
    primary key (ID)
)^
-- end TSADV_COURSE_PERSON_NOTE
-- begin TSADV_COURSE_CERTIFICATE
create table TSADV_COURSE_CERTIFICATE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    CERTIFICATE_ID uuid not null,
    COURSE_ID uuid not null,
    START_DATE date not null,
    END_DATE date not null,
    --
    primary key (ID)
)^
-- end TSADV_COURSE_CERTIFICATE
