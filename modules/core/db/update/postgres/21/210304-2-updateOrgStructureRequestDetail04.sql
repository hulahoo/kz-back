alter table TSADV_ORG_STRUCTURE_REQUEST_DETAIL add constraint FK_TSADV_ORGSTRUCTUREQUESTDETAIL_PARENT_ORGANIZATION_GROUP foreign key (PARENT_ORGANIZATION_GROUP_ID) references BASE_ORGANIZATION_GROUP(ID);
create index IDX_TSADV_ORGSTRUCTUREQUESTDETAIL_PARENT_ORGANIZATION_GROUP on TSADV_ORG_STRUCTURE_REQUEST_DETAIL (PARENT_ORGANIZATION_GROUP_ID);