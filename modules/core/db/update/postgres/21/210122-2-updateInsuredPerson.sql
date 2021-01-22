alter table TSADV_INSURED_PERSON rename column address_id to address_id__u40813 ;
alter table TSADV_INSURED_PERSON drop constraint FK_TSADV_INSURED_PERSON_ADDRESS ;
drop index IDX_TSADV_INSURED_PERSON_ADDRESS ;
alter table TSADV_INSURED_PERSON rename column type to type__u76788 ;
alter table TSADV_INSURED_PERSON alter column type__u76788 drop not null ;
alter table TSADV_INSURED_PERSON rename column document_number to document_number__u47356 ;
alter table TSADV_INSURED_PERSON alter column document_number__u47356 drop not null ;
alter table TSADV_INSURED_PERSON add column ADDRESS_ID uuid ;
alter table TSADV_INSURED_PERSON add column DOCUMENT_NUMBER varchar(255) ;
alter table TSADV_INSURED_PERSON add column TYPE varchar(50) ^
update TSADV_INSURED_PERSON set TYPE = 'Employee' where TYPE is null ;
alter table TSADV_INSURED_PERSON alter column TYPE set not null ;
