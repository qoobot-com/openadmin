package com.qoobot.openadmin.admin.mapper;

import com.qoobot.openadmin.admin.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Insert("INSERT INTO admin_user (id, username, display_name, email, mobile, password_hash, enabled, deleted, version, created_at, updated_at, department_id) VALUES (#{id}, #{username}, #{displayName}, #{email}, #{mobile}, #{passwordHash}, #{enabled}, #{deleted}, #{version}, #{createdAt}, #{updatedAt}, #{departmentId})")
    void insert(User user);

    @Update("UPDATE admin_user SET display_name=#{displayName}, email=#{email}, mobile=#{mobile}, password_hash=#{passwordHash}, enabled=#{enabled}, deleted=#{deleted}, version=#{version}, updated_at=#{updatedAt}, department_id=#{departmentId} WHERE id=#{id}")
    void update(User user);

    @Delete("DELETE FROM admin_user WHERE id=#{id}")
    void delete(String id);

    @Select("SELECT * FROM admin_user WHERE id=#{id}")
    User findById(String id);

    @Select("SELECT * FROM admin_user WHERE username=#{username}")
    User findByUsername(String username);

    @Select("SELECT * FROM admin_user LIMIT #{limit} OFFSET #{offset}")
    List<User> findPage(@Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT * FROM admin_user WHERE department_id = #{departmentId} LIMIT #{limit} OFFSET #{offset}")
    List<User> findPageByDepartment(@Param("departmentId") String departmentId, @Param("offset") int offset, @Param("limit") int limit);
}
