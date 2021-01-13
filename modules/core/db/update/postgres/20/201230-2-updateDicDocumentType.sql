alter table TSADV_DIC_DOCUMENT_TYPE add column FOREIGNER Boolean default false ^
update TSADV_DIC_DOCUMENT_TYPE set FOREIGNER = false where FOREIGNER is null ;
alter table TSADV_DIC_DOCUMENT_TYPE alter column FOREIGNER set not null ;
