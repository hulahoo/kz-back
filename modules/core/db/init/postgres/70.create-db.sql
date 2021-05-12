CREATE OR REPLACE VIEW request_hierarchy_v
            (r_id, id, rd_id, parent_rd_id, group_id, parent_group_id, organization_group_id,
             parent_organization_group_id, position_group_id, element_type, change_type, p_name_ru, p_name_en, name_ru,
             name_en, grade_group_id, grade_rule_id, p_grade, p_head_count, head_count, grade, min_salary, max_salary)
AS
WITH full_hierarchy AS
         (
             SELECT
                 fh.*,
                 p.position_name_lang1,
                 p.position_name_lang3,
                 o.organization_name_lang1,
                 o.organization_name_lang3
             FROM (
                 SELECT
                     he.id,
                     he.element_type,
                     he.group_id,
                     he.parent_group_id,
                     he.organization_group_id,
                     NULL::uuid              AS parent_organization_group_id,
                     NULL::uuid              AS position_group_id,
                     NULL::uuid              AS grade_group_id,
                     NULL::uuid              AS grade_rule_id,
                     NULL::character varying AS p_grade,
                     NULL::bigint            AS head_count
                 FROM base_hierarchy_element he
                 WHERE he.delete_ts IS NULL
                   AND he.element_type = 1
                   AND NOW() >= he.start_date
                   AND NOW() <= he.end_date
                 UNION
                 SELECT
                     NULL::uuid,
                     2                             AS element_type,
                     NULL::uuid                    AS group_id,
                     he.group_id                   AS parent_group_id,
                     p_1.organization_group_ext_id AS organization_group_id,
                     p_1.organization_group_ext_id AS parent_organization_group_id,
                     p_1.group_id                  AS position_group_id,
                     p_1.grade_group_id,
                     p_1.grade_rule_id,
                     g_1.grade_name                AS p_grade,
                     (SELECT
                          COUNT(*) AS count
                      FROM base_assignment a
                      WHERE a.position_group_id = p_1.group_id
                        AND a.organization_group_id = p_1.organization_group_ext_id
                        AND a.delete_ts IS NULL
                        AND NOW() >= a.start_date
                        AND NOW() <= a.end_date)   AS head_count
                 FROM base_position p_1
                 JOIN base_hierarchy_element he
                      ON he.delete_ts IS NULL AND NOW() >= he.start_date AND NOW() <= he.end_date AND
                         he.element_type = 1 AND he.organization_group_id = p_1.organization_group_ext_id
                 LEFT JOIN tsadv_grade g_1
                           ON g_1.group_id = p_1.grade_group_id AND g_1.delete_ts IS NULL AND
                              NOW() >= g_1.start_date AND NOW() <= g_1.end_date
                 WHERE p_1.delete_ts IS NULL
                   AND NOW() >= p_1.start_date
                   AND NOW() <= p_1.end_date) fh
             LEFT JOIN base_organization o
                       ON o.delete_ts IS NULL
                           AND NOW() >= o.start_date
                           AND NOW() <= o.end_date
                           AND o.group_id = fh.organization_group_id
             LEFT JOIN base_position p
                       ON p.delete_ts IS NULL
                           AND NOW() >= p.start_date
                           AND NOW() <= p.end_date
                           AND p.group_id = fh.position_group_id
         )
SELECT
    t.r_id,
    t.id,
    t.rd_id,
    t.parent_rd_id,
    t.group_id,
    t.parent_group_id,
    t.organization_group_id,
    t.parent_organization_group_id,
    t.position_group_id,
    t.element_type,
    t.change_type,
    t.p_name_ru,
    t.p_name_en,
    t.name_ru,
    t.name_en,
    t.grade_group_id,
    t.grade_rule_id,
    t.p_grade,
    t.p_head_count,
    t.head_count AS head_count,
    g.grade_name AS grade,
    grv.min_     AS min_salary,
    grv.max_     AS max_salary
