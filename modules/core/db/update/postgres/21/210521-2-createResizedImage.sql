alter table TSADV_RESIZED_IMAGE add constraint FK_TSADV_RESIZED_IMAGE_SIZE foreign key (SIZE_ID) references TSADV_IMAGE_SIZE(ID);
alter table TSADV_RESIZED_IMAGE add constraint FK_TSADV_RESIZED_IMAGE_ORIGINAL_IMAGE foreign key (ORIGINAL_IMAGE_ID) references SYS_FILE(ID);
alter table TSADV_RESIZED_IMAGE add constraint FK_TSADV_RESIZED_IMAGE_RESIZED_IMAGE foreign key (RESIZED_IMAGE_ID) references SYS_FILE(ID);
create index IDX_TSADV_RESIZED_IMAGE_SIZE on TSADV_RESIZED_IMAGE (SIZE_ID);
create index IDX_TSADV_RESIZED_IMAGE_ORIGINAL_IMAGE on TSADV_RESIZED_IMAGE (ORIGINAL_IMAGE_ID);
