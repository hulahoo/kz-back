alter table TSADV_COIN_DISTRIBUTION_RULE rename column employee_coins to employee_coins__u54796 ;
alter table TSADV_COIN_DISTRIBUTION_RULE alter column employee_coins__u54796 drop not null ;
alter table TSADV_COIN_DISTRIBUTION_RULE rename column manager_coins to manager_coins__u63832 ;
alter table TSADV_COIN_DISTRIBUTION_RULE alter column manager_coins__u63832 drop not null ;
alter table TSADV_COIN_DISTRIBUTION_RULE add column COINS bigint ^
update TSADV_COIN_DISTRIBUTION_RULE set COINS = 0 where COINS is null ;
alter table TSADV_COIN_DISTRIBUTION_RULE alter column COINS set not null ;
