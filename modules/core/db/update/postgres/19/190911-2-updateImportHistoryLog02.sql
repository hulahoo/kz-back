alter table TSADV_IMPORT_HISTORY_LOG rename column file_id to file_id__u63839 ;
drop index IDX_TSADV_IMPORT_HISTORY_LOG_FILE ;
alter table TSADV_IMPORT_HISTORY_LOG drop constraint FK_TSADV_IMPORT_HISTORY_LOG_FILE ;
alter table TSADV_IMPORT_HISTORY_LOG add column IMPORT_HISTORY_ID uuid ;
