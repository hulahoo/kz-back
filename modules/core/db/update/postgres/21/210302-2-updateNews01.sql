update TSADV_NEWS set NEWS_LANG1 = '' where NEWS_LANG1 is null ;
alter table TSADV_NEWS alter column NEWS_LANG1 set not null ;
update TSADV_NEWS set TITLE_LANG1 = '' where TITLE_LANG1 is null ;
alter table TSADV_NEWS alter column TITLE_LANG1 set not null ;
update TSADV_NEWS set IS_PUBLISHED = false where IS_PUBLISHED is null ;
alter table TSADV_NEWS alter column IS_PUBLISHED set not null ;
-- update TSADV_NEWS set BANNER_ID = <default_value> where BANNER_ID is null ;
alter table TSADV_NEWS alter column BANNER_ID set not null ;
