alter table TSADV_PERSON_POINT_LOG rename column date_ to date___u85018 ;
alter table TSADV_PERSON_POINT_LOG alter column date___u85018 drop not null ;
alter table TSADV_PERSON_POINT_LOG add column ACTION_TYPE varchar(50) ^
update TSADV_PERSON_POINT_LOG set ACTION_TYPE = 'RECEIVE_RECOGNITION' where ACTION_TYPE is null ;
alter table TSADV_PERSON_POINT_LOG alter column ACTION_TYPE set not null ;
alter table TSADV_PERSON_POINT_LOG add column OPERATION_TYPE varchar(50) ^
update TSADV_PERSON_POINT_LOG set OPERATION_TYPE = 'RECEIPT' where OPERATION_TYPE is null ;
alter table TSADV_PERSON_POINT_LOG alter column OPERATION_TYPE set not null ;
alter table TSADV_PERSON_POINT_LOG add column DATE_ timestamp ^
update TSADV_PERSON_POINT_LOG set DATE_ = current_timestamp where DATE_ is null ;
alter table TSADV_PERSON_POINT_LOG alter column DATE_ set not null ;
