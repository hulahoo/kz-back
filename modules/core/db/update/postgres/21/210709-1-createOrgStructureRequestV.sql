create or replace function request_tree_data(p_person_group_id uuid, p_request_id uuid) returns json
    language plpgsql
as $$
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
                and a.primary_flag is true
                AND (select s.request_date
                     from tsadv_org_structure_request s
                     where s.id = p_request_id) BETWEEN a.start_date AND a.end_date
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
            h.p_min_salary,
            h.p_max_salary,
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
            h.p_min_salary,
            h.p_max_salary,
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
                'nameRu', ARRAY [r.p_name_ru,r.name_ru,coalesce(r.p_name_ru, r.name_ru)],
                'nameEn', ARRAY [r.p_name_en,r.name_en,coalesce(r.p_name_en, r.name_en)],
                'gradeGroupId', r.grade_group_id,
                'grade', ARRAY [r.p_grade,r.grade,r.grade],
                'headCount', ARRAY [r.p_head_count,r.head_count,(r.head_count - r.p_head_count)],
                'baseSalary', ARRAY [r.p_min_salary,r.min_salary,(r.min_salary - r.p_min_salary)],
                'mtPayrollPer', ARRAY [r.p_max_salary,r.max_salary,(r.max_salary - r.p_max_salary)],
                'mtPayroll',
                ARRAY [r.p_max_salary * r.p_head_count,r.max_salary * r.head_count, (r.max_salary * r.head_count - r.p_max_salary * r.p_head_count)],
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
                      r.p_min_salary,
                      r.p_max_salary,
                      r.p_head_count,
                      r.head_count,
                      r.root
         ) r;
    RETURN l_json_data;
END ;
$$;

alter function request_tree_data(uuid, uuid) owner to tal;
