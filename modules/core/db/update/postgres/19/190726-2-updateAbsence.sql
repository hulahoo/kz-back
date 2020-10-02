alter table TSADV_ABSENCE add column IF NOT EXISTS PERIOD_START date ;
alter table TSADV_ABSENCE add column IF NOT EXISTS PERIOD_END date ;
alter table TSADV_ABSENCE add column IF NOT EXISTS USE_IN_BALANCE boolean ;
