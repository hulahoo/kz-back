alter table TSADV_IMPROVING_PROFESSIONAL_SKILLS_FILE_DESCRIPTOR_LINK add constraint FK_IMPPROSKIFILDES_IMPROVING_PROFESSIONAL_SKILLS foreign key (IMPROVING_PROFESSIONAL_SKILLS_ID) references TSADV_IMPROVING_PROFESSIONAL_SKILLS(ID);
alter table TSADV_IMPROVING_PROFESSIONAL_SKILLS_FILE_DESCRIPTOR_LINK add constraint FK_IMPPROSKIFILDES_FILE_DESCRIPTOR foreign key (FILE_DESCRIPTOR_ID) references SYS_FILE(ID);