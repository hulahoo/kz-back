alter table TSADV_CALENDAR_HOLIDAY add column ACTION_DATE_FROM date ^
update TSADV_CALENDAR_HOLIDAY set ACTION_DATE_FROM = current_date where ACTION_DATE_FROM is null ;
alter table TSADV_CALENDAR_HOLIDAY alter column ACTION_DATE_FROM set not null ;
alter table TSADV_CALENDAR_HOLIDAY add column ACTION_DATE_TO date ^
update TSADV_CALENDAR_HOLIDAY set ACTION_DATE_TO = current_date where ACTION_DATE_TO is null ;
alter table TSADV_CALENDAR_HOLIDAY alter column ACTION_DATE_TO set not null ;