alter table BASE_ORGANIZATION_GROUP add constraint FK_BASE_ORGANIZATION_GROUP_LOCATION foreign key (LOCATION_ID) references BASE_DIC_LOCATION(ID);
create index IDX_BASE_ORGANIZATION_GROUP_LOCATION on BASE_ORGANIZATION_GROUP (LOCATION_ID);
