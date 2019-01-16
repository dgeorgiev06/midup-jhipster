package com.bluehoodie.midup.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;

/**
 * A DTO for the Event entity.
 */
public class EventDTO implements Serializable {

    private Long id;

    private String eventName;

    private Instant eventDate;

    private String venueId;

    private String address;

    private Long eventOrganizerId;

    private String eventOrganizerLogin;

    private String imageUrl;

    @JsonProperty("invitees")
    public HashSet<InviteeDTO> invitees;

    private String venueName;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public void setInvitees(HashSet<InviteeDTO> invitees) { this.invitees = invitees; }

    public HashSet<InviteeDTO> getInvitees() {
        return invitees;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Instant getEventDate() {
        return eventDate;
    }

    public void setEventDate(Instant eventDate) {
        this.eventDate = eventDate;
    }

    public String getVenueId() {
        return venueId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getEventOrganizerId() {
        return eventOrganizerId;
    }

    public void setEventOrganizerId(Long userProfileId) {
        this.eventOrganizerId = userProfileId;
    }

    public String getEventOrganizerLogin() {
        return eventOrganizerLogin;
    }

    public void setEventOrganizerLogin(String userProfileLogin) {
        this.eventOrganizerLogin = userProfileLogin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EventDTO eventDTO = (EventDTO) o;
        if (eventDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), eventDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "EventDTO{" +
            "id=" + getId() +
            ", eventName='" + getEventName() + "'" +
            ", eventDate='" + getEventDate() + "'" +
            ", venueId='" + getVenueId() + "'" +
            ", address='" + getAddress() + "'" +
            ", eventOrganizer=" + getEventOrganizerId() +
            ", venueName=" + getVenueName() +
            "}";
    }
}
