package com.bluehoodie.midup.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of InviteeSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class InviteeSearchRepositoryMockConfiguration {

    @MockBean
    private InviteeSearchRepository mockInviteeSearchRepository;

}
