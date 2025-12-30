package com.springwithviteblog.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class SpaForwardingController {
  @RequestMapping({"/{path:[^\\.]*}", "/**/{path:[^\\.]*}"})
  public String forward(HttpServletRequest request) {
    String path = request.getRequestURI();
    if (path.startsWith("/api")
        || path.startsWith("/rss")
        || path.startsWith("/uploads")
        || path.startsWith("/themes")
        || path.startsWith("/h2-console")) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    return "forward:/index.html";
  }
}
