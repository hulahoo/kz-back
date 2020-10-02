alter table TSADV_PERSONAL_DATA_REQUEST rename column person_id to person_id__u85310 ;
alter table TSADV_PERSONAL_DATA_REQUEST drop constraint FK_TSADV_PERSONAL_DATA_REQUEST_PERSON ;
alter table TSADV_PERSONAL_DATA_REQUEST add column PERSON_GROUP_ID uuid ;
