package com.bluehoodie.midup.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Event.
 */
@Entity
@Table(name = "event")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "event")
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "event_name")
    private String eventName;

    @Column(name = "event_date")
    private Instant eventDate;

    @Column(name = "venue_id")
    private String venueId;

    @Column(name = "address")
    private String address;

    @Column(name = "venue_name")
    private String venueName;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne
    private UserProfile eventOrganizer;

    @OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Invitee> invitees = new HashSet<>();
    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public Event eventName(String eventName) {
        this.eventName = eventName;
        return this;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Instant getEventDate() {
        return eventDate;
    }

    public Event eventDate(Instant eventDate) {
        this.eventDate = eventDate;
        return this;
    }

    public void setEventDate(Instant eventDate) {
        this.eventDate = eventDate;
    }

    public String getVenueId() {
        return venueId;
    }

    public Event venueId(String venueId) {
        this.venueId = venueId;
        return this;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public String getAddress() {
        return address;
    }

    public Event address(String address) {
        this.address = address;
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVenueName() {
        return venueName;
    }

    public Event venueName(String venueName) {
        this.venueName = venueName;
        return this;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Event imageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public UserProfile getEventOrganizer() {
        return eventOrganizer;
    }

    public Event eventOrganizer(UserProfile userProfile) {
        this.eventOrganizer = userProfile;
        return this;
    }

    public void setEventOrganizer(UserProfile userProfile) {
        this.eventOrganizer = userProfile;
    }

    public Set<Invitee> getInvitees() {
        return invitees;
    }

    public Event invitees(Set<Invitee> invitees) {
        this.invitees = invitees;
        return this;
    }

    public Event addInvitee(Invitee invitee) {
        this.invitees.add(invitee);
        invitee.setEvent(this);
        return this;
    }

    public Event removeInvitee(Invitee invitee) {
        this.invitees.remove(invitee);
        invitee.setEvent(null);
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
        Event event = (Event) o;
        if (event.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), event.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Event{" +
            "id=" + getId() +
            ", eventName='" + getEventName() + "'" +
            ", eventDate='" + getEventDate() + "'" +
            ", venueId='" + getVenueId() + "'" +
            ", address='" + getAddress() + "'" +
            ", venueName='" + getVenueName() + "'" +
            ", imageUrl='" + getImageUrl() + "'" +
            "}";
    }
}
