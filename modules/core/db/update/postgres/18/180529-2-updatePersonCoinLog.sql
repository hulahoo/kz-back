alter table TSADV_PERSON_COIN_LOG rename column date_ to date___u25757 ;
alter table TSADV_PERSON_COIN_LOG alter column date___u25757 drop not null ;
alter table TSADV_PERSON_COIN_LOG add column ACTION_TYPE varchar(50) ^
update TSADV_PERSON_COIN_LOG set ACTION_TYPE = 'RECEIVE_RECOGNITION' where ACTION_TYPE is null ;
alter table TSADV_PERSON_COIN_LOG alter column ACTION_TYPE set not null ;
alter table TSADV_PERSON_COIN_LOG add column OPERATION_TYPE varchar(50) ^
update TSADV_PERSON_COIN_LOG set OPERATION_TYPE = 'RECEIPT' where OPERATION_TYPE is null ;
alter table TSADV_PERSON_COIN_LOG alter column OPERATION_TYPE set not null ;
alter table TSADV_PERSON_COIN_LOG add column DATE_ timestamp ^
update TSADV_PERSON_COIN_LOG set DATE_ = current_timestamp where DATE_ is null ;
alter table TSADV_PERSON_COIN_LOG alter column DATE_ set not null ;
