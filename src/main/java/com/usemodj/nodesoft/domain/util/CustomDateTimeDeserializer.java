package com.usemodj.nodesoft.domain.util;

import java.io.IOException;

import org.elasticsearch.common.joda.time.format.DateTimeFormat;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.usemodj.nodesoft.web.rest.TopicResource;

/**
 * Custom Jackson deserializer for displaying Joda DateTime objects.
 */
public class CustomDateTimeDeserializer extends JsonDeserializer<DateTime> {
    private final Logger log = LoggerFactory.getLogger(CustomDateTimeDeserializer.class);

    @Override
    public DateTime deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        JsonToken t =  jp.getCurrentToken();
		
        if (t == JsonToken.VALUE_STRING) {
            String str = jp.getText().trim();
            try {
            	log.debug("JsonToken String: {}", str);
				return ISODateTimeFormat.dateTimeParser().parseDateTime(str);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
        }
        else if (t == JsonToken.VALUE_NUMBER_INT) {
            return new DateTime(jp.getLongValue());
        }
        throw ctxt.mappingException(handledType());
		
    }
}
