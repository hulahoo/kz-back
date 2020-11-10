-- alter table BASE_ORGANIZATION add column COMPANY_ID uuid ^
-- update BASE_ORGANIZATION set COMPANY_ID = <default_value> ;
-- alter table BASE_ORGANIZATION alter column COMPANY_ID set not null ;
alter table BASE_ORGANIZATION add column COMPANY_ID uuid ;
