-- alter table
create
or replace
function tal.populate_organization_list( sec_group_id uuid ) returns boolean as $$ begin delete
from
	TSADV_SECURITY_ORGANIZATION_LIST;

 insert
	into
		TSADV_SECURITY_ORGANIZATION_LIST ( id,
		"version",
		create_ts,
		created_by,
		security_group_id,
		organization_group_id,
		transaction_date ) select
			newid(),
			1,
			current_date,
			'admin',
			tt.security_group_id,
			tt.organization_group_id,
			current_date
		from
			(
			select
				*
			from
				(
				select
					distinct t.security_group_id,
					os.organization_group_id
				from
					TSADV_SECURITY_HIERARCHY_NODE t
				join tsadv_organization_structure os on
					current_date between os.start_date and os.end_date
					and os.path like '%' || t.organization_group_id || '%'
					and os.delete_ts is null
				where
					t.security_group_id = coalesce( sec_group_id,
					t.security_group_id )
					and t.delete_ts is null
			union all
				select
					e.security_group_id,
					e.organization_group_id
				from
					TSADV_SECURITY_ORGANIZATION_ELIGIBILITY e
				where
					e.include_ = true
					and e.security_group_id = coalesce( sec_group_id,
					e.security_group_id )
					and e.delete_ts is null ) t
		except
			select
				e.security_group_id,
				e.organization_group_id
			from
				TSADV_SECURITY_ORGANIZATION_ELIGIBILITY e
			where
				e.include_ = false
				and e.security_group_id = coalesce( sec_group_id,
				e.security_group_id )
				and e.delete_ts is null ) tt;

 return true;


end;

 $$ language plpgsql;

 create
or replace
function tal.populate_person_list( sec_group_id uuid ) returns boolean as $$ begin 

 delete
from
	tsadv_security_person_list t
where
	t.security_group_id = coalesce( sec_group_id,
	t.security_group_id );

 insert
	into
		tsadv_security_person_list ( id,
		"version",
		create_ts,
		created_by,
		security_group_id,
		person_group_id,
		transaction_date ) select
			newid(),
			1,
			current_date,
			'admin',
			tt.security_group_id,
			tt.person_group_id,
			current_date
		from
			(
			select
				distinct spt.security_group_id,
				p.group_id as person_group_id
			from
				TSADV_SECURITY_PERSON_TYPE spt
			join base_person p on
				p.delete_ts is null
				and p.type_id = spt.person_type_id
				and current_date between p.start_date and p.end_date
			left join base_assignment a on
				a.person_group_id = p.group_id
				and a.delete_ts is null
			where
				a.id is null
				and spt.delete_ts is null
				and spt.security_group_id = coalesce( sec_group_id,
				spt.security_group_id )
		union all
			select
				distinct spt.security_group_id,
				p.group_id as person_group_id
			from
				TSADV_SECURITY_PERSON_TYPE spt
			join base_person p on
				p.delete_ts is null
				and p.type_id = spt.person_type_id
				and current_date between p.start_date and p.end_date
			join base_assignment a on
				a.person_group_id = p.group_id
				and a.delete_ts is null
			join TSADV_SECURITY_ORGANIZATION_LIST sol on
				sol.security_group_id = spt.security_group_id
				and sol.organization_group_id = a.organization_group_id
				and sol.delete_ts is null
			where
				spt.delete_ts is null
				and spt.security_group_id = coalesce( sec_group_id,
				spt.security_group_id ) ) tt;

 return true;


end;

 $$ language plpgsql;
