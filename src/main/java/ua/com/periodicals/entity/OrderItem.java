package ua.com.periodicals.entity;

import javax.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="order_item_id")
    private long id;

    @Column(name = "invoice_id")
    private long invoiceId;

    @Column(name = "periodical_id")
    private long periodicalId;

    @Column(name = "price_per_month")
    private long costPerMonth;

//    @OneToOne
////    @JoinColumn(name = "periodicals_id");
//    private Periodical periodical;

    @ManyToOne
    @JoinColumn(name = "invoice_id", insertable = false, updatable = false)
    private Invoice invoice;

    public OrderItem() {
    }

    public OrderItem(long periodicalId, long costPerMonth) {
        this.periodicalId = periodicalId;
        this.costPerMonth = costPerMonth;
    }

    public OrderItem(long invoiceId, long periodicalId, long costPerMonth) {
        this.invoiceId = invoiceId;
        this.periodicalId = periodicalId;
        this.costPerMonth = costPerMonth;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public long getPeriodicalId() {
        return periodicalId;
    }

    public void setPeriodicalId(long periodicalId) {
        this.periodicalId = periodicalId;
    }

    public long getCostPerMonth() {
        return costPerMonth;
    }

    public void setCostPerMonth(long costPerMonth) {
        this.costPerMonth = costPerMonth;
    }

//    public Periodical getPeriodical() {
//        return periodical;
//    }
//
//    public void setPeriodical(Periodical periodical) {
//        this.periodical = periodical;
//    }


    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
            "id=" + id +
            ", invoiceId=" + invoiceId +
            ", periodicalId=" + periodicalId +
            ", costPerMonth=" + costPerMonth +
            '}';
    }
}
