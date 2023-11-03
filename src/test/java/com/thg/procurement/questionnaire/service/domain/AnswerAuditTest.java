package com.thg.procurement.questionnaire.service.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.thg.procurement.questionnaire.service.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AnswerAuditTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AnswerAudit.class);
        AnswerAudit answerAudit1 = new AnswerAudit();
        answerAudit1.setId("id1");
        AnswerAudit answerAudit2 = new AnswerAudit();
        answerAudit2.setId(answerAudit1.getId());
        assertThat(answerAudit1).isEqualTo(answerAudit2);
        answerAudit2.setId("id2");
        assertThat(answerAudit1).isNotEqualTo(answerAudit2);
        answerAudit1.setId(null);
        assertThat(answerAudit1).isNotEqualTo(answerAudit2);
    }
}
