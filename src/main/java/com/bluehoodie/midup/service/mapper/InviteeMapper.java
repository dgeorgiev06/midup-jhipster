package com.bluehoodie.midup.service.mapper;

import com.bluehoodie.midup.domain.*;
import com.bluehoodie.midup.service.dto.InviteeDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Invitee and its DTO InviteeDTO.
 */
@Mapper(componentModel = "spring", uses = {UserProfileMapper.class, EventMapper.class})
public interface InviteeMapper extends EntityMapper<InviteeDTO, Invitee> {

    @Mapping(source = "userProfile.id", target = "userProfileId")
    @Mapping(source = "userProfile.imageUrl", target = "imageUrl")
    @Mapping(source = "userProfile.user.login", target = "userLogin")
    @Mapping(source = "event.id", target = "eventId")
    @Mapping(source = "event.eventName", target = "eventEventName")
    InviteeDTO toDto(Invitee invitee);

    @Mapping(source = "userProfileId", target = "userProfile")
    @Mapping(source = "eventId", target = "event")
    Invitee toEntity(InviteeDTO inviteeDTO);

    default Invitee fromId(Long id) {
        if (id == null) {
            return null;
        }
        Invitee invitee = new Invitee();
        invitee.setId(id);
        return invitee;
    }
}
