/*
 * Copyright © 2013 Sven Ruppert (sven.ruppert@gmail.com)
 *
 * Licensed under the EUPL, Version 1.2 (the "Licence");
 * you may not use this file except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *     https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */
package com.svenruppert.dependencies.core.net;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTP status codes per RFC 9110 (+ gängige Erweiterungen).
 * - Typsicher (Enum)
 * - O(1) Lookup via fromCode(int)
 * - Hilfsmethoden: isInformational/Successful/Redirection/ClientError/ServerError, family()
 * <p>
 * Hinweis:
 * - Reason-Phrases sind menschenlesbar; HTTP/2/3 ignorieren sie.
 * - 418 ist nicht standardisiert (Spaß-Status), daher als NON_STANDARD markiert.
 */
public enum HttpStatus {

  // 1xx — Informational
  CONTINUE(100, "Continue"),
  SWITCHING_PROTOCOLS(101, "Switching Protocols"),
  PROCESSING(102, "Processing"),                       // WebDAV (RFC 2518)
  EARLY_HINTS(103, "Early Hints"),                     // RFC 8297

  // 2xx — Successful
  OK(200, "OK"),
  CREATED(201, "Created"),
  ACCEPTED(202, "Accepted"),
  NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information"),
  NO_CONTENT(204, "No Content"),
  RESET_CONTENT(205, "Reset Content"),
  PARTIAL_CONTENT(206, "Partial Content"),
  MULTI_STATUS(207, "Multi-Status"),                   // WebDAV (RFC 4918)
  ALREADY_REPORTED(208, "Already Reported"),           // WebDAV (RFC 5842)
  IM_USED(226, "IM Used"),                              // RFC 3229

  // 3xx — Redirection
  MULTIPLE_CHOICES(300, "Multiple Choices"),
  MOVED_PERMANENTLY(301, "Moved Permanently"),
  FOUND(302, "Found"),
  SEE_OTHER(303, "See Other"),
  NOT_MODIFIED(304, "Not Modified"),
  USE_PROXY(305, "Use Proxy"),                          // veraltet
  UNUSED(306, "Unused"),
  TEMPORARY_REDIRECT(307, "Temporary Redirect"),
  PERMANENT_REDIRECT(308, "Permanent Redirect"),        // RFC 7538

  // 4xx — Client Error
  BAD_REQUEST(400, "Bad Request"),
  UNAUTHORIZED(401, "Unauthorized"),
  PAYMENT_REQUIRED(402, "Payment Required"),
  FORBIDDEN(403, "Forbidden"),
  NOT_FOUND(404, "Not Found"),
  METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
  NOT_ACCEPTABLE(406, "Not Acceptable"),
  PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
  REQUEST_TIMEOUT(408, "Request Timeout"),
  CONFLICT(409, "Conflict"),
  GONE(410, "Gone"),
  LENGTH_REQUIRED(411, "Length Required"),
  PRECONDITION_FAILED(412, "Precondition Failed"),
  CONTENT_TOO_LARGE(413, "Content Too Large"),          // früher: Payload Too Large
  URI_TOO_LONG(414, "URI Too Long"),
  UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
  RANGE_NOT_SATISFIABLE(416, "Range Not Satisfiable"),
  EXPECTATION_FAILED(417, "Expectation Failed"),
  MISDIRECTED_REQUEST(421, "Misdirected Request"),      // HTTP/2
  UNPROCESSABLE_CONTENT(422, "Unprocessable Content"),  // WebDAV (RFC 4918), früher: Unprocessable Entity
  LOCKED(423, "Locked"),                                // WebDAV
  FAILED_DEPENDENCY(424, "Failed Dependency"),          // WebDAV
  TOO_EARLY(425, "Too Early"),                          // RFC 8470
  UPGRADE_REQUIRED(426, "Upgrade Required"),
  PRECONDITION_REQUIRED(428, "Precondition Required"),  // RFC 6585
  TOO_MANY_REQUESTS(429, "Too Many Requests"),          // RFC 6585
  REQUEST_HEADER_FIELDS_TOO_LARGE(431, "Request Header Fields Too Large"), // RFC 6585
  UNAVAILABLE_FOR_LEGAL_REASONS(451, "Unavailable For Legal Reasons"),     // RFC 7725

  // Non-Standard / Humor (bewusst gekennzeichnet)
  IM_A_TEAPOT(418, "I'm a teapot", true),               // RFC 2324 (April Fools) / non-standard in Praxis

  // 5xx — Server Error
  INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
  NOT_IMPLEMENTED(501, "Not Implemented"),
  BAD_GATEWAY(502, "Bad Gateway"),
  SERVICE_UNAVAILABLE(503, "Service Unavailable"),
  GATEWAY_TIMEOUT(504, "Gateway Timeout"),
  HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported"),
  VARIANT_ALSO_NEGOTIATES(506, "Variant Also Negotiates"), // RFC 2295
  INSUFFICIENT_STORAGE(507, "Insufficient Storage"),       // WebDAV
  LOOP_DETECTED(508, "Loop Detected"),                     // WebDAV
  NOT_EXTENDED(510, "Not Extended"),                       // RFC 2774 (historisch)
  NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required"); // RFC 6585

  // ---------- Lookup ----------
  private static final Map<Integer, HttpStatus> LOOKUP = new HashMap<>();

  static {
    for (HttpStatus s : values()) {
      // Bei Kollisionen gewinnt der zuerst definierte; hier irrelevant.
      LOOKUP.putIfAbsent(s.code, s);
    }
  }

  private final int code;
  private final String reason;
  private final boolean nonStandard;

  HttpStatus(int code, String reason) {
    this(code, reason, false);
  }

  HttpStatus(int code, String reason, boolean nonStandard) {
    this.code = code;
    this.reason = reason;
    this.nonStandard = nonStandard;
  }

  public static HttpStatus fromCode(int code) {
    HttpStatus s = LOOKUP.get(code);
    if (s == null) throw new IllegalArgumentException("Unknown HTTP status code: " + code);
    return s;
  }

  public int code() {
    return code;
  }

  public String codeStr() {
    return String.valueOf(code());
  }

  public String reason() {
    return reason;
  }

  public boolean isNonStandard() {
    return nonStandard;
  }

  public Family family() {
    if (code >= 100 && code < 200) return Family.INFORMATIONAL;
    if (code >= 200 && code < 300) return Family.SUCCESSFUL;
    if (code >= 300 && code < 400) return Family.REDIRECTION;
    if (code >= 400 && code < 500) return Family.CLIENT_ERROR;
    if (code >= 500 && code < 600) return Family.SERVER_ERROR;
    return Family.UNKNOWN;
  }

  public boolean isInformational() {
    return family() == Family.INFORMATIONAL;
  }

  public boolean isSuccessful() {
    return family() == Family.SUCCESSFUL;
  }

  public boolean isRedirection() {
    return family() == Family.REDIRECTION;
  }

  public boolean isClientError() {
    return family() == Family.CLIENT_ERROR;
  }

  public boolean isServerError() {
    return family() == Family.SERVER_ERROR;
  }

  @Override
  public String toString() {
    return code + " " + reason;
  }

  // ---------- Familien-Typ ----------
  public enum Family {
    INFORMATIONAL, SUCCESSFUL, REDIRECTION, CLIENT_ERROR, SERVER_ERROR, UNKNOWN
  }
}
