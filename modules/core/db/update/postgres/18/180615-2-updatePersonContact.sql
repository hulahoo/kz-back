alter table TSADV_PERSON_CONTACT add column END_DATE date ^
update TSADV_PERSON_CONTACT set END_DATE = current_date where END_DATE is null ;
alter table TSADV_PERSON_CONTACT alter column END_DATE set not null ;
alter table TSADV_PERSON_CONTACT add column START_DATE date ^
update TSADV_PERSON_CONTACT set START_DATE = current_date where START_DATE is null ;
alter table TSADV_PERSON_CONTACT alter column START_DATE set not null ;
