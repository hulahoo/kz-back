create table TSADV_BOOK (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    BOOK_NAME_LANG1 varchar(255),
    BOOK_DESCRIPTION_LANG1 varchar(2000),
    AUTHOR_LANG1 varchar(255),
    PUBLISH_DATE date,
    ISBN varchar(255),
    ACTIVE boolean,
    AVERAGE_SCORE decimal(19, 2),
    CATEGORY_ID uuid,
    IMAGE_ID uuid,
    LANGUAGE_ varchar(50),
    FB2_ID uuid,
    EPUB_ID uuid,
    MOBI_ID uuid,
    KF8_ID uuid,
    PDF_ID uuid,
    --
    primary key (ID)
);
