--INSERT DEFAULT INDICATOR_TYPE FOR ESCAPE TYPE_ID NULL ERROR 
 INSERT INTO public.tsadv_dic_incentive_indicator_type (id, "version", company_id, lang_value1, code,is_system_record,active,is_default) 
 VALUES((md5(random()::text || clock_timestamp()::text)::uuid), 0, (select id from base_dic_company limit 1),'DEFAULT_INDICATOR_TYPE','DEFAULT_INDICATOR_TYPE', false, false, false);
 
 alter table TSADV_DIC_INCENTIVE_INDICATORS add column TYPE_ID uuid ^
 update TSADV_DIC_INCENTIVE_INDICATORS set TYPE_ID = (select id from tsadv_dic_incentive_indicator_type where code = 'DEFAULT_INDICATOR_TYPE') where TYPE_ID IS NULL ;
 alter table TSADV_DIC_INCENTIVE_INDICATORS alter column TYPE_ID set not null ;
--alter table TSADV_DIC_INCENTIVE_INDICATORS add column TYPE_ID uuid not null ;
alter table TSADV_DIC_INCENTIVE_INDICATORS add column GENERAL boolean ^
update TSADV_DIC_INCENTIVE_INDICATORS set GENERAL = false where GENERAL is null ;
alter table TSADV_DIC_INCENTIVE_INDICATORS alter column GENERAL set not null ;
