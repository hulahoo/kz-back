update TSADV_PORTAL_FEEDBACK_QUESTIONS set TYPE_ID = (select id from TSADV_DIC_PORTAL_FEEDBACK_TYPE where code = 'QUESTION') ;
 alter table TSADV_PORTAL_FEEDBACK_QUESTIONS alter column TYPE_ID set not null ;
