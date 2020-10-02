alter table TSADV_PERSON_DOCUMENT add column SERIES varchar(255) ;
alter table TSADV_PERSON_DOCUMENT alter column ISSUED_BY drop not null ;
update TSADV_PERSON_DOCUMENT set DOCUMENT_NUMBER = '' where DOCUMENT_NUMBER is null ;
alter table TSADV_PERSON_DOCUMENT alter column DOCUMENT_NUMBER set not null ;
