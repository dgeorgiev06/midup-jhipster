package com.bluehoodie.midup.service.mapper;

import com.bluehoodie.midup.domain.*;
import com.bluehoodie.midup.service.dto.ProfileAddressDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity ProfileAddress and its DTO ProfileAddressDTO.
 */
@Mapper(componentModel = "spring", uses = {UserProfileMapper.class})
public interface ProfileAddressMapper extends EntityMapper<ProfileAddressDTO, ProfileAddress> {

    @Mapping(source = "userProfile.id", target = "userProfileId")
    ProfileAddressDTO toDto(ProfileAddress profileAddress);

    @Mapping(source = "userProfileId", target = "userProfile")
    ProfileAddress toEntity(ProfileAddressDTO profileAddressDTO);

    default ProfileAddress fromId(Long id) {
        if (id == null) {
            return null;
        }
        ProfileAddress profileAddress = new ProfileAddress();
        profileAddress.setId(id);
        return profileAddress;
    }
}
