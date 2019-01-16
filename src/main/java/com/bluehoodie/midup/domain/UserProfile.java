package com.bluehoodie.midup.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A UserProfile.
 */
@Entity
@Table(name = "user_profile")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "userprofile")
public class UserProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "address")
    private String address;

    @Column(name = "address_longitude")
    private Double addressLongitude;

    @Column(name = "address_latitude")
    private Double addressLatitude;

    @OneToOne    @JoinColumn(unique = true)
    private User user;

    @OneToMany(mappedBy = "friendRequesting", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Friend> requestingFriends = new HashSet<>();
    @OneToMany(mappedBy = "friendAccepting", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Friend> acceptingFriends = new HashSet<>();
    @OneToMany(mappedBy = "eventOrganizer", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Event> events = new HashSet<>();
    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Invitee> invitees = new HashSet<>();
    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public UserProfile imageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAddress() {
        return address;
    }

    public UserProfile address(String address) {
        this.address = address;
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getAddressLongitude() {
        return addressLongitude;
    }

    public UserProfile addressLongitude(Double addressLongitude) {
        this.addressLongitude = addressLongitude;
        return this;
    }

    public void setAddressLongitude(Double addressLongitude) {
        this.addressLongitude = addressLongitude;
    }

    public Double getAddressLatitude() {
        return addressLatitude;
    }

    public UserProfile addressLatitude(Double addressLatitude) {
        this.addressLatitude = addressLatitude;
        return this;
    }

    public void setAddressLatitude(Double addressLatitude) {
        this.addressLatitude = addressLatitude;
    }

    public User getUser() {
        return user;
    }

    public UserProfile user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Friend> getRequestingFriends() {
        return requestingFriends;
    }

    public UserProfile requestingFriends(Set<Friend> friends) {
        this.requestingFriends = friends;
        return this;
    }

    public UserProfile addRequestingFriend(Friend friend) {
        this.requestingFriends.add(friend);
        friend.setFriendRequesting(this);
        return this;
    }

    public UserProfile removeRequestingFriend(Friend friend) {
        this.requestingFriends.remove(friend);
        friend.setFriendRequesting(null);
        return this;
    }

    public void setRequestingFriends(Set<Friend> friends) {
        this.requestingFriends = friends;
    }

    public Set<Friend> getAcceptingFriends() {
        return acceptingFriends;
    }

    public UserProfile acceptingFriends(Set<Friend> friends) {
        this.acceptingFriends = friends;
        return this;
    }

    public UserProfile addAcceptingFriend(Friend friend) {
        this.acceptingFriends.add(friend);
        friend.setFriendAccepting(this);
        return this;
    }

    public UserProfile removeAcceptingFriend(Friend friend) {
        this.acceptingFriends.remove(friend);
        friend.setFriendAccepting(null);
        return this;
    }

    public void setAcceptingFriends(Set<Friend> friends) {
        this.acceptingFriends = friends;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public UserProfile events(Set<Event> events) {
        this.events = events;
        return this;
    }

    public UserProfile addEvent(Event event) {
        this.events.add(event);
        event.setEventOrganizer(this);
        return this;
    }

    public UserProfile removeEvent(Event event) {
        this.events.remove(event);
        event.setEventOrganizer(null);
        return this;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    public Set<Invitee> getInvitees() {
        return invitees;
    }

    public UserProfile invitees(Set<Invitee> invitees) {
        this.invitees = invitees;
        return this;
    }

    public UserProfile addInvitee(Invitee invitee) {
        this.invitees.add(invitee);
        invitee.setUserProfile(this);
        return this;
    }

    public UserProfile removeInvitee(Invitee invitee) {
        this.invitees.remove(invitee);
        invitee.setUserProfile(null);
        return this;
    }

    public void setInvitees(Set<Invitee> invitees) {
        this.invitees = invitees;
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
        UserProfile userProfile = (UserProfile) o;
        if (userProfile.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), userProfile.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "UserProfile{" +
            "id=" + getId() +
            ", imageUrl='" + getImageUrl() + "'" +
            ", address='" + getAddress() + "'" +
            ", addressLongitude=" + getAddressLongitude() +
            ", addressLatitude=" + getAddressLatitude() +
            "}";
    }
}
