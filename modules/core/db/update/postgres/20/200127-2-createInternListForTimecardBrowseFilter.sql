INSERT INTO sec_filter
(id, create_ts, created_by, "version", update_ts, updated_by, delete_ts, deleted_by, component, "name", code, "xml", user_id, global_default)
VALUES(newid(), current_timestamp, 'admin', 1, current_timestamp, NULL, NULL, NULL, '[tsadv$InternListForTimecard.browse].filter', 'Стажёры', NULL, '<?xml version="1.0" encoding="UTF-8"?>

<filter>
  <and>
    <c name="WFQejSIovX" class="java.lang.String" width="1" type="CUSTOM" locCaption="Табельный номер/ФИО" entityAlias="e"><![CDATA[concat({E}.lastName, '' '' , {E}.firstName, '' '', {E}.middleName) like :component$filter.WFQejSIovX49720
or {E}.employeeNumber like :component$filter.WFQejSIovX49720]]>
      <param name="component$filter.WFQejSIovX49720" javaClass="java.lang.String">NULL</param>
    </c>
    <c name="EezwxaRBxW" class="java.lang.String" width="1" type="CUSTOM" locCaption="ЦЗ/Организация" entityAlias="e"><![CDATA[o.costCenter.langValue1 like :component$filter.EezwxaRBxW72780
or o.costCenter.langValue2 like :component$filter.EezwxaRBxW72780
or o.costCenter.langValue3 like :component$filter.EezwxaRBxW72780
or o.costCenter.langValue4 like :component$filter.EezwxaRBxW72780
or o.costCenter.langValue5 like :component$filter.EezwxaRBxW72780
or o.organizationNameLang1 like :component$filter.EezwxaRBxW72780
or o.organizationNameLang2 like :component$filter.EezwxaRBxW72780
or o.organizationNameLang3 like :component$filter.EezwxaRBxW72780
or o.organizationNameLang4 like :component$filter.EezwxaRBxW72780
or o.organizationNameLang5 like :component$filter.EezwxaRBxW72780]]>
      <param name="component$filter.EezwxaRBxW72780" javaClass="java.lang.String">NULL</param>
      <join><![CDATA[join base$OrganizationExt o
on o.group.id = a.organizationGroup.id]]></join>
    </c>
  </and>
</filter>
', NULL, true);
