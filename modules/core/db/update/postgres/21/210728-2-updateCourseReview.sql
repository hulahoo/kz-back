alter table TSADV_COURSE_REVIEW rename column from_feedback to from_feedback__u46441 ;
alter table TSADV_COURSE_REVIEW alter column from_feedback__u46441 drop not null ;
alter table TSADV_COURSE_REVIEW add column FROM_COURSE_FEEDBACK_PERSON_ANSWER_ID uuid ;