FROM (SELECT
          rd.org_structure_request_id                                   AS r_id,
          fh.id,
          rd.id                                                         AS rd_id,
          rd.parent_id                                                  AS parent_rd_id,
          fh.group_id,
          COALESCE(rd.parent_id, fh.parent_group_id)                    AS parent_group_id,
          fh.organization_group_id,
          fh.parent_organization_group_id,
          fh.position_group_id,
          fh.element_type,
          rd.change_type,
          COALESCE(fh.position_name_lang1, rd.position_name_ru,
                   fh.organization_name_lang1, rd.organization_name_ru) AS p_name_ru,
          COALESCE(fh.position_name_lang3, rd.position_name_en,
                   fh.organization_name_lang3, rd.organization_name_en) AS p_name_en,
          COALESCE(rd.position_name_ru, fh.position_name_lang1,
                   rd.organization_name_ru, fh.organization_name_lang1) AS name_ru,
          COALESCE(rd.position_name_en, fh.position_name_lang3,
                   rd.organization_name_en, fh.organization_name_lang3) AS name_en,
          COALESCE(rd.grade_group_id, fh.grade_group_id)                AS grade_group_id,
          fh.grade_rule_id,
          fh.p_grade,
          COALESCE(fh.head_count::numeric, rd.head_count)               AS p_head_count,
          COALESCE(rd.head_count, fh.head_count::numeric)               AS head_count
      FROM full_hierarchy fh
               LEFT JOIN tsadv_org_structure_request_detail rd
                         ON rd.delete_ts IS NULL
                             AND (rd.change_type::text =
                        ANY (ARRAY ['EDIT'::character varying, 'CLOSE'::character varying]::text[]))
                             AND rd.element_type = fh.element_type
                             AND 1 =
                                 (CASE
                                      WHEN rd.element_type = 1
                                          AND rd.organization_group_id = fh.organization_group_id
                                          THEN 1
                                      WHEN rd.element_type = 2
                                          AND rd.position_group_id = fh.position_group_id
                                          THEN 1
                                      ELSE 0
                                     END)
      UNION
      SELECT
          NULL                                                         AS r_id,
          fh.id,
          NULL                                                         AS rd_id,
          NULL                                                         AS parent_rd_id,
          fh.group_id,
          fh.parent_group_id                                           AS parent_group_id,
          fh.organization_group_id,
          fh.parent_organization_group_id,
          fh.position_group_id,
          fh.element_type,
          NULL::text,
          COALESCE(fh.position_name_lang1, fh.organization_name_lang1) AS p_name_ru,
          COALESCE(fh.position_name_lang3, fh.organization_name_lang3) AS p_name_en,
          COALESCE(fh.position_name_lang1, fh.organization_name_lang1) AS name_ru,
          COALESCE(fh.position_name_lang3, fh.organization_name_lang3) AS name_en,
          fh.grade_group_id                                            AS grade_group_id,
          fh.grade_rule_id,
          fh.p_grade,
          fh.head_count::numeric                                       AS p_head_count,
          fh.head_count::numeric                                       AS head_count
      FROM full_hierarchy fh
      UNION
      SELECT
          rd.org_structure_request_id                            AS r_id,
          NULL::uuid                                             AS id,
          rd.id                                                  AS rd_id,
          rd.parent_id                                           AS parent_rd_id,
          rd.id                                                  AS group_id,
          COALESCE(rd.parent_id, he.group_id)                    AS parent_group_id,
          NULL::uuid                                             AS organization_group_id,
          rd.parent_organization_group_id,
          NULL::uuid                                             AS position_group_id,
          rd.element_type,
          rd.change_type,
          NULL::character varying                                AS p_name_ru,
          NULL::character varying                                AS p_name_en,
          COALESCE(rd.position_name_ru, rd.organization_name_ru) AS name_ru,
          COALESCE(rd.position_name_en, rd.organization_name_en) AS name_en,
          rd.grade_group_id,
          NULL::uuid                                             AS grade_rule_id,
          NULL::character varying                                AS p_grade_name,
          0                                                      AS p_head_count,
          rd.head_count
      FROM tsadv_org_structure_request_detail rd
          LEFT JOIN base_hierarchy_element he
      ON he.delete_ts IS NULL AND NOW() >= he.start_date AND NOW() <= he.end_date AND
          he.organization_group_id = rd.parent_organization_group_id AND he.element_type = 1
      WHERE rd.delete_ts IS NULL
        AND (rd.change_type::text = 'NEW'::text OR rd.change_type::text = 'CLOSE'::text)) t
         LEFT JOIN tsadv_grade g
                   ON t.element_type = 2 AND g.group_id = t.grade_group_id AND g.delete_ts IS NULL AND
                      NOW() >= g.start_date AND NOW() <= g.end_date
         LEFT JOIN tsadv_grade_rule_value grv
                   ON t.element_type = 2 AND grv.grade_rule_id = t.grade_rule_id AND
                      grv.grade_group_id = t.grade_group_id AND grv.delete_ts IS NULL AND
                      NOW() >= grv.start_date AND NOW() <= grv.end_date;

ALTER TABLE request_hierarchy_v
    OWNER TO tal;


CREATE OR REPLACE FUNCTION request_tree_data(p_person_group_id uuid, p_request_id uuid)
    RETURNS json
    LANGUAGE plpgsql
AS
$$
DECLARE
l_hierarchy_element_group_id uuid;
    l_json_data                  json;
    l_root_changed               boolean := FALSE;
BEGIN
SELECT
    he.group_id
INTO l_hierarchy_element_group_id
FROM base_hierarchy_element he
WHERE he.delete_ts IS NULL
  AND NOW() BETWEEN he.start_date AND he.end_date
  AND he.organization_group_id =
      (
          SELECT
              a.organization_group_id
          FROM base_assignment a
          WHERE a.person_group_id = p_person_group_id
            AND a.delete_ts IS NULL
            AND NOW() BETWEEN a.start_date AND a.end_date
      );

