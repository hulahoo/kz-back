alter table TSADV_DIC_PROTECTION_EQUIPMENT rename column unit_of_measure_id to unit_of_measure_id__u69318 ;
drop index IDX_TSADV_DIC_PROTECTION_EQUIPMENT_UNIT_OF_MEASURE ;
alter table TSADV_DIC_PROTECTION_EQUIPMENT drop constraint FK_TSADV_DIC_PROTECTION_EQUIPMENT_UNIT_OF_MEASURE ;
alter table TSADV_DIC_PROTECTION_EQUIPMENT add column UNIT_OF_MEASURE_ID uuid ;
