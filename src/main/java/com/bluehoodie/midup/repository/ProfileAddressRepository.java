package com.bluehoodie.midup.repository;

import com.bluehoodie.midup.domain.ProfileAddress;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the ProfileAddress entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProfileAddressRepository extends JpaRepository<ProfileAddress, Long>, JpaSpecificationExecutor<ProfileAddress> {

}
