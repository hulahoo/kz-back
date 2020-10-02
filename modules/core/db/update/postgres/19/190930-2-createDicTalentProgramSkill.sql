alter table TSADV_DIC_TALENT_PROGRAM_SKILL add constraint  FK_TSADV_DIC_TALENT_PROGRAM_SKILL_TALENT_PROGRAM_STEP_SKILL foreign key (TALENT_PROGRAM_STEP_SKILL_ID) references TSADV_TALENT_PROGRAM_STEP_SKILL(ID);
create unique index if not exists IDX_TSADV_DIC_TALENT_PROGRAM_SKILL_UK_DESCRIPTION3 on TSADV_DIC_TALENT_PROGRAM_SKILL (DESCRIPTION3) where DELETE_TS is null ;
create unique index if not exists IDX_TSADV_DIC_TALENT_PROGRAM_SKILL_UK_DESCRIPTION4 on TSADV_DIC_TALENT_PROGRAM_SKILL (DESCRIPTION4) where DELETE_TS is null ;
create unique index if not exists IDX_TSADV_DIC_TALENT_PROGRAM_SKILL_UK_DESCRIPTION1 on TSADV_DIC_TALENT_PROGRAM_SKILL (DESCRIPTION1) where DELETE_TS is null ;
create unique index if not exists IDX_TSADV_DIC_TALENT_PROGRAM_SKILL_UK_DESCRIPTION2 on TSADV_DIC_TALENT_PROGRAM_SKILL (DESCRIPTION2) where DELETE_TS is null ;
create unique index if not exists IDX_TSADV_DIC_TALENT_PROGRAM_SKILL_UK_CODE on TSADV_DIC_TALENT_PROGRAM_SKILL (CODE) where DELETE_TS is null ;
create unique index if not exists IDX_TSADV_DIC_TALENT_PROGRAM_SKILL_UK_LANG_VALUE2 on TSADV_DIC_TALENT_PROGRAM_SKILL (LANG_VALUE2) where DELETE_TS is null ;
create unique index if not exists IDX_TSADV_DIC_TALENT_PROGRAM_SKILL_UK_DESCRIPTION5 on TSADV_DIC_TALENT_PROGRAM_SKILL (DESCRIPTION5) where DELETE_TS is null ;
create unique index if not exists IDX_TSADV_DIC_TALENT_PROGRAM_SKILL_UK_LANG_VALUE1 on TSADV_DIC_TALENT_PROGRAM_SKILL (LANG_VALUE1) where DELETE_TS is null ;
create unique index if not exists IDX_TSADV_DIC_TALENT_PROGRAM_SKILL_UK_LANG_VALUE4 on TSADV_DIC_TALENT_PROGRAM_SKILL (LANG_VALUE4) where DELETE_TS is null ;
create unique index if not exists IDX_TSADV_DIC_TALENT_PROGRAM_SKILL_UK_LANG_VALUE3 on TSADV_DIC_TALENT_PROGRAM_SKILL (LANG_VALUE3) where DELETE_TS is null ;
create unique index if not exists IDX_TSADV_DIC_TALENT_PROGRAM_SKILL_UK_LANG_VALUE5 on TSADV_DIC_TALENT_PROGRAM_SKILL (LANG_VALUE5) where DELETE_TS is null ;
create index if not exists IDX_TSADV_DIC_TALENT_PROGRAM_SKILL_TALENT_PROGRAM_STEP_SKILL on TSADV_DIC_TALENT_PROGRAM_SKILL (TALENT_PROGRAM_STEP_SKILL_ID);
