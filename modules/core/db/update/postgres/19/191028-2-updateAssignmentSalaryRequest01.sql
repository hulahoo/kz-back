alter table TSADV_ASSIGNMENT_SALARY_REQUEST add constraint FK_TSADV_ASSIGNMENT_SALARY_REQUEST_SUBSTITUTED_EMPLOYEE foreign key (SUBSTITUTED_EMPLOYEE_ID) references BASE_PERSON_GROUP(ID);
create index IDX_TSADV_ASSIGNMENT_SALARY_REQUEST_SUBSTITUTED_EMPLOYEE on TSADV_ASSIGNMENT_SALARY_REQUEST (SUBSTITUTED_EMPLOYEE_ID);
