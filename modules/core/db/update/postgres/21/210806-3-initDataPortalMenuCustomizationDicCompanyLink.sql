INSERT INTO TSADV_PORTAL_MENU_CUSTOMIZATION_DIC_COMPANY_LINK
SELECT c.id as PORTAL_MENU_CUSTOMIZATION_ID, gc::uuid as DIC_COMPANY_ID
FROM TSADV_PORTAL_MENU_CUSTOMIZATION c
         join sys_config_value('general.company.id') gc on 1 = 1