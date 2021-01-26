alter table TSADV_CONTRACT_CONDITIONS rename column cost_in_kzt to cost_in_kzt__u85144 ;
alter table TSADV_CONTRACT_CONDITIONS alter column cost_in_kzt__u85144 drop not null ;
alter table TSADV_CONTRACT_CONDITIONS rename column age_max to age_max__u45450 ;
alter table TSADV_CONTRACT_CONDITIONS alter column age_max__u45450 drop not null ;
alter table TSADV_CONTRACT_CONDITIONS add column AGE_MAX integer ^
update TSADV_CONTRACT_CONDITIONS set AGE_MAX = 0 where AGE_MAX is null ;
alter table TSADV_CONTRACT_CONDITIONS alter column AGE_MAX set not null ;
alter table TSADV_CONTRACT_CONDITIONS add column COST_IN_KZT decimal(19, 2) ^
update TSADV_CONTRACT_CONDITIONS set COST_IN_KZT = 0 where COST_IN_KZT is null ;
alter table TSADV_CONTRACT_CONDITIONS alter column COST_IN_KZT set not null ;
