package com.springwithviteblog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaForwardingController {
  @RequestMapping({
      "/{path:^(?!api|assets|uploads|themes|rss|h2-console)[^\\.]*$}",
      "/{path:^(?!api|assets|uploads|themes|rss|h2-console)[^\\.]*$}/**"
  })
  public String forward() {
    return "forward:/index.html";
  }
}
