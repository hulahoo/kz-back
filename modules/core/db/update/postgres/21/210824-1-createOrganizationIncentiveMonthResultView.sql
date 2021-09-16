drop view IF EXISTS tsadv_organization_incentive_month_result_view;
create view tsadv_organization_incentive_month_result_view
            (id, version, create_ts, created_by, update_ts, updated_by, delete_ts, deleted_by, company_id, period_,
             department_id, indicator_id, weight, plan_, fact, result_, premium_percent, total_premium_percent, status_id,
             comment_, parent_id)
as
SELECT ir.id,
       ir.version,
       mr.create_ts,
       mr.created_by,
       mr.update_ts,
       mr.updated_by,
       mr.delete_ts,
       mr.deleted_by,
       mr.company_id,
       mr.period_,
       ir.organization_group_id                                                           AS department_id,
       ir.indicator_id,
       ir.weight,
       ir.plan_,
       ir.fact,
       ir.result_,
       s.total_score                                                                      AS premium_percent,
       sum(s.total_score) OVER (PARTITION BY mr.id, ii.type_id, ir.organization_group_id) AS total_premium_percent,
        NULL::uuid                                                                         AS status_id,
        NULL::character varying(2500)                                                      AS comment_,
       mr.id                                                                              AS parent_id
FROM tsadv_organization_incentive_month_result mr
         JOIN tsadv_organization_incentive_result ir
              ON ir.organization_incentive_month_result_id = mr.id AND ir.delete_ts IS NULL
         JOIN tsadv_dic_incentive_indicators ii ON ii.id = ir.indicator_id AND ii.delete_ts IS NULL
         LEFT JOIN tsadv_dic_incentive_indicator_score_setting s
                   ON s.indicator_id = ir.indicator_id AND s.delete_ts IS NULL AND ir.result_ >= s.min_percent AND
                      ir.result_ <= s.max_percent
WHERE mr.delete_ts IS NULL
UNION
SELECT mr.id,
       mr.version,
       mr.create_ts,
       mr.created_by,
       mr.update_ts,
       mr.updated_by,
       mr.delete_ts,
       mr.deleted_by,
       mr.company_id,
       mr.period_,
       NULL::uuid             AS department_id,
        NULL::uuid             AS indicator_id,
        NULL::double precision AS weight,
       NULL::numeric          AS plan_,
       NULL::numeric          AS fact,
       NULL::numeric          AS result_,
       NULL::double precision AS premium_percent,
       NULL::double precision AS total_premium_percent,
       mr.status_id,
       mr.comment_,
       NULL::uuid             AS parent_id
FROM tsadv_organization_incentive_month_result mr
WHERE mr.delete_ts IS NULL;

alter table tsadv_organization_incentive_month_result_view
    owner to tal;