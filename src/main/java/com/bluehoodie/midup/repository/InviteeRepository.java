package com.bluehoodie.midup.repository;

import com.bluehoodie.midup.domain.Invitee;
import com.bluehoodie.midup.domain.UserProfile;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data  repository for the Invitee entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InviteeRepository extends JpaRepository<Invitee, Long>, JpaSpecificationExecutor<Invitee> {

    List<Invitee> findAllByUserProfile(UserProfile userProfile);
}
