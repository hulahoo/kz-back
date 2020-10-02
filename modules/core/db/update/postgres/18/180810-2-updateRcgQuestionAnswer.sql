alter table TSADV_RCG_QUESTION_ANSWER add column SCORE integer ^
update TSADV_RCG_QUESTION_ANSWER set SCORE = 0 where SCORE is null ;
alter table TSADV_RCG_QUESTION_ANSWER alter column SCORE set not null ;
