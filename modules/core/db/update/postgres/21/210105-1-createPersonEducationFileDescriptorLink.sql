create table TSADV_PERSON_EDUCATION_FILE_DESCRIPTOR_LINK (
    PERSON_EDUCATION_ID uuid,
    FILE_DESCRIPTOR_ID uuid,
    primary key (PERSON_EDUCATION_ID, FILE_DESCRIPTOR_ID)
);