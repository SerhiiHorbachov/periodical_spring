package ua.com.periodicals.entity;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private long id;

    @Column(name = "user_id")
    private long userId;

    @Enumerated(EnumType.STRING)
    STATUS status;

    @CreationTimestamp
    @Column(name = "creation_date")
    Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "update_date")
    Timestamp updatedAt;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "invoice", cascade = CascadeType.PERSIST)
//    @JoinColumn(name = "invoice_id")
    private Set<OrderItem> orderItems = new HashSet<>();

        @Transient
    private Set<Periodical> periodicals = new HashSet<>();

    public Invoice() {
    }

    public Invoice(long userId) {
        this.userId = userId;
        this.status = STATUS.IN_PROGRESS;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<OrderItem> getOrderItems() {
        return orderItems;
    }

    public Set<Periodical> getPeriodicals() {
        return periodicals;
    }

    public void setPeriodicals(Set<Periodical> periodicals) {
        this.periodicals = periodicals;
    }

    public void setOrderItems(Set<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
    }


    public enum STATUS {
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }

    @Override
    public String toString() {
        return "Invoice{" +
            "id=" + id +
            ", userId=" + userId +
            ", status=" + status +
            ", orderItems=" + orderItems +
            '}';
    }
}
