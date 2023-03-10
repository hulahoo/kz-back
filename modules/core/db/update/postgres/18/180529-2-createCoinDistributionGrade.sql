alter table TSADV_COIN_DISTRIBUTION_GRADE add constraint FK_TSADV_COIN_DISTRIBUTION_GRADE_GRADE_GROUP foreign key (GRADE_GROUP_ID) references TSADV_GRADE_GROUP(ID);
alter table TSADV_COIN_DISTRIBUTION_GRADE add constraint FK_TSADV_COIN_DISTRIBUTION_GRADE_COIN_DISTRIBUTION_RULE foreign key (COIN_DISTRIBUTION_RULE_ID) references TSADV_COIN_DISTRIBUTION_RULE(ID);
create index IDX_TSADV_COIN_DISTRIBUTION_GRADE_GRADE_GROUP on TSADV_COIN_DISTRIBUTION_GRADE (GRADE_GROUP_ID);
create index IDX_TSADV_COIN_DISTRIBUTION_GRADE_COIN_DISTRIBUTION_RULE on TSADV_COIN_DISTRIBUTION_GRADE (COIN_DISTRIBUTION_RULE_ID);
