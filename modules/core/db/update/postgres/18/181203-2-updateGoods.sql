alter table TSADV_GOODS rename column image_id to image_id__u36786 ;
drop index IDX_TSADV_GOODS_IMAGE ;
alter table TSADV_GOODS drop constraint FK_TSADV_GOODS_IMAGE ;
