create unique index IDX_TSADV_ASSIGNED_GOAL_UNQ on TSADV_ASSIGNED_GOAL (PERSON_GROUP_ID, GOAL_ID) where DELETE_TS is null ;
