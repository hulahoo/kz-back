alter table TSADV_DIC_PROTECTION_EQUIPMENT add constraint FK_TSADV_DIC_PROTECTION_EQUIPMENT_TYPE foreign key (TYPE_ID) references TSADV_DIC_PROTECTION_EQUIPMENT_TYPE(ID);
alter table TSADV_DIC_PROTECTION_EQUIPMENT add constraint FK_TSADV_DIC_PROTECTION_EQUIPMENT_REPLACEMENT_UOM foreign key (REPLACEMENT_UOM_ID) references TSADV_DIC_MEASURE_TYPE(ID);
alter table TSADV_DIC_PROTECTION_EQUIPMENT add constraint FK_TSADV_DIC_PROTECTION_EQUIPMENT_UNIT_OF_MEASURE foreign key (UNIT_OF_MEASURE_ID) references TSADV_DIC_MEASURE_TYPE(ID);
create unique index IDX_TSADV_DIC_PROTECTION_EQUIPMENT_UK_DESCRIPTION4 on TSADV_DIC_PROTECTION_EQUIPMENT (DESCRIPTION4) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_PROTECTION_EQUIPMENT_UK_DESCRIPTION5 on TSADV_DIC_PROTECTION_EQUIPMENT (DESCRIPTION5) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_PROTECTION_EQUIPMENT_UK_LANG_VALUE4 on TSADV_DIC_PROTECTION_EQUIPMENT (LANG_VALUE4) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_PROTECTION_EQUIPMENT_UK_DESCRIPTION1 on TSADV_DIC_PROTECTION_EQUIPMENT (DESCRIPTION1) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_PROTECTION_EQUIPMENT_UK_LANG_VALUE5 on TSADV_DIC_PROTECTION_EQUIPMENT (LANG_VALUE5) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_PROTECTION_EQUIPMENT_UK_DESCRIPTION2 on TSADV_DIC_PROTECTION_EQUIPMENT (DESCRIPTION2) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_PROTECTION_EQUIPMENT_UK_CODE on TSADV_DIC_PROTECTION_EQUIPMENT (CODE) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_PROTECTION_EQUIPMENT_UK_DESCRIPTION3 on TSADV_DIC_PROTECTION_EQUIPMENT (DESCRIPTION3) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_PROTECTION_EQUIPMENT_UK_LANG_VALUE1 on TSADV_DIC_PROTECTION_EQUIPMENT (LANG_VALUE1) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_PROTECTION_EQUIPMENT_UK_LANG_VALUE2 on TSADV_DIC_PROTECTION_EQUIPMENT (LANG_VALUE2) where DELETE_TS is null ;
create unique index IDX_TSADV_DIC_PROTECTION_EQUIPMENT_UK_LANG_VALUE3 on TSADV_DIC_PROTECTION_EQUIPMENT (LANG_VALUE3) where DELETE_TS is null ;
create index IDX_TSADV_DIC_PROTECTION_EQUIPMENT_TYPE on TSADV_DIC_PROTECTION_EQUIPMENT (TYPE_ID);
create index IDX_TSADV_DIC_PROTECTION_EQUIPMENT_REPLACEMENT_UOM on TSADV_DIC_PROTECTION_EQUIPMENT (REPLACEMENT_UOM_ID);
create index IDX_TSADV_DIC_PROTECTION_EQUIPMENT_UNIT_OF_MEASURE on TSADV_DIC_PROTECTION_EQUIPMENT (UNIT_OF_MEASURE_ID);
