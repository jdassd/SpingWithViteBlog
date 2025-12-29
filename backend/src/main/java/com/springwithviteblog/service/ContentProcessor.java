package com.springwithviteblog.service;

import com.springwithviteblog.domain.ContentType;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;

@Service
public class ContentProcessor {
  private final Parser parser;
  private final HtmlRenderer renderer;
  private final Safelist safelist;

  public ContentProcessor() {
    this.parser = Parser.builder().build();
    this.renderer = HtmlRenderer.builder().build();
    this.safelist = Safelist.relaxed()
        .addTags("pre", "code")
        .addAttributes("code", "class")
        .addAttributes("pre", "class")
        .addProtocols("img", "src", "http", "https", "data")
        .addProtocols("a", "href", "http", "https", "mailto");
  }

  public String toSafeHtml(ContentType contentType, String contentRaw) {
    if (contentRaw == null) {
      return "";
    }
    String html;
    if (contentType == ContentType.MARKDOWN) {
      html = renderer.render(parser.parse(contentRaw));
    } else {
      html = contentRaw;
    }
    return Jsoup.clean(html, safelist);
  }

  public String toSummary(ContentType contentType, String contentRaw, int maxLength) {
    if (contentRaw == null) {
      return "";
    }
    String text;
    if (contentType == ContentType.MARKDOWN) {
      String html = renderer.render(parser.parse(contentRaw));
      text = Jsoup.parse(html).text();
    } else {
      text = Jsoup.parse(contentRaw).text();
    }
    if (text.length() <= maxLength) {
      return text;
    }
    return text.substring(0, maxLength);
  }
}
