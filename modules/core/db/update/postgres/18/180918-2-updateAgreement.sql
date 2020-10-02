alter table TSADV_AGREEMENT rename column category_id to category_id__u21816 ;
drop index IDX_TSADV_AGREEMENT_CATEGORY ;
alter table TSADV_AGREEMENT drop constraint FK_TSADV_AGREEMENT_CATEGORY ;
