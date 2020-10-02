update TSADV_RECOGNITION set COMMENT_ = '' where COMMENT_ is null ;
alter table TSADV_RECOGNITION alter column COMMENT_ set not null ;
-- update TSADV_RECOGNITION set AUTHOR_ID = <default_value> where AUTHOR_ID is null ;
alter table TSADV_RECOGNITION alter column AUTHOR_ID set not null ;
-- update TSADV_RECOGNITION set RECEIVER_ID = <default_value> where RECEIVER_ID is null ;
alter table TSADV_RECOGNITION alter column RECEIVER_ID set not null ;
