alter table TSADV_DIC_RC_QUESTION_CATEGORY add column INTEGRATION_USER_LOGIN varchar(255) ;
alter table TSADV_DIC_RC_QUESTION_CATEGORY add column IS_DEFAULT boolean ^
update TSADV_DIC_RC_QUESTION_CATEGORY set IS_DEFAULT = false where IS_DEFAULT is null ;
alter table TSADV_DIC_RC_QUESTION_CATEGORY alter column IS_DEFAULT set not null ;
alter table TSADV_DIC_RC_QUESTION_CATEGORY add column ORGANIZATION_BIN varchar(255) ;
