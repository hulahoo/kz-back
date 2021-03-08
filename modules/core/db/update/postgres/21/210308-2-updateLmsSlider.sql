alter table TSADV_LMS_SLIDER rename column url to url__u77622 ;
alter table TSADV_LMS_SLIDER rename column position_ to position___u44787 ;
alter table TSADV_LMS_SLIDER alter column position___u44787 drop not null ;
alter table TSADV_LMS_SLIDER add column POSITION_ID uuid ;
