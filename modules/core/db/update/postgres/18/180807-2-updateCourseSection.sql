alter table TSADV_COURSE_SECTION add column MANDATORY boolean ^
update TSADV_COURSE_SECTION set MANDATORY = false where MANDATORY is null ;
alter table TSADV_COURSE_SECTION alter column MANDATORY set not null ;
