-- update TSADV_PORTAL_FEEDBACK_QUESTIONS set USER_ID = <default_value> where USER_ID is null ;
alter table TSADV_PORTAL_FEEDBACK_QUESTIONS alter column USER_ID set not null ;
-- update TSADV_PORTAL_FEEDBACK_QUESTIONS set PORTAL_FEEDBACK_ID = <default_value> where PORTAL_FEEDBACK_ID is null ;
alter table TSADV_PORTAL_FEEDBACK_QUESTIONS alter column PORTAL_FEEDBACK_ID set not null ;
update TSADV_PORTAL_FEEDBACK_QUESTIONS set TOPIC = '' where TOPIC is null ;
alter table TSADV_PORTAL_FEEDBACK_QUESTIONS alter column TOPIC set not null ;
update TSADV_PORTAL_FEEDBACK_QUESTIONS set TEXT = '' where TEXT is null ;
alter table TSADV_PORTAL_FEEDBACK_QUESTIONS alter column TEXT set not null ;
