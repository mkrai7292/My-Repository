package com.thg.procurement.questionnaire.service.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.thg.procurement.questionnaire.service.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class QuestionnaireTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Questionnaire.class);
        Questionnaire questionnaire1 = new Questionnaire();
        questionnaire1.setId("id1");
        Questionnaire questionnaire2 = new Questionnaire();
        questionnaire2.setId(questionnaire1.getId());
        assertThat(questionnaire1).isEqualTo(questionnaire2);
        questionnaire2.setId("id2");
        assertThat(questionnaire1).isNotEqualTo(questionnaire2);
        questionnaire1.setId(null);
        assertThat(questionnaire1).isNotEqualTo(questionnaire2);
    }
}
