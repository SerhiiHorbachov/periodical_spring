package ua.com.periodicals.entity;

import javax.persistence.*;

@Entity
@Table(name = "periodicals")
public class Periodical {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private String description;

    @Column(name = "monthly_price_cents")
    private long monthlyPrice;

    public Periodical() {
    }

    public Periodical(long id, String name, String description, long monthlyPrice) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.monthlyPrice = monthlyPrice;
    }

    public Periodical(String name, String description, long monthlyPrice) {
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

    public long getMonthlyPrice() {
        return monthlyPrice;
    }

    public void setMonthlyPrice(long monthlyPrice) {
        this.monthlyPrice = monthlyPrice;
    }

    @Override
    public String toString() {
        return "Periodical{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", monthlyPrice=" + monthlyPrice +
            '}';
    }
}
