alter table TSADV_PERFORMANCE_PLAN add column PERFORMANCE_PLAN_NAME_EN varchar(255) ;
alter table TSADV_PERFORMANCE_PLAN add column PERFORMANCE_PLAN_NAME_KZ varchar(255) ;
alter table TSADV_PERFORMANCE_PLAN alter column ADMINISTRATOR_PERSON_GROUP_ID drop not null ;