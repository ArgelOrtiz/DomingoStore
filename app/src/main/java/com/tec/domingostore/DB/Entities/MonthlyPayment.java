package com.tec.domingostore.DB.Entities;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

import java.util.Map;

@Entity(indexes = {
        @Index(value = "db_id DESC", unique = true)
})
public class MonthlyPayment {
    @Id
    private Long id;

    @NotNull
    private String db_id;

    private String name;
    private double number_of_months;
    private double commission;
    private boolean status;
    private boolean sync;

    @Generated(hash = 498583578)
    public MonthlyPayment(Long id, @NotNull String db_id, String name,
                          double number_of_months, double commission, boolean status,
                          boolean sync) {
        this.id = id;
        this.db_id = db_id;
        this.name = name;
        this.number_of_months = number_of_months;
        this.commission = commission;
        this.status = status;
        this.sync = sync;
    }

    @Generated(hash = 50337918)
    public MonthlyPayment() {
    }

    public static MonthlyPayment processFromServer(String db_id, Map<String, Object> result) {
        MonthlyPayment currentMonthlyPayment = new MonthlyPayment();

        currentMonthlyPayment.setDb_id(db_id);

        if (result.containsKey("name") && result.get("name") != null)
            currentMonthlyPayment.setName(result.get("name").toString());

        if (result.containsKey("number_of_months") && result.get("number_of_months") != null)
            currentMonthlyPayment.setNumber_of_months(Double.parseDouble(result.get("number_of_months").toString()));

        if (result.containsKey("commission"))
            currentMonthlyPayment.setCommission(Double.parseDouble(result.get("commission").toString()));

        if (result.containsKey("status"))
            currentMonthlyPayment.setStatus((boolean)result.get("status"));

        return currentMonthlyPayment;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDb_id() {
        return this.db_id;
    }

    public void setDb_id(String db_id) {
        this.db_id = db_id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getNumber_of_months() {
        return this.number_of_months;
    }

    public void setNumber_of_months(double number_of_months) {
        this.number_of_months = number_of_months;
    }

    public double getCommission() {
        return this.commission;
    }

    public void setCommission(double commission) {
        this.commission = commission;
    }

    public boolean getStatus() {
        return this.status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean getSync() {
        return this.sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }


}
