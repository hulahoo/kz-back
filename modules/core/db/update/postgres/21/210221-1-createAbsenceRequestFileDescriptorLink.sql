create table TSADV_ABSENCE_REQUEST_FILE_DESCRIPTOR_LINK (
    ABSENCE_REQUEST_ID uuid,
    FILE_DESCRIPTOR_ID uuid,
    primary key (ABSENCE_REQUEST_ID, FILE_DESCRIPTOR_ID)
);
