package com.thg.procurement.questionnaire.service.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.thg.procurement.questionnaire.service.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class QuestionnaireAuditTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(QuestionnaireAudit.class);
        QuestionnaireAudit questionnaireAudit1 = new QuestionnaireAudit();
        questionnaireAudit1.setId("id1");
        QuestionnaireAudit questionnaireAudit2 = new QuestionnaireAudit();
        questionnaireAudit2.setId(questionnaireAudit1.getId());
        assertThat(questionnaireAudit1).isEqualTo(questionnaireAudit2);
        questionnaireAudit2.setId("id2");
        assertThat(questionnaireAudit1).isNotEqualTo(questionnaireAudit2);
        questionnaireAudit1.setId(null);
        assertThat(questionnaireAudit1).isNotEqualTo(questionnaireAudit2);
    }
}
