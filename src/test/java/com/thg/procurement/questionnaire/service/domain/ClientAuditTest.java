package com.thg.procurement.questionnaire.service.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.thg.procurement.questionnaire.service.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClientAuditTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClientAudit.class);
        ClientAudit clientAudit1 = new ClientAudit();
        clientAudit1.setId("id1");
        ClientAudit clientAudit2 = new ClientAudit();
        clientAudit2.setId(clientAudit1.getId());
        assertThat(clientAudit1).isEqualTo(clientAudit2);
        clientAudit2.setId("id2");
        assertThat(clientAudit1).isNotEqualTo(clientAudit2);
        clientAudit1.setId(null);
        assertThat(clientAudit1).isNotEqualTo(clientAudit2);
    }
}
