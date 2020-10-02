alter table BASE_ASSIGNMENT_GROUP add column ASSIGNMENT_NUMBER varchar(255) ^
update BASE_ASSIGNMENT_GROUP set ASSIGNMENT_NUMBER = '' where ASSIGNMENT_NUMBER is null ;
alter table BASE_ASSIGNMENT_GROUP alter column ASSIGNMENT_NUMBER set not null ;
