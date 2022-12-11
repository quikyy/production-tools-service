package com.productiontools.controllers;

import com.productiontools.entities.budget.BudgetRate;
import com.productiontools.services.BudgetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/budget/api")
public class BudgetController {

    @Autowired
    BudgetService budgetService;

    @PostMapping(path = "/updateDatabase")
    public void updateDatabase(@RequestBody JsonNode payload){
        budgetService.updateDatabase(payload);
    }

    @GetMapping(path = "/getRates")
    public List<BudgetRate> getBudgetRates(){
        return budgetService.getBudgetRates();
    }

}
