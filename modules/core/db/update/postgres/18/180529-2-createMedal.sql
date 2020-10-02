alter table TSADV_MEDAL add constraint FK_TSADV_MEDAL_ICON foreign key (ICON_ID) references SYS_FILE(ID);
alter table TSADV_MEDAL add constraint FK_TSADV_MEDAL_TEMPLATE foreign key (TEMPLATE_ID) references REPORT_REPORT(ID);
create index IDX_TSADV_MEDAL_ICON on TSADV_MEDAL (ICON_ID);
create index IDX_TSADV_MEDAL_TEMPLATE on TSADV_MEDAL (TEMPLATE_ID);
