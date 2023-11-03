package com.thg.procurement.questionnaire.service;

import com.thg.procurement.questionnaire.service.QuestionnaireServiceApp;
import com.thg.procurement.questionnaire.service.config.AsyncSyncConfiguration;
import com.thg.procurement.questionnaire.service.config.EmbeddedCouchbase;
import com.thg.procurement.questionnaire.service.config.TestSecurityConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.jhipster.config.JHipsterConstants;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { QuestionnaireServiceApp.class, AsyncSyncConfiguration.class, TestSecurityConfiguration.class })
@EmbeddedCouchbase
@ActiveProfiles(JHipsterConstants.SPRING_PROFILE_TEST)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public @interface IntegrationTest {
}
