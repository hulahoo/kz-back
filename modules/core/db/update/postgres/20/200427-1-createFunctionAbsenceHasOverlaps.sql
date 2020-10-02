create or replace function absence_has_overlaps(p_person_group_id uuid, p_start_date date, p_end_date date,
                                                p_lang character varying, p_absence_id uuid,
                                                p_checking_type character varying) returns character varying
  language plpgsql
as
$$
declare
  l_message           varchar default '';
  cr                  record;
  l_initiator_warning varchar default '';
  l_hr_warning        varchar default '';
  l_has_overlaps      boolean default false;
begin
  if (p_checking_type = 'INITIATOR') then
    if (p_lang = 'en') then
      l_initiator_warning := chr(10) || 'With any question please contact to hr.helpdesk';
    else
      l_initiator_warning := chr(10) || 'По вопросам можете обратиться на hr.helpdesk';
    end if;
  else
    if (p_lang = 'en') then
      l_hr_warning := chr(10) || 'Continue?';
    else
      l_hr_warning := chr(10) || 'Продолжить?';
    end if;
  end if;
  if p_lang = 'en'
  then
    l_message := 'There are absence overlaps for selected dates' || chr(10);
    for cr in (select dat.lang_value3 || ' from ' || to_char(a.date_from, 'dd.mm.yyyy') || ' to ' ||
                      to_char(a.date_to, 'dd.mm.yyyy') || chr(10) as "absences"
               from tsadv_absence a
                      join tsadv_dic_absence_type dat
                           on dat.id = a.type_id
                      left join tsadv_absence t1 --отмена отсутствия
                                on t1.parent_absence_id = a.id
                                  and t1.type_id = '6c7a2f45-262f-1ae4-be8d-5cb51fb9d7ef' --отмена приказа
               where a.person_group_id = p_person_group_id
                 and a.date_from <= p_end_date
                 and a.date_to >= p_start_date
                 and a.delete_ts is null
                 and (p_absence_id is null or a.id <> p_absence_id)
                 and a.type_id not in ('6c7a2f45-262f-1ae4-be8d-5cb51fb9d7ef',
                                       'e5878ace-eb4e-e079-986b-24880ace41ca') --отмена и отзыв из отпуска
                 and t1.id is null                                             --убираем отмененные отсутствия
               union all
               select btt.lang_value3 || ' business trip from ' || to_char(bt.date_from, 'dd.mm.yyyy') || ' to ' ||
                      to_char(coalesce(bt.end_date_kz, date_to), 'dd.mm.yyyy') || chr(10)
               from tsadv_business_trip bt
                      join tsadv_dic_business_trip_type btt
                           on btt.id = bt.type_id
               where bt.delete_ts is null
                 and bt.person_group_id = p_person_group_id
                 and bt.date_from <= p_end_date
                 and coalesce(bt.end_date_kz, bt.date_to) >= p_start_date
               union all
               select ' (Launched) ' || tdat.lang_value3 || ' from ' || to_char(r.date_from, 'dd.mm.yyyy') || ' to ' ||
                      to_char(r.date_to, 'dd.mm.yyyy') || chr(10) as "absences"
               from tsadv_absence_request r
                      join base_assignment s
                           on s.group_id = r.assignment_group_id
                             and s.delete_ts is null
                             and s.person_group_id = p_person_group_id
                             and p_start_date between s.start_date and s.end_date
                      join tsadv_dic_absence_type tdat on r.type_id = tdat.id and tdat.deleted_by is null
                      join tsadv_dic_request_status tdrs
                           on r.status_id = tdrs.id and tdrs.code = 'APPROVING' and tdrs.delete_ts is null
               where r.date_from <= p_end_date
                 and r.date_to >= p_start_date
                 and r.delete_ts is null
                 and 'INITIATOR' = p_checking_type
    )
      loop
        l_message := l_message || cr.absences;
        l_has_overlaps := true;
      end loop;
    l_message := l_message || l_initiator_warning || l_hr_warning;
  else
    l_message := 'Есть перекрывающие отсутствия:' || chr(10);
    for cr in (select dat.lang_value1 || ' от ' || to_char(a.date_from, 'dd.mm.yyyy') || ' по ' ||
                      to_char(a.date_to, 'dd.mm.yyyy') || chr(10) as "absences"
               from tsadv_absence a
                      join tsadv_dic_absence_type dat
                           on dat.id = a.type_id
                      left join tsadv_absence t1 --отмена отсутствия
                                on t1.parent_absence_id = a.id
                                  and t1.type_id = '6c7a2f45-262f-1ae4-be8d-5cb51fb9d7ef' --отмена приказа
               where a.person_group_id = p_person_group_id
                 and a.date_from <= p_end_date
                 and a.date_to >= p_start_date
                 and a.delete_ts is null
                 and (p_absence_id is null or a.id <> p_absence_id)
                 and a.type_id not in ('6c7a2f45-262f-1ae4-be8d-5cb51fb9d7ef',
                                       'e5878ace-eb4e-e079-986b-24880ace41ca') --отмена и отзыв из отпуска
                 and t1.id is null                                             --убираем отмененные отсутствия
               union all
               select btt.lang_value1 || ' командировка от ' || to_char(bt.date_from, 'dd.mm.yyyy') || ' по ' ||
                      to_char(coalesce(bt.end_date_kz, date_to), 'dd.mm.yyyy') || chr(10)
               from tsadv_business_trip bt
                      join tsadv_dic_business_trip_type btt
                           on btt.id = bt.type_id
               where bt.delete_ts is null
                 and bt.person_group_id = p_person_group_id
                 and bt.date_from <= p_end_date
                 and coalesce(bt.end_date_kz, bt.date_to) >= p_start_date
               union all
               select ' (Запущенный) ' || tdat.lang_value1 || ' от ' || to_char(r.date_from, 'dd.mm.yyyy') || ' по ' ||
                      to_char(r.date_to, 'dd.mm.yyyy') || chr(10) as "absences"
               from tsadv_absence_request r
                      join base_assignment s
                           on s.group_id = r.assignment_group_id
                             and s.delete_ts is null
                             and s.person_group_id = p_person_group_id
                             and p_start_date between s.start_date and s.end_date
                      join tsadv_dic_absence_type tdat on r.type_id = tdat.id and tdat.deleted_by is null
                      join tsadv_dic_request_status tdrs
                           on r.status_id = tdrs.id and tdrs.code = 'APPROVING' and tdrs.delete_ts is null
               where r.date_from <= p_end_date
                 and r.date_to >= p_start_date
                 and r.delete_ts is null
                 and 'INITIATOR' = p_checking_type
    )
      loop
        l_message := l_message || cr.absences;
        l_has_overlaps := true;
      end loop;
    l_message := l_message || l_initiator_warning || l_hr_warning;
  end if;
  if (l_has_overlaps = false) then
    l_message := '';
  end if;
  return l_message;
end;
$$;

alter function absence_has_overlaps(uuid, date, date, varchar, uuid, varchar) owner to tal;