alter table TSADV_JOB_PROTECTION_EQUIPMENT add constraint FK_TSADV_JOBPROTECTIEQUIPMEN_PERSONAL_PROTECTION_EQUIPMENT1 foreign key (PERSONAL_PROTECTION_EQUIPMENT_ID) references TSADV_DIC_PROTECTION_EQUIPMENT(ID);
create index IDX_TSADV_JOBPROTECTIEQUIPMEN_PERSONAL_PROTECTION_EQUIPMENT1 on TSADV_JOB_PROTECTION_EQUIPMENT (PERSONAL_PROTECTION_EQUIPMENT_ID);
