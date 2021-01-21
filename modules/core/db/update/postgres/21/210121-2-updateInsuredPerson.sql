alter table TSADV_INSURED_PERSON rename column address_id to address_id__u13041 ;
alter table TSADV_INSURED_PERSON alter column address_id__u13041 drop not null ;
alter table TSADV_INSURED_PERSON drop constraint FK_TSADV_INSURED_PERSON_ADDRESS ;
drop index IDX_TSADV_INSURED_PERSON_ADDRESS ;
alter table TSADV_INSURED_PERSON add column ADDRESS_ID uuid ;
alter table TSADV_INSURED_PERSON alter column ADDRESS_ID drop not null ;
alter table TSADV_INSURED_PERSON alter column ADDRESS drop not null ;
