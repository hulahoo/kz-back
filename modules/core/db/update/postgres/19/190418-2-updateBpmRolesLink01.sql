alter table TSADV_BPM_ROLES_LINK add constraint FK_TSADV_BPM_ROLES_LINK_POSITION_BPM_ROLE foreign key (POSITION_BPM_ROLE_ID) references TSADV_POSITION_BPM_ROLE(ID);
create index IDX_TSADV_BPM_ROLES_LINK_POSITION_BPM_ROLE on TSADV_BPM_ROLES_LINK (POSITION_BPM_ROLE_ID);
