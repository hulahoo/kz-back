CREATE OR REPLACE VIEW public.tsadv_organization_incentive_month_result_view --THIS view USED in tsadv_organization_incentive_month_result_short_view
AS SELECT ir.id,
    ir.version,
    mr.create_ts,
    mr.created_by,
    mr.update_ts,
    mr.updated_by,
    mr.delete_ts,
    mr.deleted_by,
    mr.id AS month_result_id,
    mr.company_id,
    mr.period_,
    ir.organization_group_id AS department_id,
    ir.indicator_id,
    it.code AS indicator_type,
    ir.weight,
    ir.plan_,
    ir.fact,
    ir.result_ AS premium_percent,
    sum(ir.result_) OVER (PARTITION BY mr.company_id, mr.period_, ir.organization_group_id, it.code) AS total_premium_percent,
    mr.status_id,
    mr.comment_
   FROM tsadv_organization_incentive_month_result mr
     LEFT JOIN tsadv_organization_incentive_result ir ON ir.organization_incentive_month_result_id = mr.id
     LEFT JOIN tsadv_dic_incentive_indicators ii ON ir.indicator_id = ii.id
     LEFT JOIN tsadv_dic_incentive_indicator_type it ON ii.type_id = it.id
  WHERE mr.delete_ts IS NULL AND ir.delete_ts IS NULL;