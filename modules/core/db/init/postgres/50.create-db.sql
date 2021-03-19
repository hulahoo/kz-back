
-- hierarchy functions
-- the last version
create or replace function get_child_hierarchy_element(hierarchy_id uuid, parent_group_id uuid, system_date date)
  returns TABLE
          (
            id       uuid,
            hasChild boolean
          )
  language plpgsql
as
$$
Begin
  return query execute
    'with hierarchy as (
  select el.*
  from base_hierarchy_element el
         left join get_hierarchy_exception($3) e
                   on e.id = el.id
  where e is null
    and el.hierarchy_id = $1
    and $3 between el.start_date and el.end_date
    and el.delete_ts is null
),
     hierarchy_child as (
       select hierarchy.parent_group_id
       from hierarchy
       group by hierarchy.parent_group_id
     )
select el.id, child.parent_group_id is not null as hasChild
from hierarchy el
       left join hierarchy_child child on child.parent_group_id = el.group_id
where case when $2 is null then el.parent_group_id is null else el.parent_group_id = $2 end'
    using hierarchy_id,parent_group_id,system_date;
Exception
  When Others Then
    RAISE NOTICE 'Error: %', 'Error get_child_hierarchy_element';
    RAISE;
END;
$$;

alter function get_child_hierarchy_element(uuid,uuid,date) owner to tal;

create or replace function search_hierarchy_element(hierarchy_id uuid, search_text text, system_date date)
  returns TABLE
          (
            id          uuid,
            group_id    uuid,
            parent_path text,
            child_path  text
          )
  language plpgsql
as
$$
Begin
  return query execute
    'with recursive hierarchy_parent as (
  with hierarchy as (
    select el.*
    from base_hierarchy_element el
           left join get_hierarchy_exception($3) e
                     on e.id = el.id
      where e is null
           and el.hierarchy_id = $1
           and $3 between el.start_date and el.end_date
           and el.delete_ts is null
  )
  select hierarchy.*,
         hierarchy.id::text as path
  from hierarchy
    where
       hierarchy.parent_group_id is null
    union
    select
       h.*,
       hp.path || ''|'' || h.id::text as path
    from
       hierarchy_parent hp
         join hierarchy h
              on h.parent_group_id = hp.group_id
),
               hierarchy as (
                 select el.*
                 from base_hierarchy_element el
                        left join get_hierarchy_exception($3) e
                                  on e.id = el.id
                   where e is null
                        and el.hierarchy_id = $1
                        and $3 between el.start_date and el.end_date
                        and el.delete_ts is null
               ),
               children as (
                 select hierarchy.parent_group_id, string_agg(hierarchy.id::text, ''|'') as child_ids
                 from hierarchy
                   group by hierarchy.parent_group_id
               )
select el.id, el.group_id, el.path, c.child_ids
from hierarchy_parent el
       left join children c on c.parent_group_id = el.group_id
       left join base_position p
                 on p.group_id = el.position_group_id
                   and $3 between p.start_date and p.end_date
                   and p.delete_ts is null
       left join base_organization o
                 on o.group_id = el.organization_group_id
                   and $3 between o.start_date and o.end_date
                   and o.delete_ts is null
  where o.id is not null
       and ( lower(o.organization_name_lang1) ~ $2
or lower(o.organization_name_lang2) ~ $2
or lower(o.organization_name_lang3) ~ $2)
   or p.id is not null
and (lower(p.position_full_name_lang1) ~ $2
  or lower(p.position_full_name_lang2) ~ $2
  or lower(p.position_full_name_lang3) ~ $2)'
    using hierarchy_id,lower(search_text),system_date;
Exception
  When Others Then
    RAISE NOTICE 'Error: %', 'Error search_hierarchy_element';
    RAISE;
END;
$$;

alter function search_hierarchy_element(uuid,text,date) owner to tal;


create or replace function get_hierarchy_exception(system_date date) returns TABLE(id uuid)
	language plpgsql
