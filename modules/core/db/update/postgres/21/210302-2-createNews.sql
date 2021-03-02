alter table TSADV_NEWS add constraint FK_TSADV_NEWS_BANNER foreign key (BANNER_ID) references SYS_FILE(ID);
create index IDX_TSADV_NEWS_BANNER on TSADV_NEWS (BANNER_ID);
