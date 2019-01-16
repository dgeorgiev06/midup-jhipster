package com.bluehoodie.midup.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of ProfileAddressSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class ProfileAddressSearchRepositoryMockConfiguration {

    @MockBean
    private ProfileAddressSearchRepository mockProfileAddressSearchRepository;

}
