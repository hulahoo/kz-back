alter table TSADV_PERSON_ANSWER add column ANSWERED boolean ^
update TSADV_PERSON_ANSWER set ANSWERED = false where ANSWERED is null ;
alter table TSADV_PERSON_ANSWER alter column ANSWERED set not null ;
