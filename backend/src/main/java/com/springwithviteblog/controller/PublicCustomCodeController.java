package com.springwithviteblog.controller;

import com.springwithviteblog.service.CustomCodeService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublicCustomCodeController {
  private final CustomCodeService customCodeService;

  public PublicCustomCodeController(CustomCodeService customCodeService) {
    this.customCodeService = customCodeService;
  }

  @GetMapping(value = "/custom.css", produces = "text/css")
  public String customCss() {
    String css = customCodeService.getActiveCss();
    return css == null ? "" : css;
  }

  @GetMapping(value = "/custom.js", produces = "application/javascript")
  public String customJs() {
    String js = customCodeService.getActiveJs();
    return js == null ? "" : js;
  }
}
