update TSADV_ASSIGNED_PERFORMANCE_PLAN
set stage_id = (select id from TSADV_DIC_PERFORMANCE_STAGE where code = TSADV_ASSIGNED_PERFORMANCE_PLAN.step_stage_status )
where TSADV_ASSIGNED_PERFORMANCE_PLAN.stage_id is null