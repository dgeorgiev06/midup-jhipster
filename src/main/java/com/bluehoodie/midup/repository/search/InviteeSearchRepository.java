package com.bluehoodie.midup.repository.search;

import com.bluehoodie.midup.domain.Invitee;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Invitee entity.
 */
public interface InviteeSearchRepository extends ElasticsearchRepository<Invitee, Long> {
}
