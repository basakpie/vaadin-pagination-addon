package com.vaadin.addon.pagination;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by gmind on 2016-10-31.
 */
public interface TestBeanRepository extends JpaRepository<TestBean, Long> {

}
