alter table TSADV_ORG_STRUCTURE_REQUEST_DETAIL rename column change_type to change_type__u25395 ;
alter table TSADV_ORG_STRUCTURE_REQUEST_DETAIL alter column change_type__u25395 drop not null ;
-- alter table TSADV_ORG_STRUCTURE_REQUEST_DETAIL add column CHANGE_TYPE_ID uuid ^
-- update TSADV_ORG_STRUCTURE_REQUEST_DETAIL set CHANGE_TYPE_ID = <default_value> ;
-- alter table TSADV_ORG_STRUCTURE_REQUEST_DETAIL alter column CHANGE_TYPE_ID set not null ;
alter table TSADV_ORG_STRUCTURE_REQUEST_DETAIL add column CHANGE_TYPE_ID uuid not null ;
