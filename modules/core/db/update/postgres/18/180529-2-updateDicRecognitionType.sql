alter table TSADV_DIC_RECOGNITION_TYPE rename column coins to coins__u16293 ;
alter table TSADV_DIC_RECOGNITION_TYPE add column COINS bigint ^
update TSADV_DIC_RECOGNITION_TYPE set COINS = 0 where COINS is null ;
alter table TSADV_DIC_RECOGNITION_TYPE alter column COINS set not null ;
