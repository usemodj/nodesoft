package com.usemodj.nodesoft.domain.util;

import java.io.IOException;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class CustomLocalDateSerializer extends JsonSerializer<LocalDate> {
	private final Logger log = LoggerFactory.getLogger(CustomLocalDateSerializer.class);
			
    private static DateTimeFormatter formatter = DateTimeFormat
            .forPattern("yyyy-MM-dd");

    @Override
    public void serialize(LocalDate value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
//        log.debug(value.toString());
//        LocalDate date = new LocalDate(value, DateTimeZone.UTC);
//        log.debug(value.toString());
    	jgen.writeString(formatter.print(value));
    }
}
