package com.gearx7.app.domain;

import static com.gearx7.app.domain.MachineTestSamples.*;
import static com.gearx7.app.domain.PartnerTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.gearx7.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PartnerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Partner.class);
        Partner partner1 = getPartnerSample1();
        Partner partner2 = new Partner();
        assertThat(partner1).isNotEqualTo(partner2);

        partner2.setId(partner1.getId());
        assertThat(partner1).isEqualTo(partner2);

        partner2 = getPartnerSample2();
        assertThat(partner1).isNotEqualTo(partner2);
    }

    @Test
    void machineTest() throws Exception {
        Partner partner = getPartnerRandomSampleGenerator();
        Machine machineBack = getMachineRandomSampleGenerator();

        partner.addMachine(machineBack);
        assertThat(partner.getMachines()).containsOnly(machineBack);
        assertThat(machineBack.getPartner()).isEqualTo(partner);

        partner.removeMachine(machineBack);
        assertThat(partner.getMachines()).doesNotContain(machineBack);
        assertThat(machineBack.getPartner()).isNull();

        partner.machines(new HashSet<>(Set.of(machineBack)));
        assertThat(partner.getMachines()).containsOnly(machineBack);
        assertThat(machineBack.getPartner()).isEqualTo(partner);

        partner.setMachines(new HashSet<>());
        assertThat(partner.getMachines()).doesNotContain(machineBack);
        assertThat(machineBack.getPartner()).isNull();
    }
}
