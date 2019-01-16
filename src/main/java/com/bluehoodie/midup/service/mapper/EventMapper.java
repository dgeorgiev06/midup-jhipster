package com.bluehoodie.midup.service.mapper;

import com.bluehoodie.midup.domain.*;
import com.bluehoodie.midup.service.dto.EventDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Event and its DTO EventDTO.
 */
@Mapper(componentModel = "spring", uses = {UserProfileMapper.class, InviteeMapper.class})
public interface EventMapper extends EntityMapper<EventDTO, Event> {

    @Mapping(source = "eventOrganizer.id", target = "eventOrganizerId")
    @Mapping(source = "eventOrganizer.user.login", target = "eventOrganizerLogin")
    @Mapping(source = "invitees", target = "invitees")
    @Mapping(source = "imageUrl", target = "imageUrl")
    EventDTO toDto(Event event);

    @Mapping(source = "eventOrganizerId", target = "eventOrganizer")
    @Mapping(source = "invitees", target = "invitees")
    @Mapping(source = "imageUrl", target = "imageUrl")
    Event toEntity(EventDTO eventDTO);

    default Event fromId(Long id) {
        if (id == null) {
            return null;
        }
        Event event = new Event();
        event.setId(id);
        return event;
    }
}
