package com.logiroute.api.repository;

import com.logiroute.api.domain.Pacote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PacoteRepository extends JpaRepository<Pacote, Long> {
}