SELECT
    (CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END)
INTO l_root_changed
FROM request_hierarchy_v h
WHERE h.group_id = l_hierarchy_element_group_id
  AND h.r_id = p_request_id;

WITH RECURSIVE rec AS (
    SELECT
        h.id,
        h.group_id,
        h.parent_group_id,
        h.organization_group_id,
        h.parent_organization_group_id,
        h.position_group_id,
        h.r_id,
        h.rd_id,
        h.parent_rd_id,
        h.element_type,
        h.change_type,
        h.p_name_ru,
        h.p_name_en,
        h.name_ru,
        h.name_en,
        h.grade_group_id,
        h.p_grade,
        h.grade,
        h.p_head_count,
        h.head_count,
        h.min_salary,
        h.max_salary,
        TRUE root
    FROM request_hierarchy_v h
    WHERE h.group_id = l_hierarchy_element_group_id
      AND (CASE
               WHEN l_root_changed = TRUE
                   THEN h.r_id = p_request_id
               ELSE (h.r_id IS NULL OR h.r_id = p_request_id)
        END)
    UNION
    SELECT
        h.id,
        h.group_id,
        h.parent_group_id,
        h.organization_group_id,
        h.parent_organization_group_id,
        h.position_group_id,
        h.r_id,
        h.rd_id,
        r.rd_id,
        h.element_type,
        h.change_type,
        h.p_name_ru,
        h.p_name_en,
        h.name_ru,
        h.name_en,
        h.grade_group_id,
        h.p_grade,
        h.grade,
        h.p_head_count,
        h.head_count,
        h.min_salary,
        h.max_salary,
        FALSE
    FROM rec r
             JOIN LATERAL (SELECT
                               h.*,
                               (CASE
                                    WHEN h.change_type = 'NEW' THEN 0
                                    ELSE (COUNT(COALESCE(h.group_id, h.position_group_id))
                                          OVER (PARTITION BY COALESCE(h.group_id, h.position_group_id))) END) duplicate
                           FROM request_hierarchy_v h
                           WHERE (h.parent_group_id = r.group_id OR h.parent_rd_id = r.rd_id)
                             AND (h.r_id = p_request_id OR h.r_id IS NULL)
        ) h
                  ON (CASE
                          WHEN h.duplicate = 0 OR h.duplicate > 1
                              THEN h.r_id = p_request_id
                          WHEN h.duplicate = 1 AND h.change_type = 'CLOSE'
                              THEN h.r_id = p_request_id
                          ELSE (h.r_id IS NULL OR h.r_id = p_request_id) END)
)
SELECT
    JSON_AGG(JSON_BUILD_OBJECT
        (
            'rId', r.r_id,
            'rdId', r.rd_id,
            'pRdId', r.parent_rd_id,
            'groupId', r.group_id,
            'pGroupId', r.parent_group_id,
            'rowKey', COALESCE(r.rd_id, r.position_group_id, r.organization_group_id),
            'elementType', r.element_type,
            'changeType', r.change_type,
            'nameRu', ARRAY [r.p_name_ru,r.name_ru,r.p_name_ru],
            'nameEn', ARRAY [r.p_name_en,r.name_en,r.p_name_en],
            'gradeGroupId', r.grade_group_id,
            'grade', ARRAY [r.p_grade,r.grade,NULL],
            'headCount', ARRAY [r.p_head_count,r.head_count,ABS(r.p_head_count - r.head_count)],
            'baseSalary', ARRAY [r.min_salary,r.min_salary,r.min_salary],
            'mtPayrollPer', ARRAY [r.max_salary,r.max_salary,r.max_salary],
            'mtPayroll',
            ARRAY [r.max_salary * r.p_head_count,r.max_salary * r.head_count,ABS(r.max_salary * (r.p_head_count - r.head_count))],
            'pOrgGroupId', r.parent_organization_group_id,
            'orgGroupId', r.organization_group_id,
            'posGroupId', r.position_group_id,
            'root', r.root
        ))
INTO l_json_data
FROM (
         SELECT
             r.*
         FROM rec r
         GROUP BY r.id,
                  r.r_id,
                  r.rd_id,
                  r.parent_rd_id,
                  r.group_id,
                  r.parent_group_id,
                  r.element_type,
                  r.change_type,
                  r.p_name_ru,
                  r.p_name_en,
                  r.name_ru,
                  r.name_en,
                  r.parent_organization_group_id,
                  r.organization_group_id,
                  r.position_group_id,
                  r.grade_group_id,
                  r.p_grade,
                  r.grade,
                  r.min_salary,
                  r.max_salary,
                  r.p_head_count,
                  r.head_count,
                  r.root
     ) r;
RETURN l_json_data;
END ;
$$;