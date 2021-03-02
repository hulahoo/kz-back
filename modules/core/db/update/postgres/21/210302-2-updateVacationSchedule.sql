alter table TSADV_VACATION_SCHEDULE add column IS_SEND_TO_ORACLE boolean ^
update TSADV_VACATION_SCHEDULE set IS_SEND_TO_ORACLE = false where IS_SEND_TO_ORACLE is null ;
alter table TSADV_VACATION_SCHEDULE alter column IS_SEND_TO_ORACLE set not null ;
alter table TSADV_VACATION_SCHEDULE add column BALANCE integer ;
alter table TSADV_VACATION_SCHEDULE add column COMMENT_ text ;
