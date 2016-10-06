package com.github.searls.jasmine.thirdpartylibs;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ResourceHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;

public abstract class AbstractThirdPartyLibsResourceHandler extends ResourceHandler {

  @Override
  public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    if (baseRequest.isHandled()) {
      return;
    }

    String resourcePath = getResourcePath(target);
    InputStream resource = findResource(resourcePath);

    if (resource != null) {
      String javascript = IOUtils.toString(resource, "UTF-8");
      if ("/jasmine/boot.js".equals(target)) {
        javascript = javascript.replaceAll("window.onload =", "jasmine.boot =");
      }
      setHeaders(response, resourcePath, javascript);
      writeResponse(response, javascript);
      baseRequest.setHandled(true);
    }
  }

  protected abstract InputStream findResource(String resourcePath);

  private String getResourcePath(String url) {
    return url.replaceFirst("^/", "");
  }

  private void setHeaders(HttpServletResponse response, String resourcePath, String content) {
    response.setCharacterEncoding("UTF-8");

    if (resourcePath.endsWith(".css")) {
      response.setContentType("text/css");
    } else {
      response.setContentType("text/javascript");
    }
    response.addDateHeader("EXPIRES", 0L);
    response.setDateHeader(HttpHeader.LAST_MODIFIED.asString(), new Date().getTime());
    try {
      int contentLength = content.getBytes("UTF-8").length;
      response.setHeader(HttpHeader.CONTENT_LENGTH.asString(), Integer.toString(contentLength));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("The JVM does not support javascript default encoding.", e);
    }
  }

  private void writeResponse(HttpServletResponse response, String javascript) throws IOException {
    response.getWriter().write(javascript);
  }
}
