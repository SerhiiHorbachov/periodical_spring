package ua.com.periodicals.dto;

import javax.persistence.Column;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class PeriodicalDto {

    private long id;

    @NotNull
    @NotEmpty
    private String name;

    private String description;

    @NotNull
    private float monthlyPrice;

    public PeriodicalDto() {
    }

    public PeriodicalDto(long id, String name, String description, float monthlyPrice) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.monthlyPrice = monthlyPrice;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getMonthlyPrice() {
        return monthlyPrice;
    }

    public void setMonthlyPrice(float monthlyPrice) {
        this.monthlyPrice = monthlyPrice;
    }

    @Override
    public String toString() {
        return "PeriodicalDto{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", monthlyPrice=" + monthlyPrice +
            '}';
    }
}
