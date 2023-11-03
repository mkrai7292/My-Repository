package com.thg.procurement.questionnaire.service.repository;

import static com.thg.procurement.questionnaire.service.domain.QuestionnaireAudit.TYPE_NAME;

import com.thg.procurement.questionnaire.service.domain.QuestionnaireAudit;
import org.springframework.stereotype.Repository;

/**
 * Spring Data Couchbase repository for the QuestionnaireAudit entity.
 */
@SuppressWarnings("unused")
@Repository
public interface QuestionnaireAuditRepository extends JHipsterCouchbaseRepository<QuestionnaireAudit, String> {}
