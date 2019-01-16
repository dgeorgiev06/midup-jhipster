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
 * Criteria class for the Friend entity. This class is used in FriendResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /friends?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class FriendCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter status;

    private InstantFilter friendshipRequestDate;

    private InstantFilter friendshipStartDate;

    private LongFilter friendRequestingId;

    private LongFilter friendAcceptingId;

    public FriendCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getStatus() {
        return status;
    }

    public void setStatus(StringFilter status) {
        this.status = status;
    }

    public InstantFilter getFriendshipRequestDate() {
        return friendshipRequestDate;
    }

    public void setFriendshipRequestDate(InstantFilter friendshipRequestDate) {
        this.friendshipRequestDate = friendshipRequestDate;
    }

    public InstantFilter getFriendshipStartDate() {
        return friendshipStartDate;
    }

    public void setFriendshipStartDate(InstantFilter friendshipStartDate) {
        this.friendshipStartDate = friendshipStartDate;
    }

    public LongFilter getFriendRequestingId() {
        return friendRequestingId;
    }

    public void setFriendRequestingId(LongFilter friendRequestingId) {
        this.friendRequestingId = friendRequestingId;
    }

    public LongFilter getFriendAcceptingId() {
        return friendAcceptingId;
    }

    public void setFriendAcceptingId(LongFilter friendAcceptingId) {
        this.friendAcceptingId = friendAcceptingId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final FriendCriteria that = (FriendCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(status, that.status) &&
            Objects.equals(friendshipRequestDate, that.friendshipRequestDate) &&
            Objects.equals(friendshipStartDate, that.friendshipStartDate) &&
            Objects.equals(friendRequestingId, that.friendRequestingId) &&
            Objects.equals(friendAcceptingId, that.friendAcceptingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        status,
        friendshipRequestDate,
        friendshipStartDate,
        friendRequestingId,
        friendAcceptingId
        );
    }

    @Override
    public String toString() {
        return "FriendCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (status != null ? "status=" + status + ", " : "") +
                (friendshipRequestDate != null ? "friendshipRequestDate=" + friendshipRequestDate + ", " : "") +
                (friendshipStartDate != null ? "friendshipStartDate=" + friendshipStartDate + ", " : "") +
                (friendRequestingId != null ? "friendRequestingId=" + friendRequestingId + ", " : "") +
                (friendAcceptingId != null ? "friendAcceptingId=" + friendAcceptingId + ", " : "") +
            "}";
    }

}
