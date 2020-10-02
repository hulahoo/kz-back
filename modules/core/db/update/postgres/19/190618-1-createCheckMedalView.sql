create or replace view aa_check_medal as with recogn as (select distinct r.receiver_id,
                                rq.quality_id,
                                count(rq.id) over (partition by r.receiver_id) quality_count
                from tsadv_recognition r
                       join tsadv_recognition_quality rq
                            on rq.recognition_id=r.id
                              and rq.delete_ts is null
                where r.delete_ts is null),
     medals as (select distinct tmc.medal_id,
                                count(tmc.id) over (partition by tmc.medal_id) rowc
                from tsadv_medal_condition tmc
                where tmc.quality_id is not null
                  and tmc.delete_ts is null )
select distinct t.receiver_id, t.medal_id from (
                                                 select r.receiver_id,
                                                        mc.medal_id,
                                                        r.quality_id,
                                                        count(mc.id) over (partition by r.receiver_id, mc.medal_id) c,
                                                        m.rowc
                                                 from tsadv_medal_condition mc
                                                        join recogn r on r.quality_id = mc.quality_id
                                                   and r.quality_count>=mc.quality_quantity
                                                        join medals m on m.medal_id = mc.medal_id) t
where t.c=t.rowc