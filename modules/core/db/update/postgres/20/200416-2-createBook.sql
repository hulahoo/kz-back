alter table TSADV_BOOK add constraint FK_TSADV_BOOK_CATEGORY foreign key (CATEGORY_ID) references TSADV_DIC_BOOK_CATEGORY(ID);
alter table TSADV_BOOK add constraint FK_TSADV_BOOK_IMAGE foreign key (IMAGE_ID) references SYS_FILE(ID);
alter table TSADV_BOOK add constraint FK_TSADV_BOOK_FB2 foreign key (FB2_ID) references SYS_FILE(ID);
alter table TSADV_BOOK add constraint FK_TSADV_BOOK_EPUB foreign key (EPUB_ID) references SYS_FILE(ID);
alter table TSADV_BOOK add constraint FK_TSADV_BOOK_MOBI foreign key (MOBI_ID) references SYS_FILE(ID);
alter table TSADV_BOOK add constraint FK_TSADV_BOOK_KF8 foreign key (KF8_ID) references SYS_FILE(ID);
alter table TSADV_BOOK add constraint FK_TSADV_BOOK_PDF foreign key (PDF_ID) references SYS_FILE(ID);
create index IDX_TSADV_BOOK_CATEGORY on TSADV_BOOK (CATEGORY_ID);
create index IDX_TSADV_BOOK_IMAGE on TSADV_BOOK (IMAGE_ID);
create index IDX_TSADV_BOOK_FB2 on TSADV_BOOK (FB2_ID);
create index IDX_TSADV_BOOK_EPUB on TSADV_BOOK (EPUB_ID);
create index IDX_TSADV_BOOK_MOBI on TSADV_BOOK (MOBI_ID);
create index IDX_TSADV_BOOK_KF8 on TSADV_BOOK (KF8_ID);
create index IDX_TSADV_BOOK_PDF on TSADV_BOOK (PDF_ID);
