alter table TSADV_ABSENCE_RVD_REQUEST add constraint FK_TSADV_ABSENCE_RVD_REQUEST_SHIFT foreign key (SHIFT_ID) references TSADV_DIC_SHIFT(ID);
create index IDX_TSADV_ABSENCE_RVD_REQUEST_SHIFT on TSADV_ABSENCE_RVD_REQUEST (SHIFT_ID);