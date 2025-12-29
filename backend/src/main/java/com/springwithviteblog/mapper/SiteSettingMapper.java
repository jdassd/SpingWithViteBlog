package com.springwithviteblog.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SiteSettingMapper {
  @Select("SELECT setting_value FROM site_settings WHERE setting_key = #{key}")
  String findValue(@Param("key") String key);

  @Insert("INSERT INTO site_settings (setting_key, setting_value, updated_at) VALUES (#{key}, #{value}, CURRENT_TIMESTAMP)")
  int insert(@Param("key") String key, @Param("value") String value);

  @Update("UPDATE site_settings SET setting_value = #{value}, updated_at = CURRENT_TIMESTAMP WHERE setting_key = #{key}")
  int update(@Param("key") String key, @Param("value") String value);
}
