package com.bluehoodie.midup.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the ProfileAddress entity.
 */
public class ProfileAddressDTO implements Serializable {

    private Long id;

    private Integer addressType;

    private String address;

    private Boolean isDefault;

    private Double longitude;

    private Double latitude;

    private Long userProfileId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAddressType() {
        return addressType;
    }

    public void setAddressType(Integer addressType) {
        this.addressType = addressType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean isIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Long getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(Long userProfileId) {
        this.userProfileId = userProfileId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ProfileAddressDTO profileAddressDTO = (ProfileAddressDTO) o;
        if (profileAddressDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), profileAddressDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ProfileAddressDTO{" +
            "id=" + getId() +
            ", addressType=" + getAddressType() +
            ", address='" + getAddress() + "'" +
            ", isDefault='" + isIsDefault() + "'" +
            ", longitude=" + getLongitude() +
            ", latitude=" + getLatitude() +
            ", userProfile=" + getUserProfileId() +
            "}";
    }
}
