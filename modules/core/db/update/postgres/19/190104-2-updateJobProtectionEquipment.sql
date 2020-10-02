alter table TSADV_JOB_PROTECTION_EQUIPMENT add column if not exists PERSONAL_PROTECTION_EQUIPMENT_ID uuid not null;
--alter table TSADV_JOB_PROTECTION_EQUIPMENT rename column personal_protection_equipment_id to personal_protection_equipment_id__u72330 ;
--alter table TSADV_JOB_PROTECTION_EQUIPMENT alter column personal_protection_equipment_id__u72330 drop not null ;
--drop index IDX_TSADV_JOBPROTECTIEQUIPMEN_PERSONAL_PROTECTION_EQUIPMENT ;
-- alter table TSADV_JOB_PROTECTION_EQUIPMENT add column PERSONAL_PROTECTION_EQUIPMENT_ID uuid ^
-- update TSADV_JOB_PROTECTION_EQUIPMENT set PERSONAL_PROTECTION_EQUIPMENT_ID = <default_value> ;
-- alter table TSADV_JOB_PROTECTION_EQUIPMENT alter column personal_protection_equipment_id set not null ;
