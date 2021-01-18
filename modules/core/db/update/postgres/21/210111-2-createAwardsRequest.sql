alter table TSADV_AWARDS_REQUEST add constraint FK_TSADV_AWARDS_REQUEST_PERSON_GROUP foreign key (PERSON_GROUP_ID) references BASE_PERSON_GROUP(ID);
alter table TSADV_AWARDS_REQUEST add constraint FK_TSADV_AWARDS_REQUEST_PROMOTION_TYPE foreign key (PROMOTION_TYPE_ID) references TSADV_DIC_PROMOTION_TYPE(ID);
alter table TSADV_AWARDS_REQUEST add constraint FK_TSADV_AWARDS_REQUEST_AWARD_TYPE foreign key (AWARD_TYPE_ID) references TSADV_DIC_AWARD_TYPE(ID);
alter table TSADV_AWARDS_REQUEST add constraint FK_TSADV_AWARDS_REQUEST_ASSIGNMENT_GROUP foreign key (ASSIGNMENT_GROUP_ID) references BASE_ASSIGNMENT_GROUP(ID);
alter table TSADV_AWARDS_REQUEST add constraint FK_TSADV_AWARDS_REQUEST_REQUEST_STATUS foreign key (REQUEST_STATUS_ID) references TSADV_DIC_REQUEST_STATUS(ID);
create index IDX_TSADV_AWARDS_REQUEST_PERSON_GROUP on TSADV_AWARDS_REQUEST (PERSON_GROUP_ID);
create index IDX_TSADV_AWARDS_REQUEST_PROMOTION_TYPE on TSADV_AWARDS_REQUEST (PROMOTION_TYPE_ID);
create index IDX_TSADV_AWARDS_REQUEST_AWARD_TYPE on TSADV_AWARDS_REQUEST (AWARD_TYPE_ID);
create index IDX_TSADV_AWARDS_REQUEST_ASSIGNMENT_GROUP on TSADV_AWARDS_REQUEST (ASSIGNMENT_GROUP_ID);
create index IDX_TSADV_AWARDS_REQUEST_REQUEST_STATUS on TSADV_AWARDS_REQUEST (REQUEST_STATUS_ID);