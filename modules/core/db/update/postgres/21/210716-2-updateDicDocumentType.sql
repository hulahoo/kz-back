alter table TSADV_DIC_DOCUMENT_TYPE add column IS_ID_OR_PASSPORT boolean ^
update TSADV_DIC_DOCUMENT_TYPE set IS_ID_OR_PASSPORT = false where IS_ID_OR_PASSPORT is null ;
alter table TSADV_DIC_DOCUMENT_TYPE alter column IS_ID_OR_PASSPORT set not null ;
