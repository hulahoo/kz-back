alter table TSADV_PERSON_ANSWER add column CORRECT boolean ^
update TSADV_PERSON_ANSWER set CORRECT = false where CORRECT is null ;
alter table TSADV_PERSON_ANSWER alter column CORRECT set not null ;
alter table TSADV_PERSON_ANSWER add column SCORE integer ^
update TSADV_PERSON_ANSWER set SCORE = 0 where SCORE is null ;
alter table TSADV_PERSON_ANSWER alter column SCORE set not null ;
