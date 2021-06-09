create or replace view request_hierarchy_v(r_id, id, rd_id, parent_rd_id, group_id, parent_group_id, organization_group_id, parent_organization_group_id, position_group_id, element_type, change_type, p_name_ru, p_name_en, name_ru, name_en, grade_group_id, grade_rule_id, p_grade, p_head_count, head_count, grade, p_min_salary, p_max_salary, min_salary, max_salary) as
	WITH full_hierarchy AS (
    SELECT fh.id,
           fh.element_type,
           fh.group_id,
           fh.parent_group_id,
           fh.organization_group_id,
           fh.parent_organization_group_id,
           fh.position_group_id,
           fh.grade_group_id,
           fh.grade_rule_id,
           fh.p_grade,
           fh.head_count,
           fh.max_salary,
           fh.min_salary,
           p.position_name_lang1,
           p.position_name_lang3,
           o.organization_name_lang1,
           o.organization_name_lang3
    FROM (SELECT he.id,
                 he.element_type,
                 he.group_id,
                 he.parent_group_id,
                 he.organization_group_id,
                 NULL::uuid              AS parent_organization_group_id,
                 NULL::uuid              AS position_group_id,
                 NULL::uuid              AS grade_group_id,
                 NULL::uuid              AS grade_rule_id,
                 NULL::character varying AS p_grade,
                 0::bigint               AS head_count,
                 0::bigint               AS max_salary,
                 0::bigint               AS min_salary
          FROM base_hierarchy_element he
          WHERE he.delete_ts IS NULL
            AND he.element_type = 1
            AND now() >= he.start_date
            AND now() <= he.end_date
          UNION
          SELECT NULL::uuid                              AS uuid,
                 2                                       AS element_type,
                 NULL::uuid                              AS group_id,
                 he.group_id                             AS parent_group_id,
                 p_1.organization_group_ext_id           AS organization_group_id,
                 p_1.organization_group_ext_id           AS parent_organization_group_id,
                 p_1.group_id                            AS position_group_id,
                 p_1.grade_group_id,
                 p_1.grade_rule_id,
                 g_1.grade_name                          AS p_grade,
                 (SELECT count(*) AS count
                  FROM base_assignment a
                  WHERE a.position_group_id = p_1.group_id
                    AND a.organization_group_id = p_1.organization_group_ext_id
                    AND a.delete_ts IS NULL
                    AND now() >= a.start_date
                    AND now() <= a.end_date)             AS head_count,
                 COALESCE(grv.max_, 0::double precision) AS max_salary,
                 COALESCE(grv.min_, 0::double precision) AS min_salary
          FROM base_position p_1
                   JOIN base_hierarchy_element he
                        ON he.delete_ts IS NULL AND now() >= he.start_date AND now() <= he.end_date AND
                           he.element_type = 1 AND he.organization_group_id = p_1.organization_group_ext_id
                   LEFT JOIN tsadv_grade g_1 ON g_1.group_id = p_1.grade_group_id AND g_1.delete_ts IS NULL AND
                                                now() >= g_1.start_date AND now() <= g_1.end_date
                   LEFT JOIN tsadv_grade_rule_value grv
                             ON grv.grade_group_id = p_1.grade_group_id AND grv.delete_ts IS NULL AND
                                now() >= grv.start_date AND now() <= grv.end_date
          WHERE p_1.delete_ts IS NULL
            AND now() >= p_1.start_date
            AND now() <= p_1.end_date) fh
             LEFT JOIN base_organization o ON o.delete_ts IS NULL AND now() >= o.start_date AND now() <= o.end_date AND
                                              o.group_id = fh.organization_group_id
             LEFT JOIN base_position p ON p.delete_ts IS NULL AND now() >= p.start_date AND now() <= p.end_date AND
                                          p.group_id = fh.position_group_id
)
SELECT t.r_id,
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
       t.head_count,
       g.grade_name AS grade,
       t.p_min_salary,
       t.p_max_salary,
       t.min_salary,
       t.max_salary
