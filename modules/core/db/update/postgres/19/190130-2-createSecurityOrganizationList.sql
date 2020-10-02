alter table TSADV_SECURITY_ORGANIZATION_LIST add constraint FK_TSADV_SECURITY_ORGANIZATION_LIST_SECURITY_GROUP foreign key (SECURITY_GROUP_ID) references SEC_GROUP(ID);
alter table TSADV_SECURITY_ORGANIZATION_LIST add constraint FK_TSADV_SECURITY_ORGANIZATION_LIST_ORGANIZATION_GROUP foreign key (ORGANIZATION_GROUP_ID) references BASE_ORGANIZATION_GROUP(ID);
create index IDX_TSADV_SECURITY_ORGANIZATION_LIST_SECURITY_GROUP on TSADV_SECURITY_ORGANIZATION_LIST (SECURITY_GROUP_ID);
create index IDX_TSADV_SECURITY_ORGANIZATION_LIST_ORGANIZATION_GROUP on TSADV_SECURITY_ORGANIZATION_LIST (ORGANIZATION_GROUP_ID);
