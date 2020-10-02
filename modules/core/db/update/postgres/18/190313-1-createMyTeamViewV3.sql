CREATE OR REPLACE VIEW tal.tsadv_my_team_view_v3
AS WITH RECURSIVE org_pos(id, hierarchy_id, parent_id, element_type, position_group_id, manager_flag, parent_position_group_id, start_date, end_date, organization_group_id, path, lvl, path_pos_name, person_group_id, parent_person_group_id) AS (
SELECT he.id,
he.hierarchy_id,
he.parent_id,
he.element_type,
he.position_group_id,
p.manager_flag,
NULL::uuid AS parent_organization_group_id,
he.start_date,
he.end_date,
a1.organization_group_id,
a1.person_group_id::text AS path,
1 AS lvl,
COALESCE(p.position_full_name_lang1, ''::character varying)::text AS path_pos_name,
a1.person_group_id,
NULL::uuid AS parent_person_group_id,
newid() AS h_id,
NULL::uuid AS h_parent_id
FROM base_hierarchy_element he
JOIN base_hierarchy h ON h.id = he.hierarchy_id AND h.delete_ts IS NULL
JOIN base_position p ON he.position_group_id = p.group_id AND 'now'::text::date >= p.start_date AND 'now'::text::date <= p.end_date AND p.delete_ts IS NULL
LEFT JOIN base_assignment a1 ON a1.position_group_id = he.position_group_id AND 'now'::text::date >= a1.start_date AND 'now'::text::date <= a1.end_date AND a1.primary_flag = true AND a1.delete_ts IS NULL AND a1.assignment_status_id <> '852609db-c23e-af4e-14f3-ea645d38f57d'::uuid
WHERE he.parent_id IS NULL AND he.delete_ts IS NULL AND 'now'::text::date >= he.start_date AND 'now'::text::date <= he.end_date
UNION ALL
SELECT he.id,
he.hierarchy_id,
he.parent_id,
he.element_type,
he.position_group_id,
p.manager_flag,
COALESCE(op.position_group_id, op.parent_position_group_id) AS parent_position_group_id,
he.start_date,
he.end_date,
COALESCE(a1.organization_group_id, op.organization_group_id) AS organization_group_id,
((op.path || '*'::text) || COALESCE(a1.person_group_id::text, 'VACANCY'::text))::character varying AS "varchar",
op.lvl + 1,
(op.path_pos_name || '->'::text) || COALESCE(p.position_full_name_lang1, ''::character varying)::text AS path_pos_name,
a1.person_group_id,
op.person_group_id AS parent_person_group_id,
newid() AS h_id,
op.h_id AS h_parent_id
FROM base_hierarchy_element he
JOIN org_pos op ON he.parent_id = op.id
JOIN base_position p ON he.position_group_id = p.group_id AND 'now'::text::date >= p.start_date AND 'now'::text::date <= p.end_date AND p.delete_ts IS NULL
LEFT JOIN base_assignment a1 ON a1.position_group_id = he.position_group_id AND 'now'::text::date >= a1.start_date AND 'now'::text::date <= a1.end_date AND a1.primary_flag = true AND a1.delete_ts IS NULL AND a1.assignment_status_id <> '852609db-c23e-af4e-14f3-ea645d38f57d'::uuid
WHERE he.element_type = 2 AND he.delete_ts IS NULL AND 'now'::text::date >= he.start_date AND 'now'::text::date <= he.end_date
)
SELECT org_pos.person_group_id AS id,
org_pos.hierarchy_id,
org_pos.parent_id,
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
org_pos.h_id,
org_pos.h_parent_id,
NULL::integer AS version,
NULL::timestamp without time zone AS create_ts,
NULL::character varying AS created_by,
NULL::timestamp without time zone AS update_ts,
NULL::character varying AS updated_by,
NULL::timestamp without time zone AS delete_ts,
NULL::character varying AS deleted_by
FROM org_pos;

-- Permissions

ALTER TABLE tal.tsadv_my_team_view_v3 OWNER TO tal;
GRANT ALL ON TABLE tal.tsadv_my_team_view_v3 TO tal;