--alter table TSADV_SUR_CHARGE rename column vacant_job_id to vacant_job_id__u72231 ;
--drop index IDX_TSADV_SUR_CHARGE_VACANT_JOB ;
--alter table TSADV_SUR_CHARGE drop constraint FK_TSADV_SUR_CHARGE_VACANT_JOB ;
--alter table TSADV_SUR_CHARGE rename column dtype to dtype__u43780 ;
--alter table TSADV_SUR_CHARGE rename column substitute_employee_id to substitute_employee_id__u11263 ;
--drop index IDX_TSADV_SUR_CHARGE_SUBSTITUTE_EMPLOYEE ;
--alter table TSADV_SUR_CHARGE drop constraint FK_TSADV_SUR_CHARGE_SUBSTITUTE_EMPLOYEE ;
--alter table TSADV_SUR_CHARGE rename column order_date to order_date__u57017 ;
--alter table TSADV_SUR_CHARGE rename column order_number to order_number__u35423 ;
alter table TSADV_SUR_CHARGE rename column value_ to value___u00326 ;
alter table TSADV_SUR_CHARGE alter column value___u00326 drop not null ;
alter table TSADV_SUR_CHARGE add column VALUE_ double precision ^
update TSADV_SUR_CHARGE set VALUE_ = 0 where VALUE_ is null ;
alter table TSADV_SUR_CHARGE alter column VALUE_ set not null ;
