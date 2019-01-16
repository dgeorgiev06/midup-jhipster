package com.bluehoodie.midup.service.mapper;

import com.bluehoodie.midup.domain.*;
import com.bluehoodie.midup.service.dto.FriendDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Friend and its DTO FriendDTO.
 */
@Mapper(componentModel = "spring", uses = {UserProfileMapper.class})
public interface FriendMapper extends EntityMapper<FriendDTO, Friend> {

    @Mapping(source = "friendRequesting.id", target = "friendRequestingId")
    @Mapping(source = "friendAccepting.id", target = "friendAcceptingId")
    @Mapping(source = "friendAccepting.imageUrl", target = "friendAcceptingImageUrl")
    @Mapping(source = "friendRequesting.imageUrl", target = "friendRequestingImageUrl")
    @Mapping(source = "friendAccepting.address", target = "friendAcceptingAddress")
    @Mapping(source = "friendRequesting.address", target = "friendRequestingAddress")
    @Mapping(source = "friendRequesting.addressLatitude", target = "friendRequestingAddressLatitude")
    @Mapping(source = "friendRequesting.addressLongitude", target = "friendRequestingAddressLongitude")
    @Mapping(source = "friendAccepting.addressLatitude", target = "friendAcceptingAddressLatitude")
    @Mapping(source = "friendAccepting.addressLongitude", target = "friendAcceptingAddressLongitude")
    @Mapping(source = "friendRequesting.user.login", target = "friendRequestingLogin")
    @Mapping(source = "friendAccepting.user.login", target = "friendAcceptingLogin")
    FriendDTO toDto(Friend friend);

    @Mapping(source = "friendRequestingId", target = "friendRequesting")
    @Mapping(source = "friendAcceptingId", target = "friendAccepting")
    Friend toEntity(FriendDTO friendDTO);

    default Friend fromId(Long id) {
        if (id == null) {
            return null;
        }
        Friend friend = new Friend();
        friend.setId(id);
        return friend;
    }
}
