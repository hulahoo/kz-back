alter table TSADV_DIC_LC_ARTICLE add constraint FK_TSADV_DIC_LC_ARTICLE_COMPANY foreign key (COMPANY_ID) references BASE_DIC_COMPANY(ID);
create index IDX_TSADV_DIC_LC_ARTICLE_COMPANY on TSADV_DIC_LC_ARTICLE (COMPANY_ID);
