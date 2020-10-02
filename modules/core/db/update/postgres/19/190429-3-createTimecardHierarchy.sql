DROP VIEW IF EXISTS timecard_hierarchy;
CREATE OR REPLACE VIEW timecard_hierarchy AS
  WITH RECURSIVE org_pos(id, parent_id, element_type, organization_group_id, position_group_id, start_date, end_date, id_path, level) AS (
  SELECT
    he.id,
    he.parent_id,
    he.element_type,
    he.organization_group_id,
    --       NULL :: UUID                                                                  AS parent_organization_group_id,
    NULL :: UUID                                                                  AS position_group_id,
    he.start_date,
    he.end_date,
    --       coalesce(he.organization_group_id, he.position_group_id) :: CHARACTER VARYING AS path,
    he.id :: CHARACTER VARYING                                                    AS id_path,
    1
  --       coalesce(he.organization_group_id, he.position_group_id) :: CHARACTER VARYING AS organization_group_path,
  --       he.start_date                                                                 AS pos_start_date,
  --       he.end_date                                                                   AS pos_end_date
  FROM tal.base_hierarchy_element he
    JOIN tal.base_hierarchy h ON h.id = he.hierarchy_id
                                 AND h.primary_flag = TRUE
    JOIN tal.base_dic_hierarchy_type ht ON ht.id = h.type_id
  WHERE he.parent_id IS NULL
  UNION ALL
  SELECT
    he.id,
    he.parent_id,
    he.element_type,
    COALESCE(he.organization_group_id, op.organization_group_id)                    AS organization_group_id,
    --       COALESCE(op.organization_group_id, op.parent_organization_group_id)             AS parent_organization_group_id,
    COALESCE(he.position_group_id, op.position_group_id)                            AS position_group_id,
    he.start_date,
    he.end_date,
    --       ((op.path :: TEXT || '*' :: TEXT) ||
    --        coalesce(he.organization_group_id, he.position_group_id)) :: CHARACTER VARYING AS "varchar",
    ((op.id_path :: TEXT || '*' :: TEXT) ||
     he.id) :: CHARACTER VARYING                                                    AS "varchar",
    op.level + 1
  --       ((op.path :: TEXT || '*' :: TEXT) ||
  --        coalesce(he.organization_group_id, he.position_group_id)) :: CHARACTER VARYING AS "varchar",
  --       he.start_date                                                                   AS pos_start_date,
  --       he.end_date                                                                     AS pos_end_date
  FROM (SELECT *
        FROM tal.base_hierarchy_element
        UNION ALL
        SELECT
          pos.group_id               AS id,
                      pos."version",
                      pos.create_ts,
                      pos.created_by,
                      pos.update_ts,
                      pos.updated_by,
                      pos.delete_ts,
                      pos.deleted_by,
          2                          AS element_type,
          h.id                       AS "hierarchy_id",
          pos.start_date,
          pos.end_date,
          pos.group_id               AS "position_group_id",
          NULL                       AS "organization_group_id",
          he.id                      AS "parent",
          'base$HierarchyElementExt' AS "dtype",
          NULL                       AS "legacy_id"

        FROM base_position pos
          JOIN base_hierarchy_element he
            ON he.organization_group_id = pos.organization_group_ext_id
               AND he.start_date <= pos.end_date
               AND he.end_date >= pos.start_date
          --             --   AND he.start_date BETWEEN pos.start_date AND pos.end_date
          JOIN base_hierarchy h
            ON h.id = he.hierarchy_id
               AND h.primary_flag = TRUE
          JOIN tsadv_dic_position_status ps
            ON pos.position_status_id = ps.id
            AND ps.code = 'ACTIVE'
        WHERE pos.delete_ts IS NULL
--               AND current_date >= pos.start_date
--               AND current_date <= pos.end_date
       ) he
    JOIN tal.base_hierarchy h ON h.id = he.hierarchy_id
                                 AND h.primary_flag = TRUE
    JOIN org_pos op ON he.parent_id = op.id
)
SELECT
  org_pos.id,
  org_pos.parent_id,
  org_pos.element_type,
  org_pos.organization_group_id,
  --     org_pos.parent_organization_group_id,
  org_pos.position_group_id,
  org_pos.start_date,
  org_pos.end_date,
  --     org_pos.path,
  org_pos.id_path,
  org_pos.level as _level
--     org_pos.organization_group_path,
--     org_pos.pos_start_date,
--     org_pos.pos_end_date,
--     NULL :: INTEGER                          AS version,
--     NULL :: TIMESTAMP WITHOUT TIME ZONE      AS create_ts,
--     NULL :: CHARACTER VARYING                AS created_by,
--     NULL :: TIMESTAMP WITHOUT TIME ZONE      AS update_ts,
--     NULL :: CHARACTER VARYING                AS updated_by,
--     NULL :: TIMESTAMP WITHOUT TIME ZONE      AS delete_ts,
--     NULL :: CHARACTER VARYING                AS deleted_by
FROM org_pos