alter table BASE_PARTY add column TRAINING_PROVIDER boolean ^
update BASE_PARTY set TRAINING_PROVIDER = false where TRAINING_PROVIDER is null ;
alter table BASE_PARTY alter column TRAINING_PROVIDER set not null ;
