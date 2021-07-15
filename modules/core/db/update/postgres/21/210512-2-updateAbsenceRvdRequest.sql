alter table TSADV_ABSENCE_RVD_REQUEST rename column compencation to COMPENSATION ;
update TSADV_ABSENCE_RVD_REQUEST set COMPENSATION = false where COMPENSATION is null ;
alter table TSADV_ABSENCE_RVD_REQUEST alter column COMPENSATION set not null ;
