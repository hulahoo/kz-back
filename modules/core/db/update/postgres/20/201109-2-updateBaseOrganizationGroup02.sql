-- alter table BASE_ORGANIZATION_GROUP add column COMPANY_ID uuid ^
-- update BASE_ORGANIZATION_GROUP set COMPANY_ID = <default_value> ;
-- alter table BASE_ORGANIZATION_GROUP alter column COMPANY_ID set not null ;
alter table BASE_ORGANIZATION_GROUP add column COMPANY_ID uuid not null ;
