-- Все работники по аттестаци (таблица для производительности)      https://apps.uco.kz/confluence/pages/viewpage.action?pageId=14254093
create table if not exists xxtsadv_all_persons_for_attestation_t (
    person_group_id uuid not null,
    attestation_id uuid not null
);
