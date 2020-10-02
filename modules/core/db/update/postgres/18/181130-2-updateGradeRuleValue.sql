alter table TSADV_GRADE_RULE_VALUE rename column max_ to max___u19629 ;
alter table TSADV_GRADE_RULE_VALUE rename column mid to mid__u44750 ;
alter table TSADV_GRADE_RULE_VALUE rename column min_ to min___u30474 ;
alter table TSADV_GRADE_RULE_VALUE rename column value_ to value___u69559 ;
alter table TSADV_GRADE_RULE_VALUE add column VALUE_ double precision ;
alter table TSADV_GRADE_RULE_VALUE add column MIN_ double precision ;
alter table TSADV_GRADE_RULE_VALUE add column MID double precision ;
alter table TSADV_GRADE_RULE_VALUE add column MAX_ double precision ;
