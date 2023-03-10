alter table TSADV_CERTIFICATE_TEMPLATE drop constraint if exists FK_TSADV_CERTIFICATE_TEMPLATE_CERTIFICATE_TYPE;
alter table TSADV_CERTIFICATE_TEMPLATE add constraint FK_TSADV_CERTIFICATE_TEMPLATE_CERTIFICATE_TYPE foreign key (CERTIFICATE_TYPE_ID) references TSADV_DIC_CERTIFICATE_TYPE(ID);
create index if not exists IDX_TSADV_CERTIFICATE_TEMPLATE_CERTIFICATE_TYPE on TSADV_CERTIFICATE_TEMPLATE (CERTIFICATE_TYPE_ID);
