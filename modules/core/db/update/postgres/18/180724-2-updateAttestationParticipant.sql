alter table TSADV_ATTESTATION_PARTICIPANT rename column person_type_id to person_type_id__u55802 ;
drop index IDX_TSADV_ATTESTATION_PARTICIPANT_PERSON_TYPE ;
alter table TSADV_ATTESTATION_PARTICIPANT drop constraint FK_TSADV_ATTESTATION_PARTICIPANT_PERSON_TYPE ;
