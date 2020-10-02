alter table TSADV_TALENT_PROGRAM add column QUESTION_OF_ESSAY_RU varchar(255) ^
update TSADV_TALENT_PROGRAM set QUESTION_OF_ESSAY_RU = '' where QUESTION_OF_ESSAY_RU is null ;
alter table TSADV_TALENT_PROGRAM alter column QUESTION_OF_ESSAY_RU set not null ;
alter table TSADV_TALENT_PROGRAM add column QUESTION_OF_ESSAY_KZ varchar(255) ^
update TSADV_TALENT_PROGRAM set QUESTION_OF_ESSAY_KZ = '' where QUESTION_OF_ESSAY_KZ is null ;
alter table TSADV_TALENT_PROGRAM alter column QUESTION_OF_ESSAY_KZ set not null ;
alter table TSADV_TALENT_PROGRAM add column QUESTION_OF_ESSAY_EN varchar(255) ^
update TSADV_TALENT_PROGRAM set QUESTION_OF_ESSAY_EN = '' where QUESTION_OF_ESSAY_EN is null ;
alter table TSADV_TALENT_PROGRAM alter column QUESTION_OF_ESSAY_EN set not null ;
