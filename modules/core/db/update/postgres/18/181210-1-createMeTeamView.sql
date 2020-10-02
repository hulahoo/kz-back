DROP MATERIALIZED VIEW IF EXISTS tsadv_my_team_view;

CREATE materialized VIEW tsadv_my_team_view as
  WITH RECURSIVE org_pos(id, hierarchy_id, parent_id, element_type, position_group_id, manager_flag, parent_position_group_id, start_date, end_date, organization_group_id, path, lvl, path_pos_name, person_group_id, parent_person_group_id) AS (
    SELECT he.id as id,
      he.hierarchy_id,
           he.parent_id as parent_id,
      he.element_type,
      he.position_group_id,
      p.manager_flag,
           NULL::uuid AS parent_organization_group_id,
      he.start_date,
      he.end_date,
      a1.organization_group_id,
           a1.person_group_id ::text AS path,
           1 lvl,
           COALESCE(p.position_full_name_lang1, ''::character varying)::text AS path_pos_name,
      a1.person_group_id,
           null::uuid parent_person_group_id
    FROM base_hierarchy_element he
      JOIN base_hierarchy h ON h.id = he.hierarchy_id
      JOIN base_position p ON he.position_group_id = p.group_id
                              and current_date between p.start_date and p.end_date
                              and p.delete_ts is null
      join base_assignment a1
        on a1.position_group_id=he.position_group_id
           and current_date between a1.start_date and a1.end_date
           and a1.primary_flag=true
    WHERE he.parent_id IS NULL
    UNION ALL
    SELECT he.id as id,
      he.hierarchy_id,
      he.parent_id,
      he.element_type,
           he.position_group_id AS position_group_id,
      p.manager_flag,
           COALESCE(op.position_group_id, op.parent_position_group_id) AS parent_position_group_id,
      he.start_date,
      he.end_date,
           COALESCE(a1.organization_group_id, op.organization_group_id) AS organization_group_id,
           ((op.path::text || '*'::text) || coalesce(a1.person_group_id::text, 'VACANCY'))::character varying AS "varchar",
      op.lvl + 1,
           (op.path_pos_name || '->'::text) || COALESCE(p.position_full_name_lang1, ''::character varying)::text AS path_pos_name,
      a1.person_group_id,
           a2.person_group_id as parent_person_group_id
    FROM base_hierarchy_element he
      JOIN org_pos op ON he.parent_id = op.id
      JOIN base_position p ON he.position_group_id = p.group_id
                              and current_date between p.start_date and p.end_date
                              and p.delete_ts is null
      join base_assignment a1
        on a1.position_group_id=he.position_group_id
           and current_date between a1.start_date and a1.end_date
           and a1.primary_flag=true
      join base_assignment a2
        on a2.position_group_id=COALESCE(op.position_group_id, op.parent_position_group_id)
           and current_date between a2.start_date and a2.end_date
           and a2.primary_flag=true
    WHERE he.element_type = 2
  )
  SELECT
    org_pos.person_group_id as id,
    org_pos.hierarchy_id,
    org_pos.parent_person_group_id as parent_id,
    org_pos.element_type,
    org_pos.position_group_id,
    org_pos.manager_flag,
    org_pos.parent_position_group_id,
    org_pos.start_date,
    org_pos.end_date,
    org_pos.organization_group_id,
    org_pos.path,
    org_pos.lvl,
    org_pos.path_pos_name,
    org_pos.person_group_id,
    org_pos.parent_person_group_id,
    NULL::integer AS version,
    NULL::timestamp without time zone AS create_ts,
    NULL::character varying AS created_by,
    NULL::timestamp without time zone AS update_ts,
    NULL::character varying AS updated_by,
    NULL::timestamp without time zone AS delete_ts,
    NULL::character varying AS deleted_by
  FROM org_pos;