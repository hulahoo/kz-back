alter table TSADV_EXECUTIVE_ASSISTANTS add constraint FK_TSADV_EXECUTIVE_ASSISTANTS_MANAGER_POSITION_GROUP foreign key (MANAGER_POSITION_GROUP_ID) references BASE_POSITION_GROUP(ID);
alter table TSADV_EXECUTIVE_ASSISTANTS add constraint FK_TSADV_EXECUTIVE_ASSISTANTS_ASSISTANCE_POSITION_GROUP foreign key (ASSISTANCE_POSITION_GROUP_ID) references BASE_POSITION_GROUP(ID);
create index IDX_TSADV_EXECUTIVE_ASSISTANTS_MANAGER_POSITION_GROUP on TSADV_EXECUTIVE_ASSISTANTS (MANAGER_POSITION_GROUP_ID);
create index IDX_TSADV_EXECUTIVE_ASSISTANTS_ASSISTANCE_POSITION_GROUP on TSADV_EXECUTIVE_ASSISTANTS (ASSISTANCE_POSITION_GROUP_ID);