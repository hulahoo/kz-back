alter table TSADV_ORGANIZATION_INCENTIVE_MONTH_RESULT add constraint FK_TSADV_ORGANIZATION_INCENTIVE_MONTH_RESULT_COMPANY foreign key (COMPANY_ID) references BASE_ORGANIZATION_GROUP(ID);
alter table TSADV_ORGANIZATION_INCENTIVE_MONTH_RESULT add constraint FK_TSADV_ORGANIZATION_INCENTIVE_MONTH_RESULT_DEPARTMENT foreign key (DEPARTMENT_ID) references BASE_ORGANIZATION_GROUP(ID);
alter table TSADV_ORGANIZATION_INCENTIVE_MONTH_RESULT add constraint FK_TSADV_ORGANIZATION_INCENTIVE_MONTH_RESULT_INDICATOR foreign key (INDICATOR_ID) references TSADV_ORGANIZATION_INCENTIVE_INDICATORS(ID);
alter table TSADV_ORGANIZATION_INCENTIVE_MONTH_RESULT add constraint FK_TSADV_ORGANIZATION_INCENTIVE_MONTH_RESULT_STATUS foreign key (STATUS_ID) references TSADV_DIC_INCENTIVE_RESULT_STATUS(ID);
create index IDX_TSADV_ORGANIZATION_INCENTIVE_MONTH_RESULT_COMPANY on TSADV_ORGANIZATION_INCENTIVE_MONTH_RESULT (COMPANY_ID);
create index IDX_TSADV_ORGANIZATION_INCENTIVE_MONTH_RESULT_DEPARTMENT on TSADV_ORGANIZATION_INCENTIVE_MONTH_RESULT (DEPARTMENT_ID);
create index IDX_TSADV_ORGANIZATION_INCENTIVE_MONTH_RESULT_INDICATOR on TSADV_ORGANIZATION_INCENTIVE_MONTH_RESULT (INDICATOR_ID);
create index IDX_TSADV_ORGANIZATION_INCENTIVE_MONTH_RESULT_STATUS on TSADV_ORGANIZATION_INCENTIVE_MONTH_RESULT (STATUS_ID);
