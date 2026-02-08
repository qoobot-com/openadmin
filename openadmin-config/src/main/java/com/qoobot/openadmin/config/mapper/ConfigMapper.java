package com.qoobot.openadmin.config.mapper;

import com.qoobot.openadmin.config.entity.ConfigItem;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 配置项Mapper接口
 * 提供配置数据的数据库操作方法
 */
@Mapper
@Repository
public interface ConfigMapper {

    /**
     * 插入配置项
     */
    @Insert({
        "<script>",
        "INSERT INTO config_items (",
        "config_key, config_value, description, group_id, group_name,",
        "environment, config_type, encrypted, status, version,",
        "tags, created_by, updated_by, source",
        ") VALUES (",
        "#{configKey}, #{configValue}, #{description}, #{groupId}, #{groupName},",
        "#{environment}, #{configType}, #{encrypted}, #{status}, #{version},",
        "#{tags}, #{createdBy}, #{updatedBy}, #{source}",
        ")",
        "</script>"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ConfigItem configItem);

    /**
     * 根据ID更新配置项
     */
    @Update({
        "<script>",
        "UPDATE config_items SET",
        "<trim suffixOverrides=\",\">",
        "<if test=\"configKey != null\">config_key = #{configKey},</if>",
        "<if test=\"configValue != null\">config_value = #{configValue},</if>",
        "<if test=\"description != null\">description = #{description},</if>",
        "<if test=\"groupId != null\">group_id = #{groupId},</if>",
        "<if test=\"groupName != null\">group_name = #{groupName},</if>",
        "<if test=\"environment != null\">environment = #{environment},</if>",
        "<if test=\"configType != null\">config_type = #{configType},</if>",
        "<if test=\"encrypted != null\">encrypted = #{encrypted},</if>",
        "<if test=\"status != null\">status = #{status},</if>",
        "<if test=\"version != null\">version = #{version},</if>",
        "<if test=\"tags != null\">tags = #{tags},</if>",
        "<if test=\"updatedBy != null\">updated_by = #{updatedBy},</if>",
        "<if test=\"source != null\">source = #{source},</if>",
        "updated_at = NOW()",
        "</trim>",
        "WHERE id = #{id} AND deleted = false",
        "</script>"
    })
    int updateById(ConfigItem configItem);

    /**
     * 根据ID删除配置项（逻辑删除）
     */
    @Update("UPDATE config_items SET deleted = true, updated_at = NOW() WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 根据ID查询配置项
     */
    @Select("SELECT * FROM config_items WHERE id = #{id} AND deleted = false")
    ConfigItem selectById(Long id);

    /**
     * 根据配置键和环境查询配置项
     */
    @Select("SELECT * FROM config_items WHERE config_key = #{configKey} AND environment = #{environment} AND deleted = false")
    ConfigItem selectByKeyAndEnvironment(@Param("configKey") String configKey, @Param("environment") String environment);

    /**
     * 查询所有配置项
     */
    @Select("SELECT * FROM config_items WHERE deleted = false ORDER BY group_name, config_key")
    List<ConfigItem> selectAll();

    /**
     * 分页查询配置项
     */
    @Select({
        "<script>",
        "SELECT * FROM config_items WHERE deleted = false",
        "<if test=\"configKey != null and configKey != ''\">AND config_key LIKE CONCAT('%', #{configKey}, '%')</if>",
        "<if test=\"groupName != null and groupName != ''\">AND group_name LIKE CONCAT('%', #{groupName}, '%')</if>",
        "<if test=\"environment != null and environment != ''\">AND environment = #{environment}</if>",
        "<if test=\"status != null and status != ''\">AND status = #{status}</if>",
        "<if test=\"configType != null and configType != ''\">AND config_type = #{configType}</if>",
        "<if test=\"encrypted != null\">AND encrypted = #{encrypted}</if>",
        "<if test=\"keyword != null and keyword != ''\">",
        "AND (config_key LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%'))",
        "</if>",
        "ORDER BY updated_at DESC",
        "LIMIT #{offset}, #{limit}",
        "</script>"
    })
    List<ConfigItem> selectByPage(@Param("configKey") String configKey,
                                  @Param("groupName") String groupName,
                                  @Param("environment") String environment,
                                  @Param("status") String status,
                                  @Param("configType") String configType,
                                  @Param("encrypted") Boolean encrypted,
                                  @Param("keyword") String keyword,
                                  @Param("offset") int offset,
                                  @Param("limit") int limit);

    /**
     * 统计配置项数量
     */
    @Select({
        "<script>",
        "SELECT COUNT(*) FROM config_items WHERE deleted = false",
        "<if test=\"configKey != null and configKey != ''\">AND config_key LIKE CONCAT('%', #{configKey}, '%')</if>",
        "<if test=\"groupName != null and groupName != ''\">AND group_name LIKE CONCAT('%', #{groupName}, '%')</if>",
        "<if test=\"environment != null and environment != ''\">AND environment = #{environment}</if>",
        "<if test=\"status != null and status != ''\">AND status = #{status}</if>",
        "<if test=\"configType != null and configType != ''\">AND config_type = #{configType}</if>",
        "<if test=\"encrypted != null\">AND encrypted = #{encrypted}</if>",
        "<if test=\"keyword != null and keyword != ''\">",
        "AND (config_key LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%'))",
        "</if>",
        "</script>"
    })
    int countByCondition(@Param("configKey") String configKey,
                         @Param("groupName") String groupName,
                         @Param("environment") String environment,
                         @Param("status") String status,
                         @Param("configType") String configType,
                         @Param("encrypted") Boolean encrypted,
                         @Param("keyword") String keyword);

    /**
     * 根据分组ID查询配置项
     */
    @Select("SELECT * FROM config_items WHERE group_id = #{groupId} AND deleted = false ORDER BY config_key")
    List<ConfigItem> selectByGroupId(Long groupId);

    /**
     * 根据环境查询配置项
     */
    @Select("SELECT * FROM config_items WHERE environment = #{environment} AND deleted = false ORDER BY group_name, config_key")
    List<ConfigItem> selectByEnvironment(String environment);

    /**
     * 批量插入配置项
     */
    @Insert({
        "<script>",
        "INSERT INTO config_items (",
        "config_key, config_value, description, group_id, group_name,",
        "environment, config_type, encrypted, status, version,",
        "tags, created_by, updated_by, source",
        ") VALUES ",
        "<foreach collection=\"configs\" item=\"config\" separator=\",\">",
        "(#{config.configKey}, #{config.configValue}, #{config.description}, #{config.groupId}, #{config.groupName},",
        "#{config.environment}, #{config.configType}, #{config.encrypted}, #{config.status}, #{config.version},",
        "#{config.tags}, #{config.createdBy}, #{config.updatedBy}, #{config.source})",
        "</foreach>",
        "</script>"
    })
    int batchInsert(@Param("configs") List<ConfigItem> configs);

    /**
     * 批量更新配置项状态
     */
    @Update({
        "<script>",
        "UPDATE config_items SET status = #{status}, updated_at = NOW(), updated_by = #{updatedBy}",
        "WHERE id IN",
        "<foreach collection=\"ids\" item=\"id\" open=\"(\" separator=\",\" close=\")\">",
        "#{id}",
        "</foreach>",
        "AND deleted = false",
        "</script>"
    })
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") String status, @Param("updatedBy") String updatedBy);

    /**
     * 查询配置项的历史版本
     */
    @Select({
        "SELECT c.*, ch.old_value, ch.new_value, ch.change_reason, ch.operator, ch.created_at as change_time",
        "FROM config_items c",
        "LEFT JOIN config_history ch ON c.id = ch.config_id",
        "WHERE c.id = #{configId} AND c.deleted = false",
        "ORDER BY ch.created_at DESC"
    })
    List<ConfigItem> selectHistoryVersions(Long configId);
}