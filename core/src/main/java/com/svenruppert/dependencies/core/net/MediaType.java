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

/*-
 * #%L
 * SRU - Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2013 - 2026 Sven Ruppert
 * %%
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
 * #L%
 */

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Typed MIME / media type enum modelled after RFC 9110 §8.3.
 *
 * <p>Each constant carries its IANA {@code type} and {@code subtype}. Structured-syntax
 * suffixes per RFC 6839 (e.g. {@code application/hal+json}, {@code image/svg+xml})
 * are recognised by {@link #isJsonFamily()} and {@link #isXmlFamily()}.
 *
 * <p>Replaces the legacy {@code HttpStatusContentTypes} string-constant collection.
 */
public enum MediaType {

  // application/*
  APPLICATION_JSON("application", "json"),
  APPLICATION_PROBLEM_JSON("application", "problem+json"),
  APPLICATION_HAL_JSON("application", "hal+json"),
  APPLICATION_LD_JSON("application", "ld+json"),
  APPLICATION_NDJSON("application", "x-ndjson"),
  APPLICATION_XML("application", "xml"),
  APPLICATION_SOAP_XML("application", "soap+xml"),
  APPLICATION_JAVASCRIPT("application", "javascript"),
  APPLICATION_FORM_URLENCODED("application", "x-www-form-urlencoded"),
  APPLICATION_OCTET_STREAM("application", "octet-stream"),
  APPLICATION_PDF("application", "pdf"),
  APPLICATION_ZIP("application", "zip"),
  APPLICATION_GZIP("application", "gzip"),
  APPLICATION_YAML("application", "yaml"),

  // text/*
  TEXT_PLAIN("text", "plain"),
  TEXT_HTML("text", "html"),
  TEXT_CSS("text", "css"),
  TEXT_CSV("text", "csv"),
  TEXT_XML("text", "xml"),
  TEXT_MARKDOWN("text", "markdown"),
  TEXT_EVENT_STREAM("text", "event-stream"),

  // image/*
  IMAGE_PNG("image", "png"),
  IMAGE_JPEG("image", "jpeg"),
  IMAGE_GIF("image", "gif"),
  IMAGE_WEBP("image", "webp"),
  IMAGE_AVIF("image", "avif"),
  IMAGE_SVG_XML("image", "svg+xml"),

  // multipart/*
  MULTIPART_FORM_DATA("multipart", "form-data"),
  MULTIPART_MIXED("multipart", "mixed"),

  // audio/* and video/* — minimal representatives for the family predicates
  AUDIO_MPEG("audio", "mpeg"),
  AUDIO_OGG("audio", "ogg"),
  VIDEO_MP4("video", "mp4"),
  VIDEO_WEBM("video", "webm");

  private static final String CHARSET_PARAMETER = "; charset=";
  private static final Map<String, MediaType> LOOKUP = buildLookup();

  private final String type;
  private final String subtype;
  private final String mime;

  MediaType(String type, String subtype) {
    this.type    = type;
    this.subtype = subtype;
    this.mime    = type + "/" + subtype;
  }

  private static Map<String, MediaType> buildLookup() {
    Map<String, MediaType> m = new HashMap<>();
    for (MediaType mt : values()) {
      m.put(mt.mime, mt);
    }
    return Map.copyOf(m);
  }

  /** IANA top-level type, e.g. {@code "application"}. */
  public String type() {
    return type;
  }

  /** IANA subtype, e.g. {@code "json"} or {@code "hal+json"}. */
  public String subtype() {
    return subtype;
  }

  /** Canonical {@code type/subtype} string. */
  public String mime() {
    return mime;
  }

  /**
   * Returns {@code mime() + "; charset=" + cs.name().toLowerCase(Locale.ROOT)}.
   * RFC 9110 §8.3.2 says charset parameter values are case-insensitive; lowercase
   * is the conventional form for {@code Content-Type} headers.
   */
  public String withCharset(Charset cs) {
    Objects.requireNonNull(cs, "charset must not be null");
    return mime + CHARSET_PARAMETER + cs.name().toLowerCase(Locale.ROOT);
  }

  /** Convenience for {@link #withCharset(Charset) withCharset(StandardCharsets.UTF_8)}. */
  public String withCharsetUtf8() {
    return withCharset(StandardCharsets.UTF_8);
  }

  // -------------------------------------------------------------------------
  // Family predicates
  // -------------------------------------------------------------------------

  public boolean isApplication() {
    return "application".equals(type);
  }

  public boolean isText() {
    return "text".equals(type);
  }

  public boolean isImage() {
    return "image".equals(type);
  }

  public boolean isMultipart() {
    return "multipart".equals(type);
  }

  public boolean isAudio() {
    return "audio".equals(type);
  }

  public boolean isVideo() {
    return "video".equals(type);
  }

  /**
   * True for {@code application/json} and any {@code *+json} structured-syntax
   * suffix per RFC 6839, e.g. {@code application/problem+json},
   * {@code application/hal+json}, {@code application/ld+json}.
   */
  public boolean isJsonFamily() {
    return "json".equals(subtype) || subtype.endsWith("+json");
  }

  /**
   * True for {@code application/xml}, {@code text/xml} and any {@code *+xml}
   * structured-syntax suffix per RFC 6839, e.g. {@code image/svg+xml},
   * {@code application/soap+xml}.
   */
  public boolean isXmlFamily() {
    return "xml".equals(subtype) || subtype.endsWith("+xml");
  }

  // -------------------------------------------------------------------------
  // Lookup
  // -------------------------------------------------------------------------

  /**
   * Look up a {@link MediaType} from a header string. The input is normalised:
   * any parameters after the first {@code ;} are stripped, surrounding whitespace
   * is trimmed, and the type/subtype are lower-cased before lookup.
   *
   * @throws NullPointerException     if {@code header} is {@code null}
   * @throws IllegalArgumentException if no constant matches
   */
  public static MediaType fromMime(String header) {
    Objects.requireNonNull(header, "header must not be null");
    String key = normalise(header);
    MediaType mt = LOOKUP.get(key);
    if (mt == null) {
      throw new IllegalArgumentException("Unknown media type: " + header);
    }
    return mt;
  }

  private static String normalise(String header) {
    String trimmed = header.trim();
    int semi = trimmed.indexOf(';');
    String typeAndSubtype = (semi >= 0 ? trimmed.substring(0, semi) : trimmed).trim();
    return typeAndSubtype.toLowerCase(Locale.ROOT);
  }

  /** Canonical {@code type/subtype} string. */
  @Override
  public String toString() {
    return mime;
  }
}
