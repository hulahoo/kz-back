update TSADV_QUESTION_ANSWER set ANSWER_LANG1 = '' where ANSWER_LANG1 is null ;
alter table TSADV_QUESTION_ANSWER alter column ANSWER_LANG1 set not null ;