FROM (SELECT rd.org_structure_request_id                     AS r_id,
             fh.id,
             rd.id                                           AS rd_id,
             rd.parent_id                                    AS parent_rd_id,
             fh.group_id,
             COALESCE(rd.parent_id, fh.parent_group_id)      AS parent_group_id,
             fh.organization_group_id,
             fh.parent_organization_group_id,
             fh.position_group_id,
             fh.element_type,
             rd.change_type,
             COALESCE(fh.position_name_lang1, rd.position_name_ru, fh.organization_name_lang1,
                      rd.organization_name_ru)               AS p_name_ru,
             COALESCE(fh.position_name_lang3, rd.position_name_en, fh.organization_name_lang3,
                      rd.organization_name_en)               AS p_name_en,
             COALESCE(rd.position_name_ru, fh.position_name_lang1, rd.organization_name_ru,
                      fh.organization_name_lang1)            AS name_ru,
             COALESCE(rd.position_name_en, fh.position_name_lang3, rd.organization_name_en,
                      fh.organization_name_lang3)            AS name_en,
             COALESCE(rd.grade_group_id, fh.grade_group_id)  AS grade_group_id,
             fh.grade_rule_id,
             fh.p_grade,
             COALESCE(fh.head_count::numeric, rd.head_count) AS p_head_count,
             COALESCE(rd.head_count, fh.head_count::numeric) AS head_count,
             COALESCE(fh.max_salary::numeric, rd.max_salary) AS p_max_salary,
             COALESCE(rd.max_salary, fh.max_salary::numeric) AS max_salary,
             COALESCE(fh.min_salary::numeric, rd.min_salary) AS p_min_salary,
             COALESCE(rd.min_salary, fh.min_salary::numeric) AS min_salary
      FROM full_hierarchy fh
               LEFT JOIN tsadv_org_structure_request_detail rd ON rd.delete_ts IS NULL AND (rd.change_type::text = ANY
                                                                                            (ARRAY ['EDIT'::character varying::text, 'CLOSE'::character varying::text])) AND
                                                                  rd.element_type = fh.element_type AND 1 =
                                                                                                        CASE
                                                                                                            WHEN rd.element_type = 1 AND
                                                                                                                 rd.organization_group_id = fh.organization_group_id
                                                                                                                THEN 1
                                                                                                            WHEN rd.element_type = 2 AND rd.position_group_id = fh.position_group_id
                                                                                                                THEN 1
                                                                                                            ELSE 0
                                                                                                            END
      UNION
      SELECT NULL::uuid                                                   AS r_id,
             fh.id,
             NULL::uuid                                                   AS rd_id,
             NULL::uuid                                                   AS parent_rd_id,
             fh.group_id,
             fh.parent_group_id,
             fh.organization_group_id,
             fh.parent_organization_group_id,
             fh.position_group_id,
             fh.element_type,
             NULL::text                                                   AS text,
             COALESCE(fh.position_name_lang1, fh.organization_name_lang1) AS p_name_ru,
             COALESCE(fh.position_name_lang3, fh.organization_name_lang3) AS p_name_en,
             COALESCE(fh.position_name_lang1, fh.organization_name_lang1) AS name_ru,
             COALESCE(fh.position_name_lang3, fh.organization_name_lang3) AS name_en,
             fh.grade_group_id,
             fh.grade_rule_id,
             fh.p_grade,
             fh.head_count::numeric                                       AS p_head_count,
             fh.head_count::numeric                                       AS head_count,
             fh.max_salary::numeric                                       AS p_max_salary,
             fh.max_salary::numeric                                       AS max_salary,
             fh.min_salary::numeric                                       AS p_min_salary,
             fh.min_salary::numeric                                       AS min_salary
      FROM full_hierarchy fh
      UNION
      SELECT rd.org_structure_request_id                            AS r_id,
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
             rd.head_count,
             0                                                      AS p_max_salary,
             rd.max_salary,
             0                                                      AS p_min_salary,
             rd.min_salary
      FROM tsadv_org_structure_request_detail rd
               LEFT JOIN base_hierarchy_element he
                         ON he.delete_ts IS NULL AND now() >= he.start_date AND now() <= he.end_date AND
                            he.organization_group_id = rd.parent_organization_group_id AND he.element_type = 1
      WHERE rd.delete_ts IS NULL
        AND (rd.change_type::text = 'NEW'::text OR rd.change_type::text = 'CLOSE'::text)) t
         LEFT JOIN tsadv_grade g ON t.element_type = 2 AND g.group_id = t.grade_group_id AND g.delete_ts IS NULL AND
                                    now() >= g.start_date AND now() <= g.end_date;

alter table request_hierarchy_v owner to tal;

