package ua.com.periodicals.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id")
    private long userId;

    @Enumerated(EnumType.STRING)
    STATUS status;
//    Timestamp createdAt;
//    Timestamp updatedAt;

    @OneToMany
    @JoinColumn(name = "invoice_id")
    private Set<OrderItem> orderItems;

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

    public Set<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(Set<OrderItem> orderItems) {
        this.orderItems = orderItems;
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
