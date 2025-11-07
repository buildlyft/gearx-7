package com.gearx7.app.domain;

import static com.gearx7.app.domain.CategoryTestSamples.*;
import static com.gearx7.app.domain.SubcategoryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.gearx7.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SubcategoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Subcategory.class);
        Subcategory subcategory1 = getSubcategorySample1();
        Subcategory subcategory2 = new Subcategory();
        assertThat(subcategory1).isNotEqualTo(subcategory2);

        subcategory2.setId(subcategory1.getId());
        assertThat(subcategory1).isEqualTo(subcategory2);

        subcategory2 = getSubcategorySample2();
        assertThat(subcategory1).isNotEqualTo(subcategory2);
    }

    @Test
    void categoryTest() throws Exception {
        Subcategory subcategory = getSubcategoryRandomSampleGenerator();
        Category categoryBack = getCategoryRandomSampleGenerator();

        subcategory.setCategory(categoryBack);
        assertThat(subcategory.getCategory()).isEqualTo(categoryBack);

        subcategory.category(null);
        assertThat(subcategory.getCategory()).isNull();
    }
}
