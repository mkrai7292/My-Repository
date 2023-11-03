package com.thg.procurement.questionnaire.service.repository;

import static com.thg.procurement.questionnaire.service.domain.Client.TYPE_NAME;

import com.thg.procurement.questionnaire.service.domain.Client;
import org.springframework.stereotype.Repository;

/**
 * Spring Data Couchbase repository for the Client entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClientRepository extends JHipsterCouchbaseRepository<Client, String> {}
