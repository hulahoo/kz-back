alter table TSADV_COURSE_SESSION_ENROLLMENT rename column person_group_id to person_group_id__u93237 ;
alter table TSADV_COURSE_SESSION_ENROLLMENT alter column person_group_id__u93237 drop not null ;
drop index IDX_TSADV_COURSE_SESSION_ENROLLMENT_PERSON_GROUP ;
alter table TSADV_COURSE_SESSION_ENROLLMENT drop constraint FK_TSADV_COURSE_SESSION_ENROLLMENT_PERSON_GROUP ;
