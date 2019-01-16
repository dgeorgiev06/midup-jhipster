package com.bluehoodie.midup.repository.search;

import com.bluehoodie.midup.domain.Friend;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Friend entity.
 */
public interface FriendSearchRepository extends ElasticsearchRepository<Friend, Long> {
}
