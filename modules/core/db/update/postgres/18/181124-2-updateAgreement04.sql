alter table TSADV_AGREEMENT add constraint FK_TSADV_AGREEMENT_FILE foreign key (FILE_ID) references SYS_FILE(ID);
create index IDX_TSADV_AGREEMENT_FILE on TSADV_AGREEMENT (FILE_ID);
