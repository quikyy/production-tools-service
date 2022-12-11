package com.productiontools.services;

import com.productiontools.entities.budget.BudgetRate;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public interface BudgetService {
    void updateDatabase(JsonNode payload);

    @Transactional
    List<BudgetRate> getBudgetRates();
}
