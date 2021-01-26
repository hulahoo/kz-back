alter table TSADV_COURSE add column IS_ISSUED_CERTIFICATE boolean ^
update TSADV_COURSE set IS_ISSUED_CERTIFICATE = false where IS_ISSUED_CERTIFICATE is null ;
alter table TSADV_COURSE alter column IS_ISSUED_CERTIFICATE set not null ;
