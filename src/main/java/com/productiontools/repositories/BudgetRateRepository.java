package com.productiontools.repositories;

import com.productiontools.entities.budget.BudgetRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BudgetRateRepository extends JpaRepository<BudgetRate, Long> {
    Optional<BudgetRate> findByYearAndMonth(int year, int month);
}
