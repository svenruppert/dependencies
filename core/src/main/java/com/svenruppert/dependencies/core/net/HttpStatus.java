package com.svenruppert.dependencies.core.net;

public enum HttpStatus {
  OK(200, "OK"),
  CREATED(201, "Created"),
  NO_CONTENT(204, "No Content"),
  MOVED_PERMANENTLY(301, "Moved Permanently"),
  FOUND(302, "Found"),
  SEE_OTHER(303, "See Other"),
  NOT_MODIFIED(304, "Not Modified"),
  BAD_REQUEST(400, "Bad Request"),
  UNAUTHORIZED(401, "Unauthorized"),
  FORBIDDEN(403, "Forbidden"),
  NOT_FOUND(404, "Not Found"),
  METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
  CONFLICT(409, "Conflict"),
  UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
  INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
  NOT_IMPLEMENTED(501, "Not Implemented"),
  SERVICE_UNAVAILABLE(503, "Service Unavailable");

  private final int code;
  private final String reason;

  HttpStatus(int code, String reason) {
    this.code = code;
    this.reason = reason;
  }

  public static HttpStatus fromCode(int code) {
    for (HttpStatus status : values()) {
      if (status.code == code) {
        return status;
      }
    }
    throw new IllegalArgumentException("Unknown HTTP status code: " + code);
  }

  public int code() {
    return code;
  }

  public String reason() {
    return reason;
  }

  @Override
  public String toString() {
    return code + " " + reason;
  }
}