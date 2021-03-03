-- update TSADV_NEWS_LIKE set NEWS_ID_ID = <default_value> where NEWS_ID_ID is null ;
alter table TSADV_NEWS_LIKE alter column NEWS_ID_ID set not null ;
