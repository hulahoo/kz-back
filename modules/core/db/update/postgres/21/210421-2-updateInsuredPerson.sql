alter table TSADV_INSURED_PERSON rename column address_id to address_id__u75745 ;
alter table TSADV_INSURED_PERSON drop constraint FK_TSADV_INSURED_PERSON_ADDRESS ;
drop index IDX_TSADV_INSURED_PERSON_ADDRESS ;
alter table TSADV_INSURED_PERSON add column ADDRESS_TYPE_ID uuid ;
