alter table TSADV_ASSIGNED_GOAL add column CATEGORY_ID uuid ;
alter table TSADV_ASSIGNED_GOAL alter column ASSIGNED_BY_PERSON_GROUP_ID drop not null ;
