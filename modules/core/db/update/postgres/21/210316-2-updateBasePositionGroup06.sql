alter table BASE_POSITION_GROUP rename column position_group_id to position_group_id__u62112 ;
alter table BASE_POSITION_GROUP drop constraint FK_BASE_POSITION_GROUP_POSITION_GROUP ;
drop index IDX_BASE_POSITION_GROUP_POSITION_GROUP ;
