alter table SEC_USER add column PERSON_GROUP_ID uuid ;

update SEC_USER set DTYPE = 'tsadv$UserExt' ;