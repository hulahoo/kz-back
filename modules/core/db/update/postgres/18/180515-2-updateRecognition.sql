alter table TSADV_RECOGNITION rename column gived_by_person_id to gived_by_person_id__u31800 ;
drop index IDX_TSADV_RECOGNITION_GIVED_BY_PERSON ;
alter table TSADV_RECOGNITION drop constraint FK_TSADV_RECOGNITION_GIVED_BY_PERSON ;
alter table TSADV_RECOGNITION rename column recognized_person_id to recognized_person_id__u82466 ;
drop index IDX_TSADV_RECOGNITION_RECOGNIZED_PERSON ;
alter table TSADV_RECOGNITION drop constraint FK_TSADV_RECOGNITION_RECOGNIZED_PERSON ;
alter table TSADV_RECOGNITION add column AUTHOR_ID uuid ;
alter table TSADV_RECOGNITION add column RECEIVER_ID uuid ;
