alter table TSADV_ATTESTATION_ORGANIZATION add constraint FK_TSADV_ATTESTATION_ORGANIZATION_ATTESTATION foreign key (ATTESTATION_ID) references TSADV_ATTESTATION(ID);
alter table TSADV_ATTESTATION_ORGANIZATION add constraint FK_TSADV_ATTESTATION_ORGANIZATION_ORGANIZATION_GROUP foreign key (ORGANIZATION_GROUP_ID) references BASE_ORGANIZATION_GROUP(ID);
create index IDX_TSADV_ATTESTATION_ORGANIZATION_ATTESTATION on TSADV_ATTESTATION_ORGANIZATION (ATTESTATION_ID);
create index IDX_TSADV_ATTESTATION_ORGANIZATION_ORGANIZATION_GROUP on TSADV_ATTESTATION_ORGANIZATION (ORGANIZATION_GROUP_ID);
