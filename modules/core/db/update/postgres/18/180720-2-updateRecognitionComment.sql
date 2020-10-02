alter table TSADV_RECOGNITION_COMMENT add column TEXT_EN varchar(2000) ^
update TSADV_RECOGNITION_COMMENT set TEXT_EN = TEXT where TEXT_EN is null ;
alter table TSADV_RECOGNITION_COMMENT alter column TEXT_EN set not null ;
alter table TSADV_RECOGNITION_COMMENT add column TEXT_RU varchar(2000) ^
update TSADV_RECOGNITION_COMMENT set TEXT_RU = TEXT where TEXT_RU is null ;
alter table TSADV_RECOGNITION_COMMENT alter column TEXT_RU set not null ;
