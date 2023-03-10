alter table TSADV_PERSONAL_PROTECTION_INSPECTOR add constraint FK_TSADV_PERSONAL_PROTECTION_INSPECTOR_EMPLOYEE foreign key (EMPLOYEE_ID) references BASE_PERSON_GROUP(ID);
alter table TSADV_PERSONAL_PROTECTION_INSPECTOR add constraint FK_TSADV_PERSONAL_PROTECTION_INSPECTOR_ORGANIZATION_GROUP foreign key (ORGANIZATION_GROUP_ID) references BASE_ORGANIZATION_GROUP(ID);
create index IDX_TSADV_PERSONAL_PROTECTION_INSPECTOR_EMPLOYEE on TSADV_PERSONAL_PROTECTION_INSPECTOR (EMPLOYEE_ID);
create index IDX_TSADV_PERSONAL_PROTECTION_INSPECTOR_ORGANIZATION_GROUP on TSADV_PERSONAL_PROTECTION_INSPECTOR (ORGANIZATION_GROUP_ID);
