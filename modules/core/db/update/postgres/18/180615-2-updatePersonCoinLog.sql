alter table TSADV_PERSON_COIN_LOG rename column coins to coins__u16508 ;
alter table TSADV_PERSON_COIN_LOG alter column coins__u16508 drop not null ;
alter table TSADV_PERSON_COIN_LOG add column COIN_TYPE varchar(50) ^
update TSADV_PERSON_COIN_LOG set COIN_TYPE = 'COIN' where COIN_TYPE is null ;
alter table TSADV_PERSON_COIN_LOG alter column COIN_TYPE set not null ;
alter table TSADV_PERSON_COIN_LOG add column QUANTITY bigint ^
update TSADV_PERSON_COIN_LOG set QUANTITY = 0 where QUANTITY is null ;
alter table TSADV_PERSON_COIN_LOG alter column QUANTITY set not null ;
alter table TSADV_PERSON_COIN_LOG add column COIN_DISTRIBUTION_RULE_ID uuid ;
