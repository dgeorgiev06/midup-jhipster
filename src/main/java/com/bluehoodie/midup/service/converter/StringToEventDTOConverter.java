package com.bluehoodie.midup.service.converter;

import com.bluehoodie.midup.service.dto.EventDTO;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StringToEventDTOConverter implements Converter<String, EventDTO> {

    @Override
    public EventDTO convert(String source) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new JavaTimeModule());
        try {
            return mapper.readValue(source, EventDTO.class);
        }
        catch(IOException ex) {
            return null;
        }
    }
}
