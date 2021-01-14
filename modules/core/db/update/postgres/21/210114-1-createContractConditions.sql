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
    AGE_MAX varchar(255) not null,
    IS_FREE boolean not null,
    COST_IN_KZT double precision not null,
    --
    primary key (ID)
);