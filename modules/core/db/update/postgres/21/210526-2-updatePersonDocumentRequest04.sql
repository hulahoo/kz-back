-- update TSADV_PERSON_DOCUMENT_REQUEST set STATUS_ID = <default_value> where STATUS_ID is null ;
alter table TSADV_PERSON_DOCUMENT_REQUEST alter column STATUS_ID set not null ;
