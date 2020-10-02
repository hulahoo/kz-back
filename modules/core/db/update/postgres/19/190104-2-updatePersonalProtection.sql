alter table TSADV_PERSONAL_PROTECTION add column if not exists PROTECTION_EQUIPMENT_ID uuid not null ;
--alter table TSADV_PERSONAL_PROTECTION rename column protection_equipment_id to protection_equipment_id__u65626 ;
--alter table TSADV_PERSONAL_PROTECTION alter column protection_equipment_id__u65626 drop not null ;
--drop index IDX_TSADV_PERSONAL_PROTECTION_PROTECTION_EQUIPMENT ;
-- alter table TSADV_PERSONAL_PROTECTION add column PROTECTION_EQUIPMENT_ID uuid ^
-- update TSADV_PERSONAL_PROTECTION set PROTECTION_EQUIPMENT_ID = <default_value> ;
-- alter table TSADV_PERSONAL_PROTECTION alter column protection_equipment_id set not null ;
