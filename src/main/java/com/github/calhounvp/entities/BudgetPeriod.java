package com.github.calhounvp.entities;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/***
 * The BudgetPeriod made as a Singleton,
 * even though this is just a little trial project
 * I decided to just make this according to this
 * pattern because I only need an instance once because the
 * period shouldn't change in the middle of a workday where
 * this application would hypothetically be used for.
 */
public class BudgetPeriod {
    //__________________________________Properties__________________________________
    private static volatile BudgetPeriod instance;
    private final String budgetPeriod;

    //_________________________________Constructors_________________________________
    private BudgetPeriod() {
        if (instance != null) {
            throw new RuntimeException("Use getInstance() method to create");
        }
        else {
            budgetPeriod = LocalDate.now()
                                    .format(DateTimeFormatter.ofPattern("MMMMyyyy"));
        }
    }

    //____________________________________Methods___________________________________
    //************************************getters***********************************
    public static BudgetPeriod getInstance() {
        if (instance == null) {
            synchronized (BudgetPeriod.class){
                if (instance == null) {
                    instance = new BudgetPeriod();
                }
            }
        }
        return instance;
    }

    @Override
    public String toString() {
        return budgetPeriod;
    }

}
