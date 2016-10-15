package com.vaadin.addon.pagination.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by basakpie on 2016-10-14.
 */

@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Long> {
}
