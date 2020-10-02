alter table TSADV_PERSONAL_PROTECTION add column QUANTITY integer ^
update TSADV_PERSONAL_PROTECTION set QUANTITY = 0 where QUANTITY is null ;
alter table TSADV_PERSONAL_PROTECTION alter column QUANTITY set not null ;
alter table TSADV_PERSONAL_PROTECTION add column PLAN_CHANGE_DATE date ;
alter table TSADV_PERSONAL_PROTECTION add column CONDITION_ID uuid ;
alter table TSADV_PERSONAL_PROTECTION add column IS_ACCEPTED_BY_PERSON boolean ^
update TSADV_PERSONAL_PROTECTION set IS_ACCEPTED_BY_PERSON = false where IS_ACCEPTED_BY_PERSON is null ;
alter table TSADV_PERSONAL_PROTECTION alter column IS_ACCEPTED_BY_PERSON set not null ;
