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
    ORG_STRUCTURE_REQUEST_ID uuid,
    CHANGE_TYPE varchar(50) not null,
    CURRENT_ORGANIZATION_GROUP_ID uuid,
    CURRENT_POSITION_GROUP_ID uuid,
    CURRENT_GRADE_GROUP_ID uuid,
    CURRENT_HEAD_COUNT integer,
    CURRENT_BASE_SALARY decimal(19, 2),
    CURRENT_MONTHLY_PAYROLL decimal(19, 2),
    CURRENT_MONTHLY_TOTAL_PAYROLL decimal(19, 2),
    PARENT_ORGANIZATION_GROUP_ID uuid,
    PARENT_POSITION_GROUP_ID uuid,
    NEW_ORGANIZATION_ID uuid,
    NEW_POSITION_GROUP_ID uuid,
    NEW_GRADE_ID uuid,
    NEW_HEAD_COUNT decimal(19, 2),
    NEW_BASE_SALARY decimal(19, 2),
    NEW_MONTHLY_PAYROLL decimal(19, 2),
    NEW_MONTHLY_TOTAL_PAYROLL decimal(19, 2),
    DIFFERENCE_ORGANIZATION_GROUP_ID uuid,
    DIFFERENCE_POSITION_GROUP_ID uuid,
    DIFFERENCE_GRADE_GROUP_ID uuid,
    DIFFERENCE_HEAD_COUNT integer,
    DIFFERENCE_BASE_SALARY decimal(19, 2),
    DIFFERENCE_MONTHLY_PAYROLL decimal(19, 2),
    DIFFERENCE_MONTHLY_TOTAL_PAYROLL decimal(19, 2),
    --
    primary key (ID)
);