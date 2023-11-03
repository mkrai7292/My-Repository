package com.thg.procurement.questionnaire.service.repository;

import static com.thg.procurement.questionnaire.service.domain.Questionnaire.TYPE_NAME;

import com.thg.procurement.questionnaire.service.domain.Questionnaire;
import org.springframework.stereotype.Repository;

/**
 * Spring Data Couchbase repository for the Questionnaire entity.
 */
@SuppressWarnings("unused")
@Repository
public interface QuestionnaireRepository extends JHipsterCouchbaseRepository<Questionnaire, String> {}
