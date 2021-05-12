alter table TSADV_DIC_EARNING_POLICY add constraint FK_TSADV_DIC_EARNING_POLICY_COMPANY foreign key (COMPANY_ID) references BASE_DIC_COMPANY(ID);
create unique index IDX_TSADV_DIC_EARNING_POLICY_UK_LANG_VALUE4 on TSADV_DIC_EARNING_POLICY (LANG_VALUE4) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_EARNING_POLICY_UK_DESCRIPTION4 on TSADV_DIC_EARNING_POLICY (DESCRIPTION4) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_EARNING_POLICY_UK_LANG_VALUE3 on TSADV_DIC_EARNING_POLICY (LANG_VALUE3) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_EARNING_POLICY_UK_DESCRIPTION3 on TSADV_DIC_EARNING_POLICY (DESCRIPTION3) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_EARNING_POLICY_UK_DESCRIPTION2 on TSADV_DIC_EARNING_POLICY (DESCRIPTION2) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_EARNING_POLICY_UK_DESCRIPTION1 on TSADV_DIC_EARNING_POLICY (DESCRIPTION1) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_EARNING_POLICY_UK_LANG_VALUE5 on TSADV_DIC_EARNING_POLICY (LANG_VALUE5) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_EARNING_POLICY_UK_LANG_VALUE2 on TSADV_DIC_EARNING_POLICY (LANG_VALUE2) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_EARNING_POLICY_UK_CODE on TSADV_DIC_EARNING_POLICY (CODE) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_EARNING_POLICY_UK_LANG_VALUE1 on TSADV_DIC_EARNING_POLICY (LANG_VALUE1) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_EARNING_POLICY_UK_DESCRIPTION5 on TSADV_DIC_EARNING_POLICY (DESCRIPTION5) where DELETE_TS is null ;
create index IDX_TSADV_DIC_EARNING_POLICY_COMPANY on TSADV_DIC_EARNING_POLICY (COMPANY_ID);
