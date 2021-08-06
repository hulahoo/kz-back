CREATE OR REPLACE VIEW public.tsadv_organization_incentive_month_result_view
AS SELECT ir.id,
    ir.version,
    mr.create_ts,
    mr.created_by,
    mr.update_ts,
    mr.updated_by,
    mr.delete_ts,
    mr.deleted_by,
    mr.company_id,
    mr.period_,
    ir.organization_group_id AS department_id,
    ir.indicator_id,
    ir.weight,
    ir.plan_,
    ir.fact,
    ir.premium_percent,
    ir.result_ AS total_premium_percent,
    mr.status_id,
    mr.comment_
   FROM tsadv_organization_incentive_month_result mr
     LEFT JOIN tsadv_organization_incentive_result ir ON ir.organization_incentive_month_result_id = mr.id
  WHERE mr.delete_ts IS NULL AND ir.delete_ts IS NULL;