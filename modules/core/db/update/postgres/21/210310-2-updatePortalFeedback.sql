-- update TSADV_PORTAL_FEEDBACK set COMPANY_ID = <default_value> where COMPANY_ID is null ;
alter table TSADV_PORTAL_FEEDBACK alter column COMPANY_ID set not null ;
-- update TSADV_PORTAL_FEEDBACK set CATEGORY_ID = <default_value> where CATEGORY_ID is null ;
alter table TSADV_PORTAL_FEEDBACK alter column CATEGORY_ID set not null ;
update TSADV_PORTAL_FEEDBACK set EMAIL = '' where EMAIL is null ;
alter table TSADV_PORTAL_FEEDBACK alter column EMAIL set not null ;
