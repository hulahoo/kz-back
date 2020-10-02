alter table TSADV_QUESTION_ANSWER rename column answer to answer__u50835 ;
alter table TSADV_QUESTION_ANSWER add column SCORE integer ;
alter table TSADV_QUESTION_ANSWER add column ANSWER_LANG1 varchar(2000) ;
alter table TSADV_QUESTION_ANSWER add column ANSWER_LANG2 varchar(2000) ;
alter table TSADV_QUESTION_ANSWER add column ANSWER_LANG3 varchar(2000) ;
alter table TSADV_QUESTION_ANSWER add column ANSWER_LANG4 varchar(2000) ;
alter table TSADV_QUESTION_ANSWER add column ANSWER_LANG5 varchar(2000) ;
