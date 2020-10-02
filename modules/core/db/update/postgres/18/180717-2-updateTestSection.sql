alter table TSADV_TEST_SECTION add column DYNAMIC_LOAD boolean ^
update TSADV_TEST_SECTION set DYNAMIC_LOAD = false where DYNAMIC_LOAD is null ;
alter table TSADV_TEST_SECTION alter column DYNAMIC_LOAD set not null ;
alter table TSADV_TEST_SECTION add column GENERATE_COUNT integer ;
