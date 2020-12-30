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

