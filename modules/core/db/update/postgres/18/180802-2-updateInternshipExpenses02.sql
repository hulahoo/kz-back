alter table TSADV_INTERNSHIP_EXPENSES rename column trainee_id to INTERNSHIP_ID ;
drop index IDX_TSADV_INTERNSHIP_EXPENSES_TRAINEE ;
alter table TSADV_INTERNSHIP_EXPENSES drop constraint FK_TSADV_INTERNSHIP_EXPENSES_TRAINEE ;
-- alter table TSADV_INTERNSHIP_EXPENSES add column INTERNSHIP_ID uuid ^
-- update TSADV_INTERNSHIP_EXPENSES set INTERNSHIP_ID = <default_value> ;
-- alter table TSADV_INTERNSHIP_EXPENSES alter column INTERNSHIP_ID set not null ;
