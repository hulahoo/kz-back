alter table TSADV_NEWS_COMMENT add constraint FK_TSADV_NEWS_COMMENT_NEWSID foreign key (NEWSID_ID) references TSADV_NEWS(ID);
create index IDX_TSADV_NEWS_COMMENT_NEWSID on TSADV_NEWS_COMMENT (NEWSID_ID);
