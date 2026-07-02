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
package junit.com.svenruppert.dependencies.core.net;

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

import com.svenruppert.dependencies.core.net.MediaType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MediaTypeTest {

  @Test
  @DisplayName("mime() concatenates type and subtype")
  void mimeReflectsTypeAndSubtype() {
    assertEquals("application/json", MediaType.APPLICATION_JSON.mime());
    assertEquals("text/plain",       MediaType.TEXT_PLAIN.mime());
    assertEquals("image/svg+xml",    MediaType.IMAGE_SVG_XML.mime());
  }

  @Test
  @DisplayName("type() and subtype() return the IANA pair")
  void typeAndSubtype() {
    assertEquals("application",  MediaType.APPLICATION_HAL_JSON.type());
    assertEquals("hal+json",     MediaType.APPLICATION_HAL_JSON.subtype());
  }

  @Test
  @DisplayName("toString() returns the canonical mime form")
  void toStringIsMime() {
    assertEquals("application/json", MediaType.APPLICATION_JSON.toString());
  }

  @Test
  @DisplayName("withCharsetUtf8 produces the conventional lowercase header value")
  void withCharsetUtf8() {
    assertEquals("application/json; charset=utf-8", MediaType.APPLICATION_JSON.withCharsetUtf8());
    assertEquals("text/html; charset=utf-8",        MediaType.TEXT_HTML.withCharsetUtf8());
  }

  @Test
  @DisplayName("withCharset is generic over Charset")
  void withCharsetIsGeneric() {
    assertEquals("text/plain; charset=iso-8859-1",
        MediaType.TEXT_PLAIN.withCharset(StandardCharsets.ISO_8859_1));
    assertEquals("application/xml; charset=utf-16",
        MediaType.APPLICATION_XML.withCharset(StandardCharsets.UTF_16));
  }

  @Test
  @DisplayName("withCharset rejects null charset")
  void withCharsetRejectsNull() {
    assertThrows(NullPointerException.class,
        () -> MediaType.APPLICATION_JSON.withCharset(null));
  }

  @Test
  @DisplayName("family predicates pick the correct top-level type")
  void familyPredicates() {
    assertTrue(MediaType.APPLICATION_JSON.isApplication());
    assertFalse(MediaType.APPLICATION_JSON.isText());

    assertTrue(MediaType.TEXT_HTML.isText());
    assertFalse(MediaType.TEXT_HTML.isImage());

    assertTrue(MediaType.IMAGE_PNG.isImage());
    assertFalse(MediaType.IMAGE_PNG.isMultipart());

    assertTrue(MediaType.MULTIPART_FORM_DATA.isMultipart());
    assertFalse(MediaType.MULTIPART_FORM_DATA.isApplication());

    assertTrue(MediaType.AUDIO_MPEG.isAudio());
    assertFalse(MediaType.AUDIO_MPEG.isVideo());

    assertTrue(MediaType.VIDEO_MP4.isVideo());
    assertFalse(MediaType.VIDEO_MP4.isAudio());

    assertFalse(MediaType.APPLICATION_JSON.isAudio());
    assertFalse(MediaType.APPLICATION_JSON.isVideo());
  }

  @Test
  @DisplayName("isJsonFamily matches plain json AND every RFC 6839 +json suffix")
  void jsonFamilySuffix() {
    assertTrue(MediaType.APPLICATION_JSON.isJsonFamily());
    assertTrue(MediaType.APPLICATION_PROBLEM_JSON.isJsonFamily());
    assertTrue(MediaType.APPLICATION_HAL_JSON.isJsonFamily());
    assertTrue(MediaType.APPLICATION_LD_JSON.isJsonFamily());

    assertFalse(MediaType.APPLICATION_XML.isJsonFamily());
    assertFalse(MediaType.TEXT_PLAIN.isJsonFamily());
    assertFalse(MediaType.APPLICATION_NDJSON.isJsonFamily(),
        "x-ndjson is intentionally not the +json suffix family");
  }

  @Test
  @DisplayName("isXmlFamily matches plain xml AND every RFC 6839 +xml suffix")
  void xmlFamilySuffix() {
    assertTrue(MediaType.APPLICATION_XML.isXmlFamily());
    assertTrue(MediaType.TEXT_XML.isXmlFamily());
    assertTrue(MediaType.IMAGE_SVG_XML.isXmlFamily());
    assertTrue(MediaType.APPLICATION_SOAP_XML.isXmlFamily());

    assertFalse(MediaType.APPLICATION_JSON.isXmlFamily());
    assertFalse(MediaType.TEXT_HTML.isXmlFamily());
  }

  @Test
  @DisplayName("fromMime resolves the canonical mime form")
  void fromMimeCanonical() {
    assertSame(MediaType.APPLICATION_JSON, MediaType.fromMime("application/json"));
    assertSame(MediaType.TEXT_PLAIN,       MediaType.fromMime("text/plain"));
  }

  @Test
  @DisplayName("fromMime strips parameters")
  void fromMimeStripsParameters() {
    assertSame(MediaType.APPLICATION_JSON,
        MediaType.fromMime("application/json; charset=utf-8"));
    assertSame(MediaType.TEXT_HTML,
        MediaType.fromMime("text/html;charset=utf-8;boundary=foo"));
  }

  @Test
  @DisplayName("fromMime is case-insensitive on type and subtype")
  void fromMimeCaseInsensitive() {
    assertSame(MediaType.APPLICATION_JSON, MediaType.fromMime("Application/JSON"));
    assertSame(MediaType.IMAGE_SVG_XML,    MediaType.fromMime("IMAGE/svg+XML"));
  }

  @Test
  @DisplayName("fromMime trims surrounding whitespace")
  void fromMimeTrims() {
    assertSame(MediaType.APPLICATION_JSON, MediaType.fromMime("   application/json   "));
  }

  @Test
  @DisplayName("fromMime throws on unknown")
  void fromMimeThrowsOnUnknown() {
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> MediaType.fromMime("application/x-not-real"));
    assertTrue(ex.getMessage().contains("application/x-not-real"));
  }

  @Test
  @DisplayName("fromMime throws on null")
  void fromMimeThrowsOnNull() {
    assertThrows(NullPointerException.class, () -> MediaType.fromMime(null));
  }

  @Test
  @DisplayName("every enum value round-trips through fromMime")
  void roundTripEveryConstant() {
    for (MediaType mt : MediaType.values()) {
      assertSame(mt, MediaType.fromMime(mt.mime()),
          () -> "round-trip failed for " + mt.name());
    }
  }
}
