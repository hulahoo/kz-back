alter table TSADV_ORG_STRUCTURE_REQUEST rename column comment_ to comment___u07316 ;
alter table TSADV_ORG_STRUCTURE_REQUEST rename column request_status_id to request_status_id__u01243 ;
alter table TSADV_ORG_STRUCTURE_REQUEST alter column request_status_id__u01243 drop not null ;
alter table TSADV_ORG_STRUCTURE_REQUEST drop constraint FK_TSADV_ORG_STRUCTURE_REQUEST_REQUEST_STATUS ;
drop index IDX_TSADV_ORG_STRUCTURE_REQUEST_REQUEST_STATUS ;
alter table TSADV_ORG_STRUCTURE_REQUEST add column STATUS_ID uuid ^;
update TSADV_ORG_STRUCTURE_REQUEST set STATUS_ID =
 (select f.id from
( (select tdrs.id from tsadv_dic_request_status tdrs
 where tdrs.code = 'DRAFT')
 union all
 (select tdrs2.id from tsadv_dic_request_status tdrs2
 order by tdrs2.code limit 1)) f
limit 1);
 alter table TSADV_ORG_STRUCTURE_REQUEST alter column STATUS_ID set not null ;
--alter table TSADV_ORG_STRUCTURE_REQUEST add column STATUS_ID uuid not null ;
alter table TSADV_ORG_STRUCTURE_REQUEST add column LEGACY_ID varchar(255) ;
alter table TSADV_ORG_STRUCTURE_REQUEST add column INTEGRATION_USER_LOGIN varchar(255) ;
alter table TSADV_ORG_STRUCTURE_REQUEST add column ORGANIZATION_BIN varchar(255) ;
alter table TSADV_ORG_STRUCTURE_REQUEST add column COMMENT_ varchar(3000) ;
