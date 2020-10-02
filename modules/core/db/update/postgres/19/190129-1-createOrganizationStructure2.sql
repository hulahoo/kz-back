create table tsadv_organization_structure2  as (select * from tsadv_organization_structure );
drop materialized view if exists tsadv_organization_structure cascade;
alter table tsadv_organization_structure2 rename to tsadv_organization_structure;
CREATE OR REPLACE VIEW tal.xx_org_structure_v
AS SELECT org_str.organization_group_id,
    o.organization_name_lang1 AS org_name,
    org_str.path AS org_path,
    org_str.level,
    o.legacy_id,
    dot.lang_value1,
    org_str.path_org_name1,
    oof.filial_id,
    oof.filial_name,
    oof2.filial_id2,
    oof2.filial_name2,
    ooc.complex_id,
    ooc.complex_name,
    oop.predpriatie_id,
    oop.predpriatie_name,
    oop2.predpriatie_id2,
    oop2.predpriatie_name2,
    ood.department_id,
    ood.department_name,
    ooo.otdel_ceh_id,
    ooo.otdel_ceh_name,
    ol1.level1_org_name,
    ol2.level2_org_name,
    ol3.level3_org_name,
    ol4.level4_org_name,
    ol5.level5_org_name,
    ol6.level6_org_name,
    ol7.level7_org_name,
    ol8.level8_org_name,
    ol9.level9_org_name,
    ol10.level10_org_name,
    ol11.level11_org_name,
    ol12.level12_org_name,
    ol13.level13_org_name,
    ol14.level14_org_name,
    ol15.level15_org_name,
    NULL::text AS create_ts,
    NULL::text AS created_by,
    NULL::text AS update_ts,
    NULL::text AS updated_by,
    NULL::text AS delete_ts,
    NULL::text AS deleted_by,
    NULL::text AS version
   FROM base_organization o
     JOIN tsadv_organization_structure org_str ON org_str.path::text ~~ (('%'::text || o.group_id) || '%'::text) OR org_str.organization_group_id = o.group_id
     JOIN base_hierarchy h ON h.id = org_str.hierarchy_id AND h.primary_flag IS TRUE
     LEFT JOIN base_dic_org_type dot ON dot.id = o.type_id AND dot.delete_ts IS NULL
     LEFT JOIN LATERAL ( SELECT oof_1.group_id AS filial_id,
            oof_1.organization_name_lang1 AS filial_name,
            org_str_f.level
           FROM base_organization oof_1
             JOIN base_dic_org_type dot_1 ON dot_1.id = oof_1.type_id AND dot_1.delete_ts IS NULL
             JOIN tsadv_organization_structure org_str_f ON org_str_f.organization_group_id = oof_1.group_id
             JOIN base_hierarchy h_1 ON h_1.id = org_str_f.hierarchy_id AND h_1.primary_flag IS TRUE
          WHERE org_str.path::text ~~ (('%'::text || oof_1.group_id) || '%'::text) AND dot_1.code::text = 'FILIAL'::text
          ORDER BY org_str_f.level
         LIMIT 1) oof ON 1 = 1
     LEFT JOIN LATERAL ( SELECT oof_1.group_id AS filial_id2,
            oof_1.organization_name_lang1 AS filial_name2,
            org_str_f.level
           FROM base_organization oof_1
             JOIN base_dic_org_type dot_1 ON dot_1.id = oof_1.type_id AND dot_1.delete_ts IS NULL
             JOIN tsadv_organization_structure org_str_f ON org_str_f.organization_group_id = oof_1.group_id
             JOIN base_hierarchy h_1 ON h_1.id = org_str_f.hierarchy_id AND h_1.primary_flag IS TRUE
          WHERE org_str.path::text ~~ (('%'::text || oof_1.group_id) || '%'::text) AND dot_1.code::text = 'FILIAL'::text
          ORDER BY org_str_f.level
         OFFSET 1
         LIMIT 1) oof2 ON 1 = 1
     LEFT JOIN LATERAL ( SELECT oof_1.group_id AS complex_id,
            oof_1.organization_name_lang1 AS complex_name,
            org_str_f.level
           FROM base_organization oof_1
             JOIN base_dic_org_type dot_1 ON dot_1.id = oof_1.type_id AND dot_1.delete_ts IS NULL
             JOIN tsadv_organization_structure org_str_f ON org_str_f.organization_group_id = oof_1.group_id
             JOIN base_hierarchy h_1 ON h_1.id = org_str_f.hierarchy_id AND h_1.primary_flag IS TRUE
          WHERE org_str.path::text ~~ (('%'::text || oof_1.group_id) || '%'::text) AND dot_1.code::text = 'COMPLEX'::text
          ORDER BY org_str_f.level
         LIMIT 1) ooc ON 1 = 1
     LEFT JOIN LATERAL ( SELECT oof_1.group_id AS predpriatie_id,
            oof_1.organization_name_lang1 AS predpriatie_name,
            org_str_f.level
           FROM base_organization oof_1
             JOIN base_dic_org_type dot_1 ON dot_1.id = oof_1.type_id AND dot_1.delete_ts IS NULL
             JOIN tsadv_organization_structure org_str_f ON org_str_f.organization_group_id = oof_1.group_id
             JOIN base_hierarchy h_1 ON h_1.id = org_str_f.hierarchy_id AND h_1.primary_flag IS TRUE
          WHERE org_str.path::text ~~ (('%'::text || oof_1.group_id) || '%'::text) AND dot_1.code::text = 'PREDPRIATIE'::text
          ORDER BY org_str_f.level
         LIMIT 1) oop ON 1 = 1
     LEFT JOIN LATERAL ( SELECT oof_1.group_id AS predpriatie_id2,
            oof_1.organization_name_lang1 AS predpriatie_name2,
            org_str_f.level
           FROM base_organization oof_1
             JOIN base_dic_org_type dot_1 ON dot_1.id = oof_1.type_id AND dot_1.delete_ts IS NULL
             JOIN tsadv_organization_structure org_str_f ON org_str_f.organization_group_id = oof_1.group_id
             JOIN base_hierarchy h_1 ON h_1.id = org_str_f.hierarchy_id AND h_1.primary_flag IS TRUE
          WHERE org_str.path::text ~~ (('%'::text || oof_1.group_id) || '%'::text) AND dot_1.code::text = 'PREDPRIATIE'::text
          ORDER BY org_str_f.level
         OFFSET 1
         LIMIT 1) oop2 ON 1 = 1
     LEFT JOIN LATERAL ( SELECT oof_1.group_id AS department_id,
            oof_1.organization_name_lang1 AS department_name,
            org_str_f.level
           FROM base_organization oof_1
             JOIN base_dic_org_type dot_1 ON dot_1.id = oof_1.type_id AND dot_1.delete_ts IS NULL
             JOIN tsadv_organization_structure org_str_f ON org_str_f.organization_group_id = oof_1.group_id
             JOIN base_hierarchy h_1 ON h_1.id = org_str_f.hierarchy_id AND h_1.primary_flag IS TRUE
          WHERE org_str.path::text ~~ (('%'::text || oof_1.group_id) || '%'::text) AND dot_1.code::text = 'DEPARTMENT'::text
          ORDER BY org_str_f.level
         LIMIT 1) ood ON 1 = 1
     LEFT JOIN LATERAL ( SELECT oof_1.group_id AS otdel_ceh_id,
            oof_1.organization_name_lang1 AS otdel_ceh_name,
            org_str_f.level
           FROM base_organization oof_1
             JOIN base_dic_org_type dot_1 ON dot_1.id = oof_1.type_id AND dot_1.delete_ts IS NULL
             JOIN tsadv_organization_structure org_str_f ON org_str_f.organization_group_id = oof_1.group_id
             JOIN base_hierarchy h_1 ON h_1.id = org_str_f.hierarchy_id AND h_1.primary_flag IS TRUE
          WHERE org_str.path::text ~~ (('%'::text || oof_1.group_id) || '%'::text) AND (dot_1.code::text = ANY (ARRAY['PLOT'::character varying::text, 'CEH'::character varying::text, 'BERAEU'::character varying::text, 'BRANCH'::character varying::text, 'SERVICE'::character varying::text]))
          ORDER BY org_str_f.level
         LIMIT 1) ooo ON 1 = 1
     LEFT JOIN LATERAL ( SELECT oof_1.group_id AS level1_org_id,
            oof_1.organization_name_lang1 AS level1_org_name
           FROM base_organization oof_1
             JOIN tsadv_organization_structure org_str_f ON org_str_f.organization_group_id = oof_1.group_id
             JOIN base_hierarchy h_1 ON h_1.id = org_str_f.hierarchy_id AND h_1.primary_flag IS TRUE
          WHERE org_str.path::text ~~ (('%'::text || oof_1.group_id) || '%'::text) AND org_str_f.level = 1
         LIMIT 1) ol1 ON 1 = 1
     LEFT JOIN LATERAL ( SELECT oof_1.group_id AS level2_org_id,
            oof_1.organization_name_lang1 AS level2_org_name
           FROM base_organization oof_1
             JOIN tsadv_organization_structure org_str_f ON org_str_f.organization_group_id = oof_1.group_id
             JOIN base_hierarchy h_1 ON h_1.id = org_str_f.hierarchy_id AND h_1.primary_flag IS TRUE
          WHERE org_str.path::text ~~ (('%'::text || oof_1.group_id) || '%'::text) AND org_str_f.level = 2
         LIMIT 1) ol2 ON 1 = 1
     LEFT JOIN LATERAL ( SELECT oof_1.group_id AS level3_org_id,
            oof_1.organization_name_lang1 AS level3_org_name
           FROM base_organization oof_1
             JOIN tsadv_organization_structure org_str_f ON org_str_f.organization_group_id = oof_1.group_id
             JOIN base_hierarchy h_1 ON h_1.id = org_str_f.hierarchy_id AND h_1.primary_flag IS TRUE
          WHERE org_str.path::text ~~ (('%'::text || oof_1.group_id) || '%'::text) AND org_str_f.level = 3
         LIMIT 1) ol3 ON 1 = 1
     LEFT JOIN LATERAL ( SELECT oof_1.group_id AS level4_org_id,
            oof_1.organization_name_lang1 AS level4_org_name
           FROM base_organization oof_1
             JOIN tsadv_organization_structure org_str_f ON org_str_f.organization_group_id = oof_1.group_id
             JOIN base_hierarchy h_1 ON h_1.id = org_str_f.hierarchy_id AND h_1.primary_flag IS TRUE
          WHERE org_str.path::text ~~ (('%'::text || oof_1.group_id) || '%'::text) AND org_str_f.level = 4
         LIMIT 1) ol4 ON 1 = 1
     LEFT JOIN LATERAL ( SELECT oof_1.group_id AS level5_org_id,
            oof_1.organization_name_lang1 AS level5_org_name
           FROM base_organization oof_1
             JOIN tsadv_organization_structure org_str_f ON org_str_f.organization_group_id = oof_1.group_id
             JOIN base_hierarchy h_1 ON h_1.id = org_str_f.hierarchy_id AND h_1.primary_flag IS TRUE
          WHERE org_str.path::text ~~ (('%'::text || oof_1.group_id) || '%'::text) AND org_str_f.level = 5
         LIMIT 1) ol5 ON 1 = 1
     LEFT JOIN LATERAL ( SELECT oof_1.group_id AS level6_org_id,
            oof_1.organization_name_lang1 AS level6_org_name
           FROM base_organization oof_1
             JOIN tsadv_organization_structure org_str_f ON org_str_f.organization_group_id = oof_1.group_id
             JOIN base_hierarchy h_1 ON h_1.id = org_str_f.hierarchy_id AND h_1.primary_flag IS TRUE
          WHERE org_str.path::text ~~ (('%'::text || oof_1.group_id) || '%'::text) AND org_str_f.level = 6
         LIMIT 1) ol6 ON 1 = 1
     LEFT JOIN LATERAL ( SELECT oof_1.group_id AS level7_org_id,
            oof_1.organization_name_lang1 AS level7_org_name
           FROM base_organization oof_1
             JOIN tsadv_organization_structure org_str_f ON org_str_f.organization_group_id = oof_1.group_id
             JOIN base_hierarchy h_1 ON h_1.id = org_str_f.hierarchy_id AND h_1.primary_flag IS TRUE
          WHERE org_str.path::text ~~ (('%'::text || oof_1.group_id) || '%'::text) AND org_str_f.level = 7
         LIMIT 1) ol7 ON 1 = 1
     LEFT JOIN LATERAL ( SELECT oof_1.group_id AS level8_org_id,
            oof_1.organization_name_lang1 AS level8_org_name
           FROM base_organization oof_1
             JOIN tsadv_organization_structure org_str_f ON org_str_f.organization_group_id = oof_1.group_id
             JOIN base_hierarchy h_1 ON h_1.id = org_str_f.hierarchy_id AND h_1.primary_flag IS TRUE
          WHERE org_str.path::text ~~ (('%'::text || oof_1.group_id) || '%'::text) AND org_str_f.level = 8
         LIMIT 1) ol8 ON 1 = 1
     LEFT JOIN LATERAL ( SELECT oof_1.group_id AS level9_org_id,
            oof_1.organization_name_lang1 AS level9_org_name
           FROM base_organization oof_1
             JOIN tsadv_organization_structure org_str_f ON org_str_f.organization_group_id = oof_1.group_id
             JOIN base_hierarchy h_1 ON h_1.id = org_str_f.hierarchy_id AND h_1.primary_flag IS TRUE
          WHERE org_str.path::text ~~ (('%'::text || oof_1.group_id) || '%'::text) AND org_str_f.level = 9
         LIMIT 1) ol9 ON 1 = 1
     LEFT JOIN LATERAL ( SELECT oof_1.group_id AS level10_org_id,
            oof_1.organization_name_lang1 AS level10_org_name
           FROM base_organization oof_1
             JOIN tsadv_organization_structure org_str_f ON org_str_f.organization_group_id = oof_1.group_id
             JOIN base_hierarchy h_1 ON h_1.id = org_str_f.hierarchy_id AND h_1.primary_flag IS TRUE
          WHERE org_str.path::text ~~ (('%'::text || oof_1.group_id) || '%'::text) AND org_str_f.level = 10
         LIMIT 1) ol10 ON 1 = 1
     LEFT JOIN LATERAL ( SELECT oof_1.group_id AS level11_org_id,
            oof_1.organization_name_lang1 AS level11_org_name
           FROM base_organization oof_1
             JOIN tsadv_organization_structure org_str_f ON org_str_f.organization_group_id = oof_1.group_id
             JOIN base_hierarchy h_1 ON h_1.id = org_str_f.hierarchy_id AND h_1.primary_flag IS TRUE
          WHERE org_str.path::text ~~ (('%'::text || oof_1.group_id) || '%'::text) AND org_str_f.level = 11
         LIMIT 1) ol11 ON 1 = 1
     LEFT JOIN LATERAL ( SELECT oof_1.group_id AS level12_org_id,
            oof_1.organization_name_lang1 AS level12_org_name
           FROM base_organization oof_1
             JOIN tsadv_organization_structure org_str_f ON org_str_f.organization_group_id = oof_1.group_id
             JOIN base_hierarchy h_1 ON h_1.id = org_str_f.hierarchy_id AND h_1.primary_flag IS TRUE
          WHERE org_str.path::text ~~ (('%'::text || oof_1.group_id) || '%'::text) AND org_str_f.level = 12
         LIMIT 1) ol12 ON 1 = 1
     LEFT JOIN LATERAL ( SELECT oof_1.group_id AS level13_org_id,
            oof_1.organization_name_lang1 AS level13_org_name
           FROM base_organization oof_1
             JOIN tsadv_organization_structure org_str_f ON org_str_f.organization_group_id = oof_1.group_id
             JOIN base_hierarchy h_1 ON h_1.id = org_str_f.hierarchy_id AND h_1.primary_flag IS TRUE
          WHERE org_str.path::text ~~ (('%'::text || oof_1.group_id) || '%'::text) AND org_str_f.level = 13
         LIMIT 1) ol13 ON 1 = 1
     LEFT JOIN LATERAL ( SELECT oof_1.group_id AS level14_org_id,
            oof_1.organization_name_lang1 AS level14_org_name
           FROM base_organization oof_1
             JOIN tsadv_organization_structure org_str_f ON org_str_f.organization_group_id = oof_1.group_id
             JOIN base_hierarchy h_1 ON h_1.id = org_str_f.hierarchy_id AND h_1.primary_flag IS TRUE
          WHERE org_str.path::text ~~ (('%'::text || oof_1.group_id) || '%'::text) AND org_str_f.level = 14
         LIMIT 1) ol14 ON 1 = 1
     LEFT JOIN LATERAL ( SELECT oof_1.group_id AS level15_org_id,
            oof_1.organization_name_lang1 AS level15_org_name
           FROM base_organization oof_1
             JOIN tsadv_organization_structure org_str_f ON org_str_f.organization_group_id = oof_1.group_id
             JOIN base_hierarchy h_1 ON h_1.id = org_str_f.hierarchy_id AND h_1.primary_flag IS TRUE
          WHERE org_str.path::text ~~ (('%'::text || oof_1.group_id) || '%'::text) AND org_str_f.level = 15
         LIMIT 1) ol15 ON 1 = 1
  WHERE 1 = 1 AND 'now'::text::date >= org_str.start_date AND 'now'::text::date <= org_str.end_date AND 'now'::text::date >= o.start_date AND 'now'::text::date <= o.end_date AND o.delete_ts IS NULL AND o.group_id = org_str.organization_group_id;
