alter table TSADV_DIC_TRAINING_METHOD add column INTEGRATION_USER_LOGIN varchar(255) ;
alter table TSADV_DIC_TRAINING_METHOD add column IS_DEFAULT boolean ^
update TSADV_DIC_TRAINING_METHOD set IS_DEFAULT = false where IS_DEFAULT is null ;
alter table TSADV_DIC_TRAINING_METHOD alter column IS_DEFAULT set not null ;
alter table TSADV_DIC_TRAINING_METHOD add column ORGANIZATION_BIN varchar(255) ;
