alter table BASE_ORGANIZATION_GROUP add constraint FK_BASE_ORGANIZATION_GROUP_COST_CENTER foreign key (COST_CENTER_ID) references TSADV_DIC_COST_CENTER(ID);
create index IDX_BASE_ORGANIZATION_GROUP_COST_CENTER on BASE_ORGANIZATION_GROUP (COST_CENTER_ID);
