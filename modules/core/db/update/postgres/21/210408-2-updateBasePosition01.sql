alter table BASE_POSITION add constraint FK_BASE_POSITION_FUNCTIONAL_MANAGER_POSITION_GROUP foreign key (FUNCTIONAL_MANAGER_POSITION_GROUP_ID) references BASE_POSITION_GROUP(ID);
create index IDX_BASE_POSITION_FUNCTIONAL_MANAGER_POSITION_GROUP on BASE_POSITION (FUNCTIONAL_MANAGER_POSITION_GROUP_ID);
