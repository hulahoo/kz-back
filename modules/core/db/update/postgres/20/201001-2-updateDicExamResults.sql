alter table TSADV_DIC_EXAM_RESULTS add column INTEGRATION_USER_LOGIN varchar(255) ;
alter table TSADV_DIC_EXAM_RESULTS add column IS_DEFAULT boolean ^
update TSADV_DIC_EXAM_RESULTS set IS_DEFAULT = false where IS_DEFAULT is null ;
alter table TSADV_DIC_EXAM_RESULTS alter column IS_DEFAULT set not null ;
alter table TSADV_DIC_EXAM_RESULTS add column ORGANIZATION_BIN varchar(255) ;
