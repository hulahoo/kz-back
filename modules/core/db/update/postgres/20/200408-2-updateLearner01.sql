create unique index if not exists IDX_TSADV_LEARNER_UNQ on TSADV_LEARNER (GROUP_ID, PERSON_GROUP_ID) where DELETE_TS is null ;
