package com.thg.procurement.questionnaire.service.repository;

import static com.thg.procurement.questionnaire.service.domain.Answer.TYPE_NAME;

import com.thg.procurement.questionnaire.service.domain.Answer;
import org.springframework.stereotype.Repository;

/**
 * Spring Data Couchbase repository for the Answer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AnswerRepository extends JHipsterCouchbaseRepository<Answer, String> {}
