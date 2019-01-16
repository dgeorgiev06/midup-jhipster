package com.bluehoodie.midup.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A Friend.
 */
@Entity
@Table(name = "friend")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "friend")
public class Friend implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "status")
    private String status;

    @Column(name = "friendship_request_date")
    private Instant friendshipRequestDate;

    @Column(name = "friendship_start_date")
    private Instant friendshipStartDate;

    @ManyToOne
    @JsonIgnoreProperties("")
    private UserProfile friendRequesting;

    @ManyToOne
    @JsonIgnoreProperties("")
    private UserProfile friendAccepting;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public Friend status(String status) {
        this.status = status;
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getFriendshipRequestDate() {
        return friendshipRequestDate;
    }

    public Friend friendshipRequestDate(Instant friendshipRequestDate) {
        this.friendshipRequestDate = friendshipRequestDate;
        return this;
    }

    public void setFriendshipRequestDate(Instant friendshipRequestDate) {
        this.friendshipRequestDate = friendshipRequestDate;
    }

    public Instant getFriendshipStartDate() {
        return friendshipStartDate;
    }

    public Friend friendshipStartDate(Instant friendshipStartDate) {
        this.friendshipStartDate = friendshipStartDate;
        return this;
    }

    public void setFriendshipStartDate(Instant friendshipStartDate) {
        this.friendshipStartDate = friendshipStartDate;
    }

    public UserProfile getFriendRequesting() {
        return friendRequesting;
    }

    public Friend friendRequesting(UserProfile userProfile) {
        this.friendRequesting = userProfile;
        return this;
    }

    public void setFriendRequesting(UserProfile userProfile) {
        this.friendRequesting = userProfile;
    }

    public UserProfile getFriendAccepting() {
        return friendAccepting;
    }

    public Friend friendAccepting(UserProfile userProfile) {
        this.friendAccepting = userProfile;
        return this;
    }

    public void setFriendAccepting(UserProfile userProfile) {
        this.friendAccepting = userProfile;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Friend friend = (Friend) o;
        if (friend.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), friend.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Friend{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", friendshipRequestDate='" + getFriendshipRequestDate() + "'" +
            ", friendshipStartDate='" + getFriendshipStartDate() + "'" +
            "}";
    }
}
