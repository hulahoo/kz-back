alter table TSADV_BOOK add constraint FK_TSADV_BOOK_DJVU foreign key (DJVU_ID) references SYS_FILE(ID);
create index IDX_TSADV_BOOK_DJVU on TSADV_BOOK (DJVU_ID);
