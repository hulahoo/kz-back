alter table TSADV_BUDGET_REQUEST_ITEM rename column dec_value to dec_value__u37331 ;
alter table TSADV_BUDGET_REQUEST_ITEM rename column nov_value to nov_value__u63656 ;
alter table TSADV_BUDGET_REQUEST_ITEM rename column oct_value to oct_value__u56247 ;
alter table TSADV_BUDGET_REQUEST_ITEM rename column sep_value to sep_value__u51405 ;
alter table TSADV_BUDGET_REQUEST_ITEM rename column aug_value to aug_value__u56061 ;
alter table TSADV_BUDGET_REQUEST_ITEM rename column jul_value to jul_value__u14538 ;
alter table TSADV_BUDGET_REQUEST_ITEM rename column jun_value to jun_value__u27948 ;
alter table TSADV_BUDGET_REQUEST_ITEM rename column may_value to may_value__u59517 ;
alter table TSADV_BUDGET_REQUEST_ITEM rename column apr_value to apr_value__u67660 ;
alter table TSADV_BUDGET_REQUEST_ITEM rename column mar_value to mar_value__u43510 ;
alter table TSADV_BUDGET_REQUEST_ITEM rename column feb_value to feb_value__u99851 ;
alter table TSADV_BUDGET_REQUEST_ITEM rename column jan_value to jan_value__u64464 ;
alter table TSADV_BUDGET_REQUEST_ITEM add column FIRST_DAY_OF_MONTH date ^
update TSADV_BUDGET_REQUEST_ITEM set FIRST_DAY_OF_MONTH = current_date where FIRST_DAY_OF_MONTH is null ;
alter table TSADV_BUDGET_REQUEST_ITEM alter column FIRST_DAY_OF_MONTH set not null ;
