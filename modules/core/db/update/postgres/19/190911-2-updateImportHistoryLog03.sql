alter table TSADV_IMPORT_HISTORY_LOG add constraint FK_TSADV_IMPORT_HISTORY_LOG_IMPORT_HISTORY foreign key (IMPORT_HISTORY_ID) references TSADV_IMPORT_HISTORY(ID);
create index IDX_TSADV_IMPORT_HISTORY_LOG_IMPORT_HISTORY on TSADV_IMPORT_HISTORY_LOG (IMPORT_HISTORY_ID);
