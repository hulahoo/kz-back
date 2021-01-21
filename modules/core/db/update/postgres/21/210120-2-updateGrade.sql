alter table TSADV_GRADE rename column company_id to company_id__u81198 ;
alter table TSADV_GRADE drop constraint FK_TSADV_GRADE_COMPANY ;
drop index IDX_TSADV_GRADE_COMPANY ;
