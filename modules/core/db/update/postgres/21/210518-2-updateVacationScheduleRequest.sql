alter table TSADV_VACATION_SCHEDULE_REQUEST rename column sent_to_oracle to sent_to_oracle__u21426 ;
alter table TSADV_VACATION_SCHEDULE_REQUEST alter column sent_to_oracle__u21426 drop not null ;
alter table TSADV_VACATION_SCHEDULE_REQUEST add column SENT_TO_ORACLE varchar(50) ;
