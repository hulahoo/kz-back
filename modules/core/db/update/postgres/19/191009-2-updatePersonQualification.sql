update TSADV_PERSON_QUALIFICATION set ASSIGN_VALIDATION_DATE = current_date where ASSIGN_VALIDATION_DATE is null ;
alter table TSADV_PERSON_QUALIFICATION alter column ASSIGN_VALIDATION_DATE set not null ;
