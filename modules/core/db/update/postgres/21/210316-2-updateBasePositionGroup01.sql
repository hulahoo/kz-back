alter table BASE_POSITION_GROUP add constraint FK_BASE_POSITION_GROUP_GRADE_GROUP foreign key (GRADE_GROUP_ID) references TSADV_GRADE_GROUP(ID);
create index IDX_BASE_POSITION_GROUP_GRADE_GROUP on BASE_POSITION_GROUP (GRADE_GROUP_ID);
