package com.michael.repository;

import com.michael.domain.Quote;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Quote entity.
 */
@SuppressWarnings("unused")
public interface QuoteRepository extends JpaRepository<Quote,Long> {

}
