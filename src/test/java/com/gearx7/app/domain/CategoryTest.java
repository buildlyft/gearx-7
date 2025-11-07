package com.gearx7.app.domain;

import static com.gearx7.app.domain.CategoryTestSamples.*;
import static com.gearx7.app.domain.SubcategoryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.gearx7.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CategoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Category.class);
        Category category1 = getCategorySample1();
        Category category2 = new Category();
        assertThat(category1).isNotEqualTo(category2);

        category2.setId(category1.getId());
        assertThat(category1).isEqualTo(category2);

        category2 = getCategorySample2();
        assertThat(category1).isNotEqualTo(category2);
    }

    @Test
    void subcategoryTest() throws Exception {
        Category category = getCategoryRandomSampleGenerator();
        Subcategory subcategoryBack = getSubcategoryRandomSampleGenerator();

        category.addSubcategory(subcategoryBack);
        assertThat(category.getSubcategories()).containsOnly(subcategoryBack);
        assertThat(subcategoryBack.getCategory()).isEqualTo(category);

        category.removeSubcategory(subcategoryBack);
        assertThat(category.getSubcategories()).doesNotContain(subcategoryBack);
        assertThat(subcategoryBack.getCategory()).isNull();

        category.subcategories(new HashSet<>(Set.of(subcategoryBack)));
        assertThat(category.getSubcategories()).containsOnly(subcategoryBack);
        assertThat(subcategoryBack.getCategory()).isEqualTo(category);

        category.setSubcategories(new HashSet<>());
        assertThat(category.getSubcategories()).doesNotContain(subcategoryBack);
        assertThat(subcategoryBack.getCategory()).isNull();
    }
}
