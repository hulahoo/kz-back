alter table TSADV_PORTAL_MENU_CUSTOMIZATION add column ACTIVE boolean ^
update TSADV_PORTAL_MENU_CUSTOMIZATION set ACTIVE = false where ACTIVE is null ;
alter table TSADV_PORTAL_MENU_CUSTOMIZATION alter column ACTIVE set not null ;
alter table TSADV_PORTAL_MENU_CUSTOMIZATION add column NAME2 varchar(255) ;
alter table TSADV_PORTAL_MENU_CUSTOMIZATION add column PARENT_ID uuid ;
alter table TSADV_PORTAL_MENU_CUSTOMIZATION add column NAME3 varchar(255) ;
alter table TSADV_PORTAL_MENU_CUSTOMIZATION add column NAME1 varchar(255) ;
alter table TSADV_PORTAL_MENU_CUSTOMIZATION add column MENU_TYPE varchar(50) ^
update TSADV_PORTAL_MENU_CUSTOMIZATION set MENU_TYPE = 'P' where MENU_TYPE is null ;
alter table TSADV_PORTAL_MENU_CUSTOMIZATION alter column MENU_TYPE set not null ;
