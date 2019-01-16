package com.bluehoodie.midup.repository.search;

import com.bluehoodie.midup.domain.ProfileAddress;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the ProfileAddress entity.
 */
public interface ProfileAddressSearchRepository extends ElasticsearchRepository<ProfileAddress, Long> {
}
