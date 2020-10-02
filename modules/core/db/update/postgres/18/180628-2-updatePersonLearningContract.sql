alter table TSADV_PERSON_LEARNING_CONTRACT rename column reason to reason__u05476 ;
alter table TSADV_PERSON_LEARNING_CONTRACT rename column specialization to specialization__u92027 ;
alter table TSADV_PERSON_LEARNING_CONTRACT rename column learning_center to learning_center__u80936 ;
alter table TSADV_PERSON_LEARNING_CONTRACT rename column living_cost to living_cost__u26276 ;
alter table TSADV_PERSON_LEARNING_CONTRACT rename column per_diem_cost to per_diem_cost__u76349 ;
alter table TSADV_PERSON_LEARNING_CONTRACT rename column business_trip_cost to business_trip_cost__u51887 ;
alter table TSADV_PERSON_LEARNING_CONTRACT rename column learning_cost to learning_cost__u33042 ;
alter table TSADV_PERSON_LEARNING_CONTRACT alter column learning_cost__u33042 drop not null ;