CREATE OR REPLACE VIEW tal.tsadv_person_group_attestation_v
AS WITH param AS (
         SELECT a.id,
            ( SELECT count(*) AS count
                   FROM tsadv_attestation_organization ao
                  WHERE a.id = ao.attestation_id AND ao.delete_ts IS NULL) AS count_org,
            ( SELECT count(*) AS count
                   FROM tsadv_attestation_position ap
                  WHERE a.id = ap.attestation_id AND ap.delete_ts IS NULL) AS count_pos,
            ( SELECT count(*) AS count
                   FROM tsadv_attestation_job aj
                  WHERE a.id = aj.attestation_id AND aj.delete_ts IS NULL) AS count_job
           FROM tsadv_attestation a
        ), org AS (
         SELECT ao.attestation_id,
            o.group_id,
            o.id,
            org_str.organization_group_id
           FROM base_organization o
             JOIN tsadv_organization_structure org_str ON org_str.path::text ~~ (('%'::text || o.group_id) || '%'::text) OR org_str.organization_group_id = o.group_id
             JOIN tsadv_attestation_organization ao ON ao.organization_group_id = o.group_id AND ao.delete_ts IS NULL
          WHERE 1 = 1 AND 'now'::text::date >= org_str.start_date AND 'now'::text::date <= org_str.end_date AND 'now'::text::date >= o.start_date AND 'now'::text::date <= o.end_date AND o.delete_ts IS NULL AND 1 =
                CASE
                    WHEN ao.include_child = true THEN 1
                    WHEN ao.include_child = false AND o.group_id = org_str.organization_group_id THEN 1
                    ELSE 0
                END
        )
 SELECT pp.group_id AS person_group_id,
    param.id AS attestation_id
   FROM base_assignment aa
     JOIN base_person pp ON pp.group_id = aa.person_group_id AND 'now'::text::date >= pp.start_date AND 'now'::text::date <= pp.end_date
     JOIN tsadv_dic_assignment_status ast ON aa.assignment_status_id = ast.id
     JOIN param ON 1 = 1
  WHERE 1 = 1 AND aa.delete_ts IS NULL AND 'now'::text::date >= aa.start_date AND 'now'::text::date <= aa.end_date AND aa.primary_flag = true AND ast.code::text = 'ACTIVE'::text AND 1 =
        CASE
            WHEN param.count_org = 0 THEN 1
            WHEN param.count_org > 0 AND (EXISTS ( SELECT org.organization_group_id
               FROM org
              WHERE org.attestation_id = param.id AND aa.organization_group_id = org.organization_group_id)) THEN 1
            ELSE 0
        END AND 1 =
        CASE
            WHEN param.count_pos = 0 THEN 1
            WHEN param.count_pos > 0 AND (EXISTS ( SELECT pos.position_group_id
               FROM tsadv_attestation_position pos
              WHERE pos.delete_ts IS NULL AND pos.attestation_id = param.id AND aa.position_group_id = pos.position_group_id)) THEN 1
            ELSE 0
        END AND 1 =
        CASE
            WHEN param.count_job = 0 THEN 1
            WHEN param.count_job > 0 AND (EXISTS ( SELECT job.job_group_id
               FROM tsadv_attestation_job job
              WHERE job.delete_ts IS NULL AND job.attestation_id = param.id AND aa.job_group_id = job.job_group_id)) THEN 1
            ELSE 0
        END AND NOT (EXISTS ( SELECT atp.person_group_id
           FROM tsadv_attestation_participant atp
          WHERE atp.attestation_id = param.id AND aa.person_group_id = atp.person_group_id AND atp.delete_ts IS NULL));
