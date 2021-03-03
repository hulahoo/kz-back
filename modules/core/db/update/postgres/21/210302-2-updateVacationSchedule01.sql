alter table TSADV_VACATION_SCHEDULE rename column comment_ to comment___u32703 ;
alter table TSADV_VACATION_SCHEDULE rename column balance to balance__u71187 ;
alter table TSADV_VACATION_SCHEDULE rename column is_send_to_oracle to is_send_to_oracle__u07997 ;
alter table TSADV_VACATION_SCHEDULE alter column is_send_to_oracle__u07997 drop not null ;
alter table TSADV_VACATION_SCHEDULE add column REQUEST_ID uuid ;
