alter table TSADV_DIC_ABSENCE_PURPOSE add constraint FK_TSADV_DIC_ABSENCE_PURPOSE_COMPANY foreign key (COMPANY_ID) references BASE_DIC_COMPANY(ID);
create unique index IDX_TSADV_DIC_ABSENCE_PURPOSE_UK_LANG_VALUE2 on TSADV_DIC_ABSENCE_PURPOSE (LANG_VALUE2) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_ABSENCE_PURPOSE_UK_LANG_VALUE3 on TSADV_DIC_ABSENCE_PURPOSE (LANG_VALUE3) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_ABSENCE_PURPOSE_UK_LANG_VALUE1 on TSADV_DIC_ABSENCE_PURPOSE (LANG_VALUE1) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_ABSENCE_PURPOSE_UK_DESCRIPTION5 on TSADV_DIC_ABSENCE_PURPOSE (DESCRIPTION5) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_ABSENCE_PURPOSE_UK_CODE on TSADV_DIC_ABSENCE_PURPOSE (CODE) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_ABSENCE_PURPOSE_UK_DESCRIPTION4 on TSADV_DIC_ABSENCE_PURPOSE (DESCRIPTION4) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_ABSENCE_PURPOSE_UK_LANG_VALUE4 on TSADV_DIC_ABSENCE_PURPOSE (LANG_VALUE4) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_ABSENCE_PURPOSE_UK_LANG_VALUE5 on TSADV_DIC_ABSENCE_PURPOSE (LANG_VALUE5) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_ABSENCE_PURPOSE_UK_DESCRIPTION1 on TSADV_DIC_ABSENCE_PURPOSE (DESCRIPTION1) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_ABSENCE_PURPOSE_UK_DESCRIPTION3 on TSADV_DIC_ABSENCE_PURPOSE (DESCRIPTION3) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_ABSENCE_PURPOSE_UK_DESCRIPTION2 on TSADV_DIC_ABSENCE_PURPOSE (DESCRIPTION2) where DELETE_TS is null ;
create index IDX_TSADV_DIC_ABSENCE_PURPOSE_COMPANY on TSADV_DIC_ABSENCE_PURPOSE (COMPANY_ID);
