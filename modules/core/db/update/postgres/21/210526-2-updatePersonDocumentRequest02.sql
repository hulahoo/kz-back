alter table TSADV_PERSON_DOCUMENT_REQUEST add constraint FK_TSADV_PERSON_DOCUMENT_REQUEST_APPROVAL_STATUS foreign key (APPROVAL_STATUS_ID) references TSADV_DIC_APPROVAL_STATUS(ID);
create index IDX_TSADV_PERSON_DOCUMENT_REQUEST_APPROVAL_STATUS on TSADV_PERSON_DOCUMENT_REQUEST (APPROVAL_STATUS_ID);
