alter table TSADV_LEARNING_EXPENSE add constraint FK_TSADV_LEARNING_EXPENSE_DIC_MONTH foreign key (DIC_MONTH_ID) references TSADV_DIC_MONTH(ID);
create index IDX_TSADV_LEARNING_EXPENSE_DIC_MONTH on TSADV_LEARNING_EXPENSE (DIC_MONTH_ID);
