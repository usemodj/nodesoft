package com.usemodj.nodesoft.domain.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * ISO 8601 date format
 * Jackson deserializer for displaying Joda DateTime objects.
 */
public class ISO8601LocalDateDeserializer extends JsonDeserializer<LocalDate> {
    private final Logger log = LoggerFactory.getLogger(ISO8601LocalDateDeserializer.class);

    @Override
    public LocalDate deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_STRING) {
            String str = jp.getText().trim();
            log.debug(str);
            return ISODateTimeFormat.dateTimeParser().parseLocalDate(str);
        }
        if (t == JsonToken.VALUE_NUMBER_INT) {
            return new LocalDate(jp.getLongValue());
        }
        throw ctxt.mappingException(handledType());
    }
}
