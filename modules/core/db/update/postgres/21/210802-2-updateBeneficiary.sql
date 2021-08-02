alter table TSADV_BENEFICIARY add column STREET_NAME varchar(2500) ;
alter table TSADV_BENEFICIARY add column BUILDING varchar(2500) ;
 alter table TSADV_BENEFICIARY add column COUNTRY_ID uuid ^
 --USED COUNTRY WITH CODE('KZ') AS DEFAULT VALUE IN NULL FIELDS
update TSADV_BENEFICIARY set COUNTRY_ID = (SELECT id FROM public.base_dic_country WHERE code = 'KZ') where country_id is null;
 alter table TSADV_BENEFICIARY alter column COUNTRY_ID set not null ;
--alter table TSADV_BENEFICIARY add column COUNTRY_ID uuid not null ;
 alter table TSADV_BENEFICIARY add column ADDRESS_TYPE_ID uuid ^
 update TSADV_BENEFICIARY set ADDRESS_TYPE_ID = (SELECT id FROM public.tsadv_dic_address_type WHERE lang_value1 = 'Прописка') ;
 alter table TSADV_BENEFICIARY alter column ADDRESS_TYPE_ID set not null ;
--alter table TSADV_BENEFICIARY add column ADDRESS_TYPE_ID uuid not null ;
alter table TSADV_BENEFICIARY add column FLAT varchar(2500) ;
alter table TSADV_BENEFICIARY add column POSTAL_CODE varchar(2500) ;
alter table TSADV_BENEFICIARY add column ADDRESS_FOR_EXPATS varchar(2500) ;
alter table TSADV_BENEFICIARY add column BLOCK varchar(2500) ;
alter table TSADV_BENEFICIARY add column ADDRESS_KATO_CODE_ID uuid ;
 alter table TSADV_BENEFICIARY add column STREET_TYPE_ID uuid ^
 update TSADV_BENEFICIARY set STREET_TYPE_ID = (SELECT id FROM public.tsadv_dic_street_type WHERE code = 'XX_HR_001') ;
 alter table TSADV_BENEFICIARY alter column STREET_TYPE_ID set not null ;
--alter table TSADV_BENEFICIARY add column STREET_TYPE_ID uuid not null ;
