package ua.com.periodicals.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.com.periodicals.entity.Invoice;
import ua.com.periodicals.entity.OrderItem;

import java.util.List;
import java.util.Set;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
//    Set<OrderItem> findOrderItemsByInvoiceId(Long id);

    List<Invoice> findAllByStatus(Invoice.STATUS status);

}
