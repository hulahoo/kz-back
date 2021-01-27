alter table TSADV_ABS_PURPOSE_SETTING add constraint FK_TSADV_ABS_PURPOSE_SETTING_ABSENCE_TYPE foreign key (ABSENCE_TYPE_ID) references TSADV_DIC_ABSENCE_TYPE(ID);
alter table TSADV_ABS_PURPOSE_SETTING add constraint FK_TSADV_ABS_PURPOSE_SETTING_ABSENCE_PURPOSE foreign key (ABSENCE_PURPOSE_ID) references TSADV_DIC_ABSENCE_PURPOSE(ID);
create unique index IDX_TSADV_ABS_PURPOSE_SETTING_UNQ on TSADV_ABS_PURPOSE_SETTING (ABSENCE_TYPE_ID, ABSENCE_PURPOSE_ID) where DELETE_TS is null ;
create index IDX_TSADV_ABS_PURPOSE_SETTING_ABSENCE_TYPE on TSADV_ABS_PURPOSE_SETTING (ABSENCE_TYPE_ID);
create index IDX_TSADV_ABS_PURPOSE_SETTING_ABSENCE_PURPOSE on TSADV_ABS_PURPOSE_SETTING (ABSENCE_PURPOSE_ID);
