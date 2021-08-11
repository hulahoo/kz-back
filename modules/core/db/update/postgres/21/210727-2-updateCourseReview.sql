alter table TSADV_COURSE_REVIEW add column FROM_FEEDBACK boolean ^
update TSADV_COURSE_REVIEW set FROM_FEEDBACK = false where FROM_FEEDBACK is null ;
alter table TSADV_COURSE_REVIEW alter column FROM_FEEDBACK set not null ;
