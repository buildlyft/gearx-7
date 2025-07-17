package com.gearx7.app.domain;

import static com.gearx7.app.domain.AttachmentTestSamples.*;
import static com.gearx7.app.domain.MachineTestSamples.*;
import static com.gearx7.app.domain.PartnerTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.gearx7.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AttachmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Attachment.class);
        Attachment attachment1 = getAttachmentSample1();
        Attachment attachment2 = new Attachment();
        assertThat(attachment1).isNotEqualTo(attachment2);

        attachment2.setId(attachment1.getId());
        assertThat(attachment1).isEqualTo(attachment2);

        attachment2 = getAttachmentSample2();
        assertThat(attachment1).isNotEqualTo(attachment2);
    }

    @Test
    void partnerTest() throws Exception {
        Attachment attachment = getAttachmentRandomSampleGenerator();
        Partner partnerBack = getPartnerRandomSampleGenerator();

        attachment.setPartner(partnerBack);
        assertThat(attachment.getPartner()).isEqualTo(partnerBack);

        attachment.partner(null);
        assertThat(attachment.getPartner()).isNull();
    }

    @Test
    void machineTest() throws Exception {
        Attachment attachment = getAttachmentRandomSampleGenerator();
        Machine machineBack = getMachineRandomSampleGenerator();

        attachment.setMachine(machineBack);
        assertThat(attachment.getMachine()).isEqualTo(machineBack);

        attachment.machine(null);
        assertThat(attachment.getMachine()).isNull();
    }
}
