alter table TSADV_COURSE_SECTION_ATTEMPT rename column test_result_percent to test_result_percent__u25641 ;
alter table TSADV_COURSE_SECTION_ATTEMPT rename column test_result to test_result__u21086 ;
alter table TSADV_COURSE_SECTION_ATTEMPT add column TEST_RESULT decimal(19, 2) ;
alter table TSADV_COURSE_SECTION_ATTEMPT add column TEST_RESULT_PERCENT decimal(19, 2) ;
