package com.bluehoodie.midup.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Invitee entity.
 */
public class InviteeDTO implements Serializable {

    private Long id;

    private Integer status = new Integer(0);

    @JsonProperty("_userProfileId")
    private Long userProfileId;

    @JsonProperty("_userLogin")
    private String userLogin;

    @JsonProperty("_imageUrl")
    private String imageUrl;

    private Long eventId;

    private String eventEventName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(Long userProfileId) {
        this.userProfileId = userProfileId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventEventName() {
        return eventEventName;
    }

    public void setEventEventName(String eventEventName) {
        this.eventEventName = eventEventName;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String login) {
        this.userLogin = login;
    }

    public void setImageUrl(String url) {
        this.imageUrl = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        InviteeDTO inviteeDTO = (InviteeDTO) o;
        if (inviteeDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), inviteeDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "InviteeDTO{" +
            "id=" + getId() +
            ", status=" + getStatus() +
            ", userProfile=" + getUserProfileId() +
            ", event=" + getEventId() +
            ", event='" + getEventEventName() + "'" +
            "}";
    }
}
