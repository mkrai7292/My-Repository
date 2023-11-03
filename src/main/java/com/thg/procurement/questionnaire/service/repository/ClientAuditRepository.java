package com.thg.procurement.questionnaire.service.repository;

import static com.thg.procurement.questionnaire.service.domain.ClientAudit.TYPE_NAME;

import com.thg.procurement.questionnaire.service.domain.ClientAudit;
import org.springframework.stereotype.Repository;

/**
 * Spring Data Couchbase repository for the ClientAudit entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClientAuditRepository extends JHipsterCouchbaseRepository<ClientAudit, String> {}
