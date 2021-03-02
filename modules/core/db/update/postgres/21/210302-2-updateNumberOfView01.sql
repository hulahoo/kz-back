update TSADV_NUMBER_OF_VIEW set ENTITY_NAME = '' where ENTITY_NAME is null ;
alter table TSADV_NUMBER_OF_VIEW alter column ENTITY_NAME set not null ;
update TSADV_NUMBER_OF_VIEW set ENTITY_ID = newid() where ENTITY_ID is null ;
alter table TSADV_NUMBER_OF_VIEW alter column ENTITY_ID set not null ;
