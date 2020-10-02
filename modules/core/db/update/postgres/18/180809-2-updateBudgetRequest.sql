-- update TSADV_BUDGET_REQUEST set BUDGET_HEADER_ID = <default_value> where BUDGET_HEADER_ID is null ;
delete from tsadv_budget_request_item where budget_request_id in (select id from TSADV_BUDGET_REQUEST where budget_header_id is NULL);
delete from TSADV_BUDGET_REQUEST where budget_header_id is NULL ;
alter table TSADV_BUDGET_REQUEST alter column BUDGET_HEADER_ID set not null ;
