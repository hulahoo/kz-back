alter table TSADV_INTERNSHIP add constraint FK_TSADV_INTERNSHIP_INTERNSHIP_RATING foreign key (INTERNSHIP_RATING_ID) references TSADV_DIC_INTERNSHIP_RATING(ID);
create index IDX_TSADV_INTERNSHIP_INTERNSHIP_RATING on TSADV_INTERNSHIP (INTERNSHIP_RATING_ID);
