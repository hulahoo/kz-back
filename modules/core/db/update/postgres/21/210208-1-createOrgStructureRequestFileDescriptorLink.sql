create table TSADV_ORG_STRUCTURE_REQUEST_FILE_DESCRIPTOR_LINK (
    ORG_STRUCTURE_REQUEST_ID uuid,
    FILE_DESCRIPTOR_ID uuid,
    primary key (ORG_STRUCTURE_REQUEST_ID, FILE_DESCRIPTOR_ID)
);
