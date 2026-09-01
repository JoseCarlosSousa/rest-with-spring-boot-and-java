package pt.seixal.carlos.controllers.withyaml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.restassured.mapper.ObjectMapper;
import io.restassured.mapper.ObjectMapperDeserializationContext;
import io.restassured.mapper.ObjectMapperSerializationContext;

public class YAMLMapper implements ObjectMapper {
	
	private com.fasterxml.jackson.databind.ObjectMapper objectMapper;
	private TypeFactory typeFactory;

	public YAMLMapper() {
		this.objectMapper = new com.fasterxml.jackson.dataformat.yaml.YAMLMapper(new YAMLFactory())
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		this.typeFactory = TypeFactory.defaultInstance();
	}
	
	@Override
	public Object deserialize(ObjectMapperDeserializationContext context) {
		try {
			var data = context.getDataToDeserialize().asString();
			Class<?> type = (Class<?>) context.getType();
			return objectMapper.readValue(data, typeFactory.constructType(type));
		} catch (JsonProcessingException e) {
			throw new IllegalStateException("Error deserializing YAML content", e);
		}
	}

	@Override
	public Object serialize(ObjectMapperSerializationContext context) {
		try {
			return objectMapper.writeValueAsString(context.getObjectToSerialize());
		} catch (JsonProcessingException e) {
			throw new IllegalStateException("Failed to serialize object to YAML", e);
		}
	}
}
