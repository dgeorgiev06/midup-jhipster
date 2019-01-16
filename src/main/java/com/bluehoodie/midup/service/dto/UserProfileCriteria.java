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

/**
 * Criteria class for the UserProfile entity. This class is used in UserProfileResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /user-profiles?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class UserProfileCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter imageUrl;

    private StringFilter address;

    private DoubleFilter addressLongitude;

    private DoubleFilter addressLatitude;

    private LongFilter userId;

    private LongFilter requestingFriendId;

    private LongFilter acceptingFriendId;

    private LongFilter eventId;

    private LongFilter inviteeId;

    public UserProfileCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(StringFilter imageUrl) {
        this.imageUrl = imageUrl;
    }

    public StringFilter getAddress() {
        return address;
    }

    public void setAddress(StringFilter address) {
        this.address = address;
    }

    public DoubleFilter getAddressLongitude() {
        return addressLongitude;
    }

    public void setAddressLongitude(DoubleFilter addressLongitude) {
        this.addressLongitude = addressLongitude;
    }

    public DoubleFilter getAddressLatitude() {
        return addressLatitude;
    }

    public void setAddressLatitude(DoubleFilter addressLatitude) {
        this.addressLatitude = addressLatitude;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public LongFilter getRequestingFriendId() {
        return requestingFriendId;
    }

    public void setRequestingFriendId(LongFilter requestingFriendId) {
        this.requestingFriendId = requestingFriendId;
    }

    public LongFilter getAcceptingFriendId() {
        return acceptingFriendId;
    }

    public void setAcceptingFriendId(LongFilter acceptingFriendId) {
        this.acceptingFriendId = acceptingFriendId;
    }

    public LongFilter getEventId() {
        return eventId;
    }

    public void setEventId(LongFilter eventId) {
        this.eventId = eventId;
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
        final UserProfileCriteria that = (UserProfileCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(imageUrl, that.imageUrl) &&
            Objects.equals(address, that.address) &&
            Objects.equals(addressLongitude, that.addressLongitude) &&
            Objects.equals(addressLatitude, that.addressLatitude) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(requestingFriendId, that.requestingFriendId) &&
            Objects.equals(acceptingFriendId, that.acceptingFriendId) &&
            Objects.equals(eventId, that.eventId) &&
            Objects.equals(inviteeId, that.inviteeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        imageUrl,
        address,
        addressLongitude,
        addressLatitude,
        userId,
        requestingFriendId,
        acceptingFriendId,
        eventId,
        inviteeId
        );
    }

    @Override
    public String toString() {
        return "UserProfileCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (imageUrl != null ? "imageUrl=" + imageUrl + ", " : "") +
                (address != null ? "address=" + address + ", " : "") +
                (addressLongitude != null ? "addressLongitude=" + addressLongitude + ", " : "") +
                (addressLatitude != null ? "addressLatitude=" + addressLatitude + ", " : "") +
                (userId != null ? "userId=" + userId + ", " : "") +
                (requestingFriendId != null ? "requestingFriendId=" + requestingFriendId + ", " : "") +
                (acceptingFriendId != null ? "acceptingFriendId=" + acceptingFriendId + ", " : "") +
                (eventId != null ? "eventId=" + eventId + ", " : "") +
                (inviteeId != null ? "inviteeId=" + inviteeId + ", " : "") +
            "}";
    }

}
