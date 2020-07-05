package ua.com.periodicals.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.periodicals.dao.OrderItemDao;
import ua.com.periodicals.dao.PeriodicalDao;
import ua.com.periodicals.entity.Periodical;
import ua.com.periodicals.exception.EntityEngagedException;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PeriodicalService {

    private static final Logger LOG = LoggerFactory.getLogger(PeriodicalService.class);

    @Autowired
    private PeriodicalDao periodicalDao;

    @Autowired
    private OrderItemDao orderItemDao;

    public List<Periodical> getAllPeriodicals() {
        List<Periodical> periodicals = periodicalDao.findAll();
        if (periodicals.size() > 0) {
            return periodicals;
        } else {
            return new ArrayList<Periodical>();
        }
    }

    public long getCount() {
        return periodicalDao.getCount();
    }

    public List<Periodical> getPeriodicalsPage(int page, int maxItems) {
        int firstResult = page == 1 ? 0 : ((page - 1) * maxItems);
        return periodicalDao.findPerPage(firstResult, maxItems);
    }

    public Periodical save(Periodical periodical) {
        LOG.debug("Try to save new periodical: {}", periodical);
        return periodicalDao.save(periodical);
    }

    public Periodical update(Periodical periodical) {
        LOG.debug("Try to update new periodical: {}", periodical);
        return periodicalDao.update(periodical);
    }

    public Periodical getById(long id) {
        LOG.debug("Try to get periodical by id={}", id);
        return periodicalDao.getById(id);
    }

    public void deleteById(long periodicalId) throws EntityEngagedException {
        LOG.debug("Try to delete periodical, id={}", periodicalId);

        validatePeriodicalNotInUse(periodicalId);

        periodicalDao.deleteById(periodicalId);
    }

    public List<Periodical> findAllByInvoiceId(long invoiceId) {
        LOG.debug("Try to get all periodicals by invoice id={}", invoiceId);
        return periodicalDao.findAllByInvoiceId(invoiceId);
    }

    private void validatePeriodicalNotInUse(long id) throws EntityEngagedException {

        if (orderItemDao.isPeriodicalInOrderItems(id)) {
            throw new EntityEngagedException(String.format("Periodical id=%d cannot be deleted because someone subscribed to it.", id));
        }

    }


}
