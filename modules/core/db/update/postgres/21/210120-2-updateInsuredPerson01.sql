alter table TSADV_INSURED_PERSON rename column address_id to address_id__u18613 ;
alter table TSADV_INSURED_PERSON alter column address_id__u18613 drop not null ;
alter table TSADV_INSURED_PERSON drop constraint FK_TSADV_INSURED_PERSON_ADDRESS ;
drop index IDX_TSADV_INSURED_PERSON_ADDRESS ;
-- alter table TSADV_INSURED_PERSON add column ADDRESS_ID uuid ^
-- update TSADV_INSURED_PERSON set ADDRESS_ID = <default_value> ;
-- alter table TSADV_INSURED_PERSON alter column address_id set not null ;
alter table TSADV_INSURED_PERSON add column ADDRESS_ID uuid not null ;
alter table TSADV_INSURED_PERSON add column ADDRESS varchar(255) ^
update TSADV_INSURED_PERSON set ADDRESS = '' where ADDRESS is null ;
alter table TSADV_INSURED_PERSON alter column ADDRESS set not null ;
