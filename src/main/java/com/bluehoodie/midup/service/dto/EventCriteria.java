package com.bluehoodie.midup.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.InstantFilter;

/**
 * Criteria class for the Event entity. This class is used in EventResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /events?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class EventCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter eventName;

    private InstantFilter eventDate;

    private StringFilter venueId;

    private StringFilter address;

    private StringFilter venueName;

    private StringFilter imageUrl;

    private LongFilter eventOrganizerId;

    private LongFilter inviteeId;

    public EventCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getEventName() {
        return eventName;
    }

    public void setEventName(StringFilter eventName) {
        this.eventName = eventName;
    }

    public InstantFilter getEventDate() {
        return eventDate;
    }

    public void setEventDate(InstantFilter eventDate) {
        this.eventDate = eventDate;
    }

    public StringFilter getVenueId() {
        return venueId;
    }

    public void setVenueId(StringFilter venueId) {
        this.venueId = venueId;
    }

    public StringFilter getAddress() {
        return address;
    }

    public void setAddress(StringFilter address) {
        this.address = address;
    }

    public StringFilter getVenueName() {
        return venueName;
    }

    public void setVenueName(StringFilter venueName) {
        this.venueName = venueName;
    }

    public StringFilter getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(StringFilter imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LongFilter getEventOrganizerId() {
        return eventOrganizerId;
    }

    public void setEventOrganizerId(LongFilter eventOrganizerId) {
        this.eventOrganizerId = eventOrganizerId;
    }

    public LongFilter getInviteeId() {
        return inviteeId;
    }

    public void setInviteeId(LongFilter inviteeId) {
        this.inviteeId = inviteeId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EventCriteria that = (EventCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(eventName, that.eventName) &&
            Objects.equals(eventDate, that.eventDate) &&
            Objects.equals(venueId, that.venueId) &&
            Objects.equals(address, that.address) &&
            Objects.equals(venueName, that.venueName) &&
            Objects.equals(imageUrl, that.imageUrl) &&
            Objects.equals(eventOrganizerId, that.eventOrganizerId) &&
            Objects.equals(inviteeId, that.inviteeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        eventName,
        eventDate,
        venueId,
        address,
        venueName,
        imageUrl,
        eventOrganizerId,
        inviteeId
        );
    }

    @Override
    public String toString() {
        return "EventCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (eventName != null ? "eventName=" + eventName + ", " : "") +
                (eventDate != null ? "eventDate=" + eventDate + ", " : "") +
                (venueId != null ? "venueId=" + venueId + ", " : "") +
                (address != null ? "address=" + address + ", " : "") +
                (venueName != null ? "venueName=" + venueName + ", " : "") +
                (imageUrl != null ? "imageUrl=" + imageUrl + ", " : "") +
                (eventOrganizerId != null ? "eventOrganizerId=" + eventOrganizerId + ", " : "") +
                (inviteeId != null ? "inviteeId=" + inviteeId + ", " : "") +
            "}";
    }

}