as $$
Begin
  return query execute
    ' with recursive org as (
                    with postOrg as (
                      select o.group_id                                 as organization_group_ext_id,
                             string_agg(tdps.code, '' '') is not null and string_agg(tdps.code, '' '') like ''%ACTIVE%'' as is_active
                      from base_organization o
                             left join base_position p
                                       on o.group_id = p.organization_group_ext_id
                                         and $1 between p.start_date and p.end_date
                                         and p.delete_ts is null
                             left join tsadv_dic_position_status tdps
                                       on p.position_status_id = tdps.id
                                         and tdps.delete_ts is null
                      where $1 between o.start_date and o.end_date
                        and o.delete_ts is null
                      group by o.group_id
                      )
                      select el.id, el.group_id, el.organization_group_id, p.is_active as is_has_active_child
                      from postOrg p
                             join base_hierarchy_element el
                                  on p.organization_group_ext_id = el.organization_group_id
                                    and $1 between el.start_date and el.end_date
                                    and el.delete_ts is null
                                    and el.element_type = 1
                      where p.is_active is false
                      union
                      select el.id, el.group_id, o.organization_group_id, (o.is_has_active_child or p.is_active) as is_has_active_child
                      from org o
                             join base_hierarchy_element el
                                  on el.parent_group_id = o.group_id
                                    and $1 between el.start_date and el.end_date
                                    and el.element_type = 1
                                    and el.delete_ts is null
                             join postOrg p
                                  on p.organization_group_ext_id = el.organization_group_id
                  ),
                                 closedOrg as (
                                   select o.group_id                                 as organization_group_ext_id,
                                          string_agg(tdps.code, '' '') is not null and string_agg(tdps.code, '' '') like ''%ACTIVE%'' as is_active
                                   from base_organization o
                                          left join base_position p
                                                    on o.group_id = p.organization_group_ext_id
                                                      and $1 between p.start_date and p.end_date
                                                      and p.delete_ts is null
                                          left join tsadv_dic_position_status tdps
                                                    on p.position_status_id = tdps.id
                                                      and tdps.delete_ts is null
                                   where $1 between o.start_date and o.end_date
                                     and o.delete_ts is null
                                   group by o.group_id
                                   having string_agg(tdps.code, '' '') like ''%ACTIVE%'' is false
											or string_agg(tdps.code, '' '') is null
                                 ),
                                 closedOrgWithChild as (
                                   select o.organization_group_id
                                   from org o
                                   where o.is_has_active_child = true
                                   group by o.organization_group_id
                                 )
                  select el.id
                  from closedOrg co
                         join base_hierarchy_element el
                             on el.organization_group_id = co.organization_group_ext_id
                             and el.delete_ts is null
                             and $1 between el.start_date and el.end_date
                         left join closedOrgWithChild cowc
                                   on co.organization_group_ext_id = cowc.organization_group_id
                  where cowc is null '
    using system_date;
Exception
  When Others Then
    RAISE NOTICE 'Error: %', 'Error get_hierarchy_exception';
    RAISE;
END;
$$;

alter function get_hierarchy_exception(date) owner to tal;

create or replace view request_hierarchy_v
            (r_id, id, rd_id, parent_rd_id, group_id, parent_group_id, organization_group_id,
             parent_organization_group_id, position_group_id, element_type, change_type, p_name_ru, p_name_en, name_ru,
             name_en, grade_group_id, grade_rule_id, p_grade, p_head_count, head_count, grade, min_salary, max_salary)
