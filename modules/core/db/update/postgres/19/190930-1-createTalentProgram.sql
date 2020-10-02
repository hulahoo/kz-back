create table if not exists TSADV_TALENT_PROGRAM (
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
    --
    primary key (ID)
);
