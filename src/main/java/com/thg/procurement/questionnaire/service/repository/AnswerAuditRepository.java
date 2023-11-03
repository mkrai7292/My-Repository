package com.thg.procurement.questionnaire.service.repository;

import static com.thg.procurement.questionnaire.service.domain.AnswerAudit.TYPE_NAME;

import com.thg.procurement.questionnaire.service.domain.AnswerAudit;
import org.springframework.stereotype.Repository;

/**
 * Spring Data Couchbase repository for the AnswerAudit entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AnswerAuditRepository extends JHipsterCouchbaseRepository<AnswerAudit, String> {}
