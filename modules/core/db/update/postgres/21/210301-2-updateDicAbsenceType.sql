alter table TSADV_DIC_ABSENCE_TYPE add column AVAILABLE_FOR_RECALL_ABSENCE boolean ^
update TSADV_DIC_ABSENCE_TYPE set AVAILABLE_FOR_RECALL_ABSENCE = false where AVAILABLE_FOR_RECALL_ABSENCE is null ;
alter table TSADV_DIC_ABSENCE_TYPE alter column AVAILABLE_FOR_RECALL_ABSENCE set not null ;
alter table TSADV_DIC_ABSENCE_TYPE add column AVAILABLE_FOR_LEAVING_VACATION boolean ^
update TSADV_DIC_ABSENCE_TYPE set AVAILABLE_FOR_LEAVING_VACATION = false where AVAILABLE_FOR_LEAVING_VACATION is null ;
alter table TSADV_DIC_ABSENCE_TYPE alter column AVAILABLE_FOR_LEAVING_VACATION set not null ;
alter table TSADV_DIC_ABSENCE_TYPE add column AVAILABLE_FOR_CHANGE_DATE boolean ^
update TSADV_DIC_ABSENCE_TYPE set AVAILABLE_FOR_CHANGE_DATE = false where AVAILABLE_FOR_CHANGE_DATE is null ;
alter table TSADV_DIC_ABSENCE_TYPE alter column AVAILABLE_FOR_CHANGE_DATE set not null ;
