create table TSADV_PORTAL_FEEDBACK_QUESTIONS_FILE_DESCRIPTOR_LINK (
    PORTAL_FEEDBACK_QUESTIONS_ID uuid,
    FILE_DESCRIPTOR_ID uuid,
    primary key (PORTAL_FEEDBACK_QUESTIONS_ID, FILE_DESCRIPTOR_ID)
);
