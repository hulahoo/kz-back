alter table BASE_POSITION add column SUP_MANAGER_EXCLUSION boolean ^
update BASE_POSITION set SUP_MANAGER_EXCLUSION = false where SUP_MANAGER_EXCLUSION is null ;
alter table BASE_POSITION alter column SUP_MANAGER_EXCLUSION set not null ;
