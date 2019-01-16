package com.bluehoodie.midup.repository;

import com.bluehoodie.midup.domain.UserProfile;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Spring Data  repository for the UserProfile entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long>, JpaSpecificationExecutor<UserProfile> {
    @Query("select distinct userProfile from UserProfile userProfile where userProfile.user.id =:id")
    Optional<UserProfile> findOneByUser(@Param("id") Long id);

    @Query("select distinct userProfile from UserProfile userProfile where userProfile.id =:id")
    Optional<UserProfile> findById(@Param("id") Long id);
}
