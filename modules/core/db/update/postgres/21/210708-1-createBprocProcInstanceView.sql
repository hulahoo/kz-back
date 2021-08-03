CREATE OR REPLACE VIEW bproc_request_vw
AS
select id,
       create_ts,
       created_by,
       update_ts,
       updated_by,
       delete_ts,
       deleted_by,
       status_id,
       request_date,
       request_number,
       'Заявка на отзыв из отпуска' as process_ru,
       'Заявка на отзыв из отпуска' as process_kz,
       'Absence for recall'         as process_en,
       'tsadv_AbsenceForRecall'     as entity_name
from TSADV_ABSENCE_FOR_RECALL
where delete_ts is null
union
select a.id,
       a.create_ts,
       a.created_by,
       a.update_ts,
       a.updated_by,
       a.delete_ts,
       a.deleted_by,
       a.status_id,
       a.request_date,
       a.request_number,
       'Заявка на отсутствие [' || tdat.lang_value1 || ']' as process_ru,
       'Заявка на отсутствие [' || tdat.lang_value2 || ']' as process_kz,
       'Absence request [' || tdat.lang_value3 || ']'      as process_en,
       'tsadv$AbsenceRequest'                              as entity_name
from TSADV_ABSENCE_REQUEST a
         join tsadv_dic_absence_type tdat on a.type_id = tdat.id
where a.delete_ts is null
union
select a.id,
       a.create_ts,
       a.created_by,
       a.update_ts,
       a.updated_by,
       a.delete_ts,
       a.deleted_by,
       a.status_id,
       a.request_date,
       a.request_number,
       'Заявка RVD [' || tdat.lang_value1 || ']'  as process_ru,
       'Заявка RVD [' || tdat.lang_value2 || ']'  as process_kz,
       'RVD request [' || tdat.lang_value3 || ']' as process_en,
       'tsadv_AbsenceRvdRequest'                  as entity_name
from TSADV_ABSENCE_RVD_REQUEST a
         join tsadv_dic_absence_type tdat on a.type_id = tdat.id
where a.delete_ts is null
union
select a.id,
       a.create_ts,
       a.created_by,
       a.update_ts,
       a.updated_by,
       a.delete_ts,
       a.deleted_by,
       a.status_id,
       a.request_date,
       a.request_number,
       'Заявка на изменение адреса'        as process_ru,
       'Заявка на изменение адреса'        as process_kz,
       'Application for change of address' as process_en,
       'tsadv$AddressRequest'              as entity_name
from TSADV_ADDRESS_REQUEST a
where a.delete_ts is null
union
select a.id,
       a.create_ts,
       a.created_by,
       a.update_ts,
       a.updated_by,
       a.delete_ts,
       a.deleted_by,
       a.status_id,
       a.request_date,
       a.request_number,
       'Заявка на KPI'                 as process_ru,
       'Заявка на KPI'                 as process_kz,
       'KPI application'               as process_en,
       'tsadv$AssignedPerformancePlan' as entity_name
from TSADV_ASSIGNED_PERFORMANCE_PLAN a
where a.delete_ts is null
union
select a.id,
       a.create_ts,
       a.created_by,
       a.update_ts,
       a.updated_by,
       a.delete_ts,
       a.deleted_by,
       a.status_id,
       a.request_date,
       a.request_number,
       'Заявка на справку'        as process_ru,
       'Заявка на справку'        as process_kz,
       'Request for help'         as process_en,
       'tsadv_CertificateRequest' as entity_name
from TSADV_CERTIFICATE_REQUEST a
where a.delete_ts is null
union
select a.id,
       a.create_ts,
       a.created_by,
       a.update_ts,
       a.updated_by,
       a.delete_ts,
       a.deleted_by,
       a.status_id,
       a.request_date,
       a.request_number,
       'Заявка на изменение дней отсутствия' as process_ru,
       'Заявка на изменение дней отсутствия' as process_kz,
       'Change absence days request'         as process_en,
       'tsadv_ChangeAbsenceDaysRequest'      as entity_name
from TSADV_CHANGE_ABSENCE_DAYS_REQUEST a
where a.delete_ts is null
union
select a.id,
       a.create_ts,
       a.created_by,
       a.update_ts,
       a.updated_by,
       a.delete_ts,
       a.deleted_by,
       a.status_id,
       a.request_date,
       a.request_number,
       'Заявка на выход из отпуска'   as process_ru,
       'Заявка на выход из отпуска'   as process_kz,
       'Vacation leave application'   as process_en,
       'tsadv$LeavingVacationRequest' as entity_name
from TSADV_LEAVING_VACATION_REQUEST a
where a.delete_ts is null
union
select a.id,
       a.create_ts,
       a.created_by,
       a.update_ts,
       a.updated_by,
       a.delete_ts,
       a.deleted_by,
       a.status_id,
       a.request_date,
       a.request_number,
       'Заявка на изменение орг.структуры' as process_ru,
       'Заявка на изменение орг.структуры' as process_kz,
       'Org.structure change request'      as process_en,
       'tsadv_OrgStructureRequest'         as entity_name
from TSADV_ORG_STRUCTURE_REQUEST a
where a.delete_ts is null
union
select a.id,
       a.create_ts,
       a.created_by,
       a.update_ts,
       a.updated_by,
       a.delete_ts,
       a.deleted_by,
       a.status_id,
       a.request_date,
       a.request_number,
       'Заявка на документ'          as process_ru,
       'Заявка на документ'          as process_kz,
       'Person document request'     as process_en,
       'tsadv_PersonDocumentRequest' as entity_name
from TSADV_PERSON_DOCUMENT_REQUEST a
where a.delete_ts is null
union
select a.id,
       a.create_ts,
       a.created_by,
       a.update_ts,
       a.updated_by,
       a.delete_ts,
       a.deleted_by,
       a.status_id,
       a.request_date,
       a.request_number,
       'Заявка на изменение личных данных'      as process_ru,
       'Заявка на изменение личных данных'      as process_kz,
       'Application for changing personal data' as process_en,
       'tsadv$PersonalDataRequest'              as entity_name
from TSADV_PERSONAL_DATA_REQUEST a
where a.delete_ts is null
union
select a.id,
       a.create_ts,
       a.created_by,
       a.update_ts,
       a.updated_by,
       a.delete_ts,
       a.deleted_by,
       a.status_id,
       a.request_date,
       a.request_number,
       'Заявка на смену графика'      as process_ru,
       'Заявка на смену графика'      as process_kz,
       'Schedule offsets request'     as process_en,
       'tsadv_ScheduleOffsetsRequest' as entity_name
from TSADV_SCHEDULE_OFFSETS_REQUEST a
where a.delete_ts is null;