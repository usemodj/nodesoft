package com.usemodj.nodesoft.config;

import java.io.IOException;

import org.joda.time.DateTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.DefaultEntityMapper;
import org.springframework.data.elasticsearch.core.EntityMapper;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.format.datetime.joda.DateTimeFormatterFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.joda.ser.DateTimeSerializer;
import com.fasterxml.jackson.datatype.joda.ser.JacksonJodaFormat;

@Configuration
public class JacksonConfiguration {

	@Bean
	public JodaModule jacksonJodaModule() {
		JodaModule module = new JodaModule();
		DateTimeFormatterFactory formatterFactory = new DateTimeFormatterFactory();
		formatterFactory.setIso(ISO.DATE_TIME);
		module.addSerializer(DateTime.class, new DateTimeSerializer(
				new JacksonJodaFormat(formatterFactory
						.createDateTimeFormatter().withZoneUTC())));
		// module.addDeserializer(DateTime.class, new
		// CustomDateTimeDeserializer());
		
		return module;
	}

	//@Bean
	public EntityMapper entityMapper() {
		return new CustomEntityMapper();
	}

	public class CustomEntityMapper implements EntityMapper {

		private ObjectMapper objectMapper;

		public CustomEntityMapper() {
			objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JodaModule());
			objectMapper.configure(
					SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		}

		@Override
		public String mapToString(Object object) throws IOException {
			return objectMapper.writeValueAsString(object);
		}

		@Override
		public <T> T mapToObject(String source, Class<T> clazz)
				throws IOException {
			return objectMapper.readValue(source, clazz);
		}
	}
}
