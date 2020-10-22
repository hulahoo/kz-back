-- alter table TSADV_ORGANIZATION_HR_USER add column HR_ROLE_ID uuid ^
-- update TSADV_ORGANIZATION_HR_USER set HR_ROLE_ID = <default_value> ;
-- alter table TSADV_ORGANIZATION_HR_USER alter column HR_ROLE_ID set not null ;
alter table TSADV_ORGANIZATION_HR_USER add column HR_ROLE_ID uuid not null ;
