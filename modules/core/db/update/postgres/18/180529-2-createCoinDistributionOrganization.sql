alter table TSADV_COIN_DISTRIBUTION_ORGANIZATION add constraint FK_TSADV_COINDISTRIBUORGANIZA_PARENT_ORGANIZATION_GROUP foreign key (PARENT_ORGANIZATION_GROUP_ID) references BASE_ORGANIZATION_GROUP(ID);
alter table TSADV_COIN_DISTRIBUTION_ORGANIZATION add constraint FK_TSADV_COIN_DISTRIBUTION_ORGANIZATION_HIERARCHY foreign key (HIERARCHY_ID) references BASE_HIERARCHY(ID);
alter table TSADV_COIN_DISTRIBUTION_ORGANIZATION add constraint FK_TSADV_COIN_DISTRIBUTION_ORGANIZATION_ORGANIZATION_GROUP foreign key (ORGANIZATION_GROUP_ID) references BASE_ORGANIZATION_GROUP(ID);
alter table TSADV_COIN_DISTRIBUTION_ORGANIZATION add constraint FK_TSADV_COIN_DISTRIBUTION_ORGANIZATION_COIN_DISTRIBUTION_RULE foreign key (COIN_DISTRIBUTION_RULE_ID) references TSADV_COIN_DISTRIBUTION_RULE(ID);
create index IDX_TSADV_COINDISTRIBUORGANIZA_PARENT_ORGANIZATION_GROUP on TSADV_COIN_DISTRIBUTION_ORGANIZATION (PARENT_ORGANIZATION_GROUP_ID);
create index IDX_TSADV_COIN_DISTRIBUTION_ORGANIZATION_HIERARCHY on TSADV_COIN_DISTRIBUTION_ORGANIZATION (HIERARCHY_ID);
create index IDX_TSADV_COIN_DISTRIBUTION_ORGANIZATION_ORGANIZATION_GROUP on TSADV_COIN_DISTRIBUTION_ORGANIZATION (ORGANIZATION_GROUP_ID);
create index IDX_TSADV_COINDISTRIBUORGANIZA_COIN_DISTRIBUTION_RULE on TSADV_COIN_DISTRIBUTION_ORGANIZATION (COIN_DISTRIBUTION_RULE_ID);
