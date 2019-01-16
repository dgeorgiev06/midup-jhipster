package com.bluehoodie.midup.service.dto;

import java.time.Instant;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Friend entity.
 */
public class FriendDTO implements Serializable {

    private Long id;

    private String status;

    private Instant friendshipRequestDate;

    private Instant friendshipStartDate;

    private Long friendRequestingId;

    private Long friendAcceptingId;

    private String friendAcceptingImageUrl;

    private String friendRequestingImageUrl;

    private String friendRequestingAddress;

    private String friendAcceptingAddress;

    private Double friendRequestingAddressLatitude;

    private Double friendAcceptingAddressLatitude;

    private Double friendRequestingAddressLongitude;

    private Double friendAcceptingAddressLongitude;

    private String friendRequestingLogin;

    private String friendAcceptingLogin;

    public String getFriendRequestingImageUrl() {
        return friendRequestingImageUrl;
    }

    public void setFriendRequestingImageUrl(String friendRequestingImageUrl) {
        this.friendRequestingImageUrl = friendRequestingImageUrl;
    }

    public String getFriendRequestingAddress() {
        return friendRequestingAddress;
    }

    public void setFriendRequestingAddress(String friendRequestingAddress) {
        this.friendRequestingAddress = friendRequestingAddress;
    }

    public String getFriendAcceptingAddress() {
        return friendAcceptingAddress;
    }

    public void setFriendAcceptingAddress(String friendAcceptingAddress) {
        this.friendAcceptingAddress = friendAcceptingAddress;
    }

    public Double getFriendRequestingAddressLatitude() {
        return friendRequestingAddressLatitude;
    }

    public void setFriendRequestingAddressLatitude(Double friendRequestingAddressLatitude) {
        this.friendRequestingAddressLatitude = friendRequestingAddressLatitude;
    }

    public Double getFriendAcceptingAddressLatitude() {
        return friendAcceptingAddressLatitude;
    }

    public void setFriendAcceptingAddressLatitude(Double friendAcceptingAddressLatitude) {
        this.friendAcceptingAddressLatitude = friendAcceptingAddressLatitude;
    }

    public Double getFriendRequestingAddressLongitude() {
        return friendRequestingAddressLongitude;
    }

    public void setFriendRequestingAddressLongitude(Double friendRequestingAddressLongitude) {
        this.friendRequestingAddressLongitude = friendRequestingAddressLongitude;
    }

    public Double getFriendAcceptingAddressLongitude() {
        return friendAcceptingAddressLongitude;
    }

    public void setFriendAcceptingAddressLongitude(Double friendAcceptingAddressLongitude) {
        this.friendAcceptingAddressLongitude = friendAcceptingAddressLongitude;
    }

    public String getFriendRequestingLogin() {
        return friendRequestingLogin;
    }

    public void setFriendRequestingLogin(String friendRequestingLogin) {
        this.friendRequestingLogin = friendRequestingLogin;
    }

    public String getFriendAcceptingLogin() {
        return friendAcceptingLogin;
    }

    public void setFriendAcceptingLogin(String friendAcceptingLogin) {
        this.friendAcceptingLogin = friendAcceptingLogin;
    }

    public String getFriendAcceptingImageUrl() {
        return friendAcceptingImageUrl;
    }

    public void setFriendAcceptingImageUrl(String friendAcceptingImageUrl) {
        this.friendAcceptingImageUrl = friendAcceptingImageUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getFriendshipRequestDate() {
        return friendshipRequestDate;
    }

    public void setFriendshipRequestDate(Instant friendshipRequestDate) {
        this.friendshipRequestDate = friendshipRequestDate;
    }

    public Instant getFriendshipStartDate() {
        return friendshipStartDate;
    }

    public void setFriendshipStartDate(Instant friendshipStartDate) {
        this.friendshipStartDate = friendshipStartDate;
    }

    public Long getFriendRequestingId() {
        return friendRequestingId;
    }

    public void setFriendRequestingId(Long userProfileId) {
        this.friendRequestingId = userProfileId;
    }

    public Long getFriendAcceptingId() {
        return friendAcceptingId;
    }

    public void setFriendAcceptingId(Long userProfileId) {
        this.friendAcceptingId = userProfileId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FriendDTO friendDTO = (FriendDTO) o;
        if (friendDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), friendDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "FriendDTO{" +
            "id=" + id +
            ", status='" + status + '\'' +
            ", friendshipRequestDate=" + friendshipRequestDate +
            ", friendshipStartDate=" + friendshipStartDate +
            ", friendRequestingId=" + friendRequestingId +
            ", friendAcceptingId=" + friendAcceptingId +
            ", friendAcceptingImageUrl='" + friendAcceptingImageUrl + '\'' +
            ", friendRequestingImageUrl='" + friendRequestingImageUrl + '\'' +
            ", friendRequestingAddress='" + friendRequestingAddress + '\'' +
            ", friendAcceptingAddress='" + friendAcceptingAddress + '\'' +
            ", friendRequestingAddressLatitude=" + friendRequestingAddressLatitude +
            ", friendAcceptingAddressLatitude=" + friendAcceptingAddressLatitude +
            ", friendRequestingAddressLongitude=" + friendRequestingAddressLongitude +
            ", friendAcceptingAddressLongitude=" + friendAcceptingAddressLongitude +
            ", friendRequestingLogin='" + friendRequestingLogin + '\'' +
            ", friendAcceptingLogin='" + friendAcceptingLogin + '\'' +
            '}';
    }

}
