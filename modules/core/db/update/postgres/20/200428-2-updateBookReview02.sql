alter table TSADV_BOOK_REVIEW add constraint FK_TSADV_BOOK_REVIEW_AUTHOR foreign key (AUTHOR_ID) references BASE_PERSON_GROUP(ID);
create index IDX_TSADV_BOOK_REVIEW_AUTHOR on TSADV_BOOK_REVIEW (AUTHOR_ID);
