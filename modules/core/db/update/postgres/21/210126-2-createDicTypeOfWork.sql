alter table TSADV_DIC_TYPE_OF_WORK add constraint FK_TSADV_DIC_TYPE_OF_WORK_COMPANY foreign key (COMPANY_ID) references BASE_DIC_COMPANY(ID);
create unique index IDX_TSADV_DIC_TYPE_OF_WORK_UK_DESCRIPTION2 on TSADV_DIC_TYPE_OF_WORK (DESCRIPTION2) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_TYPE_OF_WORK_UK_DESCRIPTION3 on TSADV_DIC_TYPE_OF_WORK (DESCRIPTION3) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_TYPE_OF_WORK_UK_DESCRIPTION1 on TSADV_DIC_TYPE_OF_WORK (DESCRIPTION1) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_TYPE_OF_WORK_UK_CODE on TSADV_DIC_TYPE_OF_WORK (CODE) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_TYPE_OF_WORK_UK_LANG_VALUE4 on TSADV_DIC_TYPE_OF_WORK (LANG_VALUE4) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_TYPE_OF_WORK_UK_DESCRIPTION4 on TSADV_DIC_TYPE_OF_WORK (DESCRIPTION4) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_TYPE_OF_WORK_UK_LANG_VALUE5 on TSADV_DIC_TYPE_OF_WORK (LANG_VALUE5) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_TYPE_OF_WORK_UK_DESCRIPTION5 on TSADV_DIC_TYPE_OF_WORK (DESCRIPTION5) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_TYPE_OF_WORK_UK_LANG_VALUE2 on TSADV_DIC_TYPE_OF_WORK (LANG_VALUE2) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_TYPE_OF_WORK_UK_LANG_VALUE3 on TSADV_DIC_TYPE_OF_WORK (LANG_VALUE3) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_TYPE_OF_WORK_UK_LANG_VALUE1 on TSADV_DIC_TYPE_OF_WORK (LANG_VALUE1) where DELETE_TS is null ;
create index IDX_TSADV_DIC_TYPE_OF_WORK_COMPANY on TSADV_DIC_TYPE_OF_WORK (COMPANY_ID);
