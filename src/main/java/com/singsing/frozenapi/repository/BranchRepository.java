package com.singsing.frozenapi.repository;

import com.singsing.frozenapi.domain.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<Branch, Integer> {
}