as
with full_hierarchy as
         (
             select fh.*,
                    p.position_name_lang1,
                    p.position_name_lang3,
                    o.organization_name_lang1,
                    o.organization_name_lang3
             from (
                      SELECT he.id,
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
                        AND now() >= he.start_date
                        AND now() <= he.end_date
                      UNION
                      SELECT NULL::uuid,
                             2                             AS element_type,
                             NULL::uuid                    AS group_id,
                             he.group_id                   AS parent_group_id,
                             p_1.organization_group_ext_id AS organization_group_id,
                             p_1.organization_group_ext_id AS parent_organization_group_id,
                             p_1.group_id                  AS position_group_id,
                             p_1.grade_group_id,
                             p_1.grade_rule_id,
                             g_1.grade_name                AS p_grade,
                             (SELECT count(*) AS count
                              FROM base_assignment a
                              WHERE a.position_group_id = p_1.group_id
                                AND a.organization_group_id = p_1.organization_group_ext_id
                                AND a.delete_ts IS NULL
                                AND now() >= a.start_date
                                AND now() <= a.end_date)   AS head_count
                      FROM base_position p_1
                               JOIN base_hierarchy_element he
                                    ON he.delete_ts IS NULL AND now() >= he.start_date AND now() <= he.end_date AND
                                       he.element_type = 1 AND he.organization_group_id = p_1.organization_group_ext_id
                               LEFT JOIN tsadv_grade g_1
                                         ON g_1.group_id = p_1.grade_group_id AND g_1.delete_ts IS NULL AND
                                            now() >= g_1.start_date AND now() <= g_1.end_date
                      WHERE p_1.delete_ts IS NULL
                        AND now() >= p_1.start_date
                        AND now() <= p_1.end_date) fh
                      LEFT JOIN base_organization o
                                ON o.delete_ts IS NULL
                                    AND now() >= o.start_date
                                    AND now() <= o.end_date
                                    AND o.group_id = fh.organization_group_id
                      LEFT JOIN base_position p
                                ON p.delete_ts IS NULL
                                    AND now() >= p.start_date
                                    AND now() <= p.end_date
                                    AND p.group_id = fh.position_group_id
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
       grv.min_     AS min_salary,
       grv.max_     AS max_salary
FROM (SELECT rd.org_structure_request_id                                   AS r_id,
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
      select null                                                         AS r_id,
             fh.id,
             null                                                         AS rd_id,
             null                                                         AS parent_rd_id,
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
      from full_hierarchy fh
      union
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
             COALESCE(rd.position_name_ru, rd.organization_name_ru) AS p_name_ru,
             COALESCE(rd.position_name_en, rd.organization_name_en) AS p_name_en,
             COALESCE(rd.position_name_ru, rd.organization_name_ru) AS name_ru,
             COALESCE(rd.position_name_en, rd.organization_name_en) AS name_en,
             rd.grade_group_id,
             NULL::uuid                                             AS grade_rule_id,
             NULL::character varying                                AS p_grade_name,
             0                                                      AS p_head_count,
             rd.head_count
      FROM tsadv_org_structure_request_detail rd
          LEFT JOIN base_hierarchy_element he
      ON he.delete_ts IS NULL AND now() >= he.start_date AND now() <= he.end_date AND
          he.organization_group_id = rd.parent_organization_group_id AND he.element_type = 1
      WHERE rd.delete_ts IS NULL
        AND rd.change_type::text = 'NEW'::text) t
         LEFT JOIN tsadv_grade g ON t.element_type = 2 AND g.group_id = t.grade_group_id AND g.delete_ts IS NULL AND
                                    now() >= g.start_date AND now() <= g.end_date
         LEFT JOIN tsadv_grade_rule_value grv ON t.element_type = 2 AND grv.grade_rule_id = t.grade_rule_id AND
                                                 grv.grade_group_id = t.grade_group_id AND grv.delete_ts IS NULL AND
                                                 now() >= grv.start_date AND now() <= grv.end_date;

CREATE OR REPLACE FUNCTION request_tree_data(p_person_group_id uuid, p_request_id uuid)
    RETURNS json
    LANGUAGE plpgsql
AS
$$
DECLARE
l_hierarchy_element_group_id uuid;
    l_json_data                  json;
    l_root_changed               boolean := false;
BEGIN
SELECT he.group_id
INTO l_hierarchy_element_group_id
FROM base_hierarchy_element he
WHERE he.delete_ts IS NULL
  AND NOW() BETWEEN he.start_date AND he.end_date
  AND he.organization_group_id =
      (
          SELECT a.organization_group_id
          FROM base_assignment a
          WHERE a.person_group_id = p_person_group_id
            AND a.delete_ts IS NULL
            AND NOW() BETWEEN a.start_date AND a.end_date
      );

select (case when count(*) > 0 then true else false end)
into l_root_changed
from request_hierarchy_v h
where h.group_id = l_hierarchy_element_group_id
  and h.r_id = p_request_id;

WITH RECURSIVE rec AS (
    SELECT h.id,
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
      and (case
               when l_root_changed = true
                   then h.r_id = p_request_id
               else h.r_id is null
        end)
    UNION
    SELECT h.id,
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
             JOIN request_hierarchy_v h
                  ON (h.parent_group_id = r.group_id OR h.parent_rd_id = r.rd_id)
                      and (h.r_id = p_request_id or h.r_id is null)
)
SELECT JSON_AGG(JSON_BUILD_OBJECT
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
         SELECT r.*
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


