-- update TSADV_COURSE_SECTION_SESSION set COURSE_SECTION_ID = <default_value> where COURSE_SECTION_ID is null ;
alter table TSADV_COURSE_SECTION_SESSION alter column COURSE_SECTION_ID set not null ;
