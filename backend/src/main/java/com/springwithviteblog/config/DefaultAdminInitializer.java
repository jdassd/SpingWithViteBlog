package com.springwithviteblog.config;

import com.springwithviteblog.domain.Role;
import com.springwithviteblog.domain.User;
import com.springwithviteblog.mapper.UserMapper;
import com.springwithviteblog.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DefaultAdminInitializer implements ApplicationRunner {
  private static final Logger logger = LoggerFactory.getLogger(DefaultAdminInitializer.class);

  private final UserMapper userMapper;
  private final UserService userService;

  public DefaultAdminInitializer(UserMapper userMapper, UserService userService) {
    this.userMapper = userMapper;
    this.userService = userService;
  }

  @Override
  public void run(ApplicationArguments args) {
    User existing = userMapper.findByUsername("admin");
    if (existing == null) {
      userService.createUser("admin", null, "123456", Role.ADMIN, true);
      logger.info("Default admin created: admin / 123456");
    }
  }
}
