alter table TSADV_CONTRACT_CONDITIONS rename column AGE_MAX to age_max_id__u03705;
alter table TSADV_CONTRACT_CONDITIONS drop column if exists age_max_id__u03705;
alter table TSADV_CONTRACT_CONDITIONS add column if not exists AGE_MAX integer not null;

alter table TSADV_CONTRACT_CONDITIONS rename column COST_IN_KZT to cost_in_kzt_id__u03706;
alter table TSADV_CONTRACT_CONDITIONS drop  column if exists age_max_id__u03706;
alter table TSADV_CONTRACT_CONDITIONS add column if not exists COST_IN_KZT decimal(19, 2) not null;