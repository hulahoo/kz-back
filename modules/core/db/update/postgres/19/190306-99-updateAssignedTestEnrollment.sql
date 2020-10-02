-- alter table
drop view if exists tal.tsadv_assigned_test_enrollment_v;

create or replace view 
tal.tsadv_assigned_test_enrollment_v as  
select
	t.enrollment_id id,
	t.enrollment_id,
	t.pg_id,
	t.full_name,
	t.organization_name_lang1,
	t.position_name_lang1,
	t.test_id,
	t.test_name,
	t.enrollment_status,
	(max( t.csa_success )>0) as success,
	t.attempts,
	max( t.test_result ) as test_result,
	t.course_section_id,
	t.course_id,
	t.section_name,
	t.org_id,
	t.created_by
from
	(
	select
		distinct e.id as enrollment_id,
		bp.group_id pg_id,
		concat( bp.last_name , ' ' , bp.first_name, ' ', bp.middle_name ) full_name,
		org.ORGANIZATION_NAME_LANG1,
		pos.position_name_lang1,
		t.id as test_id,
		t.name as test_name,
		e.status enrollment_status,
		case
			when csa.success = true then 1
			else 0
		end as csa_success,
		count( csa.id ) over ( partition by e.id ) as attempts,
		csa.test_result,
		cs.id course_section_id,
		e.course_id,
		cs.section_name,
		org.group_id as org_id,
		e.created_by as created_by
	from
		tsadv_enrollment e
	join tsadv_course_section cs on
		cs.course_id = e.course_id
	join tsadv_course_section_object cso on
		cso.id = cs.section_object_id
	join tsadv_test t on
		t.id = cso.test_id
	join base_person bp on
		bp.group_id = e.person_group_id
		and current_date between bp.start_date and bp.end_date
		and bp.delete_ts is null
	left join base_assignment a on
		a.person_group_id = e.person_group_id
		and current_date between a.start_date and a.end_date
		and a.primary_flag = true
	left join tsadv_dic_assignment_status das on
		das.id = a.assignment_status_id
		and das.code = 'ACTIVE'
	left join tsadv_course_section_attempt csa on
		csa.enrollment_id = e.id
		and csa.course_section_id = cs.id
	left join BASE_ORGANIZATION org on
		org.group_id = a.ORGANIZATION_GROUP_ID
		and current_date between org.start_date and org.end_date
	left join BASE_POSITION pos on
		pos.group_id = a.POSITION_GROUP_ID
		and current_date between pos.start_date and pos.end_date
	where
		e.delete_ts is null ) t
group by
	t.enrollment_id,
	t.pg_id,
	t.full_name,
	t.organization_name_lang1,
	t.position_name_lang1,
	t.test_id,
	t.test_name,
	t.enrollment_status,
	t.attempts,
	t.course_id,
	t.section_name,
	t.course_section_id,
	t.org_id,
	t.created_by;
