package com.thg.procurement.questionnaire.service.repository;

import com.thg.procurement.questionnaire.service.domain.Authority;
import org.springframework.stereotype.Repository;

/**
 * Spring Data Couchbase repository for the {@link Authority} entity.
 */
@Repository
public interface AuthorityRepository extends JHipsterCouchbaseRepository<Authority, String> {}
