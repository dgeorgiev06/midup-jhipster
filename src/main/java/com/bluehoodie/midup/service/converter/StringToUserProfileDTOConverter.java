package com.bluehoodie.midup.service.converter;

import com.bluehoodie.midup.service.dto.UserProfileDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StringToUserProfileDTOConverter implements Converter<String, UserProfileDTO> {

    @Override
    public UserProfileDTO convert(String source) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(source, UserProfileDTO.class);
        }
        catch(IOException ex) {
            return null;
        }
    }
}
