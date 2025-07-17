package com.gearx7.app.domain;

import static com.gearx7.app.domain.AttachmentTestSamples.*;
import static com.gearx7.app.domain.MachineTestSamples.*;
import static com.gearx7.app.domain.PartnerTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.gearx7.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MachineTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Machine.class);
        Machine machine1 = getMachineSample1();
        Machine machine2 = new Machine();
        assertThat(machine1).isNotEqualTo(machine2);

        machine2.setId(machine1.getId());
        assertThat(machine1).isEqualTo(machine2);

        machine2 = getMachineSample2();
        assertThat(machine1).isNotEqualTo(machine2);
    }

    @Test
    void attachmentTest() throws Exception {
        Machine machine = getMachineRandomSampleGenerator();
        Attachment attachmentBack = getAttachmentRandomSampleGenerator();

        machine.addAttachment(attachmentBack);
        assertThat(machine.getAttachments()).containsOnly(attachmentBack);
        assertThat(attachmentBack.getMachine()).isEqualTo(machine);

        machine.removeAttachment(attachmentBack);
        assertThat(machine.getAttachments()).doesNotContain(attachmentBack);
        assertThat(attachmentBack.getMachine()).isNull();

        machine.attachments(new HashSet<>(Set.of(attachmentBack)));
        assertThat(machine.getAttachments()).containsOnly(attachmentBack);
        assertThat(attachmentBack.getMachine()).isEqualTo(machine);

        machine.setAttachments(new HashSet<>());
        assertThat(machine.getAttachments()).doesNotContain(attachmentBack);
        assertThat(attachmentBack.getMachine()).isNull();
    }

    @Test
    void partnerTest() throws Exception {
        Machine machine = getMachineRandomSampleGenerator();
        Partner partnerBack = getPartnerRandomSampleGenerator();

        machine.setPartner(partnerBack);
        assertThat(machine.getPartner()).isEqualTo(partnerBack);

        machine.partner(null);
        assertThat(machine.getPartner()).isNull();
    }
}
