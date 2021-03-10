-- update TSADV_NEWS_COMMENT set NEWSID_ID = <default_value> where NEWSID_ID is null ;
alter table TSADV_NEWS_COMMENT alter column NEWSID_ID set not null ;
update TSADV_NEWS_COMMENT set COMMENT_LANG1 = '' where COMMENT_LANG1 is null ;
alter table TSADV_NEWS_COMMENT alter column COMMENT_LANG1 set not null ;
