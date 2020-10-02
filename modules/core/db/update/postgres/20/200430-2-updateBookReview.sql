alter table TSADV_BOOK_REVIEW rename column author_id to author_id__u73465 ;
drop index IDX_TSADV_BOOK_REVIEW_AUTHOR ;
alter table TSADV_BOOK_REVIEW drop constraint FK_TSADV_BOOK_REVIEW_AUTHOR ;
alter table TSADV_BOOK_REVIEW add column AUTHOR_ID uuid ;
