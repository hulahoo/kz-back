alter table TSADV_NEWS_LIKE add constraint FK_TSADV_NEWS_LIKE_NEWS_ID foreign key (NEWS_ID_ID) references TSADV_NEWS(ID);
create index IDX_TSADV_NEWS_LIKE_NEWS_ID on TSADV_NEWS_LIKE (NEWS_ID_ID);
