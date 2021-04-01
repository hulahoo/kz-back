alter table TSADV_COURSE_SECTION_SCORM_RESULT rename column min_score to min_score__u12415 ;
alter table TSADV_COURSE_SECTION_SCORM_RESULT rename column max_score to max_score__u94632 ;
alter table TSADV_COURSE_SECTION_SCORM_RESULT rename column score to score__u47073 ;
alter table TSADV_COURSE_SECTION_SCORM_RESULT add column SCORE decimal(19, 2) ;
alter table TSADV_COURSE_SECTION_SCORM_RESULT add column MAX_SCORE decimal(19, 2) ;
alter table TSADV_COURSE_SECTION_SCORM_RESULT add column MIN_SCORE decimal(19, 2) ;
