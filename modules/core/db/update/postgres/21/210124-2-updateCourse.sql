alter table TSADV_COURSE add column IS_ONLINE boolean ^
update TSADV_COURSE set IS_ONLINE = false where IS_ONLINE is null ;
alter table TSADV_COURSE alter column IS_ONLINE set not null ;
