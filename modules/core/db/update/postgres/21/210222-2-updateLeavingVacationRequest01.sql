alter table TSADV_LEAVING_VACATION_REQUEST add constraint FK_TSADV_LEAVING_VACATION_REQUEST_STATUS foreign key (STATUS_ID) references TSADV_DIC_REQUEST_STATUS(ID);
create index IDX_TSADV_LEAVING_VACATION_REQUEST_STATUS on TSADV_LEAVING_VACATION_REQUEST (STATUS_ID);
