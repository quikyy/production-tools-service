package com.productiontools.services.impl;

import com.productiontools.entities.budget.BudgetRate;
import com.productiontools.enums.Currency;
import com.productiontools.repositories.BudgetRateRepository;
import com.productiontools.services.BudgetService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BudgetServiceImpl implements BudgetService {

    @Autowired
    BudgetRateRepository budgetRateRepository;

    @Override
    @Transactional
    public void updateDatabase(JsonNode payload) {
        List <BudgetRate> budgetRateList = new ArrayList<>();

        if (!payload.isNull()) {
            for (int i = 0; i < payload.size(); i++) {
                JsonNode rate = payload.get(i);
                LocalDate localDate = LocalDate.parse(rate.get("date").asText());

                BudgetRate budgetRate = BudgetRate.builder()
                        .year(localDate.getYear())
                        .month(localDate.getMonthValue())
                        .usd(Float.parseFloat(rate.get(String.valueOf(Currency.USD.getName())).asText()))
                        .eur(Float.parseFloat(rate.get(String.valueOf(Currency.EUR.getName())).asText()))
                        .gbp(Float.parseFloat(rate.get(String.valueOf(Currency.GBP.getName())).asText()))
                        .cad(Float.parseFloat(rate.get(String.valueOf(Currency.CAD.getName())).asText()))
                        .build();
                budgetRateList.add(budgetRate);
            }
        }

        if(!budgetRateList.isEmpty()) {
            budgetRateRepository.deleteAll();
            budgetRateRepository.saveAll(budgetRateList);
        }
    }

    @Override
    @Transactional
    public List<BudgetRate> getBudgetRates() {
        return budgetRateRepository.findAll();
    }
}