CREATE OR REPLACE VIEW tal.tsadv_person_group_attestation_v
AS WITH param AS (
         SELECT a.id,
            ( SELECT count(*) AS count
                   FROM tsadv_attestation_organization ao
                  WHERE a.id = ao.attestation_id AND ao.delete_ts IS NULL) AS count_org,
            ( SELECT count(*) AS count
                   FROM tsadv_attestation_position ap
                  WHERE a.id = ap.attestation_id AND ap.delete_ts IS NULL) AS count_pos,
            ( SELECT count(*) AS count
                   FROM tsadv_attestation_job aj
                  WHERE a.id = aj.attestation_id AND aj.delete_ts IS NULL) AS count_job
           FROM tsadv_attestation a
        ), org AS (
         SELECT ao.attestation_id,
            o.group_id,
            o.id,
            org_str.organization_group_id
           FROM base_organization o
             JOIN tsadv_organization_structure org_str ON org_str.path::text ~~ (('%'::text || o.group_id) || '%'::text) OR org_str.organization_group_id = o.group_id
             JOIN tsadv_attestation_organization ao ON ao.organization_group_id = o.group_id AND ao.delete_ts IS NULL
          WHERE 1 = 1 AND 'now'::text::date >= org_str.start_date AND 'now'::text::date <= org_str.end_date AND 'now'::text::date >= o.start_date AND 'now'::text::date <= o.end_date AND o.delete_ts IS NULL AND 1 =
                CASE
                    WHEN ao.include_child = true THEN 1
                    WHEN ao.include_child = false AND o.group_id = org_str.organization_group_id THEN 1
                    ELSE 0
                END
        )
 SELECT pp.group_id AS person_group_id,
    param.id AS attestation_id
   FROM base_assignment aa
     JOIN base_person pp ON pp.group_id = aa.person_group_id AND 'now'::text::date >= pp.start_date AND 'now'::text::date <= pp.end_date
     JOIN tsadv_dic_assignment_status ast ON aa.assignment_status_id = ast.id
     JOIN param ON 1 = 1
  WHERE 1 = 1 AND aa.delete_ts IS NULL AND 'now'::text::date >= aa.start_date AND 'now'::text::date <= aa.end_date AND aa.primary_flag = true AND ast.code::text = 'ACTIVE'::text AND 1 =
        CASE
            WHEN param.count_org = 0 THEN 1
            WHEN param.count_org > 0 AND (EXISTS ( SELECT org.organization_group_id
               FROM org
              WHERE org.attestation_id = param.id AND aa.organization_group_id = org.organization_group_id)) THEN 1
            ELSE 0
        END AND 1 =
        CASE
            WHEN param.count_pos = 0 THEN 1
            WHEN param.count_pos > 0 AND (EXISTS ( SELECT pos.position_group_id
               FROM tsadv_attestation_position pos
              WHERE pos.delete_ts IS NULL AND pos.attestation_id = param.id AND aa.position_group_id = pos.position_group_id)) THEN 1
            ELSE 0
        END AND 1 =
        CASE
            WHEN param.count_job = 0 THEN 1
            WHEN param.count_job > 0 AND (EXISTS ( SELECT job.job_group_id
               FROM tsadv_attestation_job job
              WHERE job.delete_ts IS NULL AND job.attestation_id = param.id AND aa.job_group_id = job.job_group_id)) THEN 1
            ELSE 0
        END AND NOT (EXISTS ( SELECT atp.person_group_id
           FROM tsadv_attestation_participant atp
          WHERE atp.attestation_id = param.id AND aa.person_group_id = atp.person_group_id AND atp.delete_ts IS NULL));
