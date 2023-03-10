alter table TSADV_COIN_DISTRIBUTION_JOB add constraint FK_TSADV_COIN_DISTRIBUTION_JOB_JOB_GROUP foreign key (JOB_GROUP_ID) references TSADV_JOB_GROUP(ID);
alter table TSADV_COIN_DISTRIBUTION_JOB add constraint FK_TSADV_COIN_DISTRIBUTION_JOB_COIN_DISTRIBUTION_RULE foreign key (COIN_DISTRIBUTION_RULE_ID) references TSADV_COIN_DISTRIBUTION_RULE(ID);
create index IDX_TSADV_COIN_DISTRIBUTION_JOB_JOB_GROUP on TSADV_COIN_DISTRIBUTION_JOB (JOB_GROUP_ID);
create index IDX_TSADV_COIN_DISTRIBUTION_JOB_COIN_DISTRIBUTION_RULE on TSADV_COIN_DISTRIBUTION_JOB (COIN_DISTRIBUTION_RULE_ID);
