create or replace function refresh_organization_structure() -- Обновление Организационной структуры (по ночам)
    returns character varying
    language plpgsql
as
$function$
begin
    delete from tsadv_organization_structure;
    insert into tsadv_organization_structure (
        id,
        hierarchy_id,
        parent_id,
        element_type,
        organization_group_id,
        parent_organization_group_id,
        start_date,
        end_date,
        path,
        _level,
        path_org_name1,
        path_org_name2,
        path_org_name3,
        version,
        create_ts,
        created_by,
        update_ts,
        updated_by,
        delete_ts,
        deleted_by
    )
    WITH RECURSIVE org_pos(
                           id,
                           hierarchy_id,
                           parent_id,
                           element_type,
                           organization_group_id,
                           parent_organization_group_id,
                           start_date,
                           end_date,
                           path,
                           level,
                           path_org_name1,
                           path_org_name2,
                           path_org_name3
        ) AS (
        SELECT he.id,
               he.hierarchy_id,
               he.parent_id,
               he.element_type,
               he.organization_group_id,
               NULL::uuid                                                       AS parent_organization_group_id,
               he.start_date,
               he.end_date,
               he.organization_group_id::character varying                      AS path,
               1,
               COALESCE(o.organization_name_lang1, ''::character varying)::text AS path_org_name1,
               COALESCE(o.organization_name_lang2, ''::character varying)::text AS path_org_name2,
               COALESCE(o.organization_name_lang3, ''::character varying)::text AS path_org_name3
        FROM base_hierarchy_element he
                 JOIN base_hierarchy h ON h.id = he.hierarchy_id
                 JOIN base_organization o ON he.organization_group_id = o.group_id
            and current_date between o.start_date and o.end_date
            and o.delete_ts is null
        WHERE he.parent_id IS NULL
        UNION ALL
        SELECT he.id,
               he.hierarchy_id,
               he.parent_id,
               he.element_type,
               COALESCE(he.organization_group_id, op.organization_group_id)                                  AS organization_group_id,
               COALESCE(op.organization_group_id, op.parent_organization_group_id)                           AS parent_organization_group_id,
               he.start_date,
               he.end_date,
               ((op.path::text || '*'::text) || he.organization_group_id)::character varying                 AS "varchar",
               op.level + 1,
               op.path_org_name1 || '->' ||
               COALESCE(o.organization_name_lang1, ''::character varying)::text                              AS path_org_name1,
               op.path_org_name2 || '->' ||
               COALESCE(o.organization_name_lang2, ''::character varying)::text                              AS path_org_name2,
               op.path_org_name3 || '->' ||
               COALESCE(o.organization_name_lang3, ''::character varying)::text                              AS path_org_name3
        FROM base_hierarchy_element he
                 JOIN org_pos op ON he.parent_id = op.id
                 JOIN base_organization o ON he.organization_group_id = o.group_id
            and current_date between o.start_date and o.end_date
            and o.delete_ts is null
        WHERE he.element_type = 1
          and he.delete_ts is null
    )
    SELECT org_pos.id,
           org_pos.hierarchy_id,
           org_pos.parent_id,
           org_pos.element_type,
           org_pos.organization_group_id,
           org_pos.parent_organization_group_id,
           org_pos.start_date,
           org_pos.end_date,
           org_pos.path,
           org_pos.level,
           org_pos.path_org_name1,
           org_pos.path_org_name2,
           org_pos.path_org_name3,
           1::integer                        AS version,
           NULL::timestamp without time zone AS create_ts,
           NULL::character varying           AS created_by,
           current_timestamp                 AS update_ts,
           NULL::character varying           AS updated_by,
           NULL::timestamp without time zone AS delete_ts,
           NULL::character varying           AS deleted_by
    FROM org_pos;

    return 'success';
end;
$function$
;
