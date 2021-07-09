alter table TSADV_DIC_TYPE_OF_TRAINING add constraint FK_TSADV_DIC_TYPE_OF_TRAINING_COMPANY foreign key (COMPANY_ID) references BASE_DIC_COMPANY(ID);
create unique index IDX_TSADV_DIC_TYPE_OF_TRAINING_UK_LANG_VALUE5 on TSADV_DIC_TYPE_OF_TRAINING (LANG_VALUE5) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_TYPE_OF_TRAINING_UK_DESCRIPTION2 on TSADV_DIC_TYPE_OF_TRAINING (DESCRIPTION2) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_TYPE_OF_TRAINING_UK_CODE on TSADV_DIC_TYPE_OF_TRAINING (CODE) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_TYPE_OF_TRAINING_UK_DESCRIPTION1 on TSADV_DIC_TYPE_OF_TRAINING (DESCRIPTION1) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_TYPE_OF_TRAINING_UK_LANG_VALUE4 on TSADV_DIC_TYPE_OF_TRAINING (LANG_VALUE4) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_TYPE_OF_TRAINING_UK_LANG_VALUE3 on TSADV_DIC_TYPE_OF_TRAINING (LANG_VALUE3) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_TYPE_OF_TRAINING_UK_DESCRIPTION5 on TSADV_DIC_TYPE_OF_TRAINING (DESCRIPTION5) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_TYPE_OF_TRAINING_UK_LANG_VALUE2 on TSADV_DIC_TYPE_OF_TRAINING (LANG_VALUE2) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_TYPE_OF_TRAINING_UK_DESCRIPTION4 on TSADV_DIC_TYPE_OF_TRAINING (DESCRIPTION4) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_TYPE_OF_TRAINING_UK_LANG_VALUE1 on TSADV_DIC_TYPE_OF_TRAINING (LANG_VALUE1) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_TYPE_OF_TRAINING_UK_DESCRIPTION3 on TSADV_DIC_TYPE_OF_TRAINING (DESCRIPTION3) where DELETE_TS is null ;
create index IDX_TSADV_DIC_TYPE_OF_TRAINING_COMPANY on TSADV_DIC_TYPE_OF_TRAINING (COMPANY_ID);
