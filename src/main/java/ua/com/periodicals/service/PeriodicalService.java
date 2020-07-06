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

/**
 * @author Serhii Hor
 * @since 2020-06
 */
@Service
@Transactional
public class PeriodicalService {

    private static final Logger LOG = LoggerFactory.getLogger(PeriodicalService.class);

    @Autowired
    private PeriodicalDao periodicalDao;

    @Autowired
    private OrderItemDao orderItemDao;

    /**
     * Finds all periodicals stored in the database
     *
     * @return List<Periodical>
     */
    public List<Periodical> getAllPeriodicals() {
        List<Periodical> periodicals = periodicalDao.findAll();
        if (periodicals.size() > 0) {
            return periodicals;
        } else {
            return new ArrayList<Periodical>();
        }
    }

    /**
     * Counts numebr of periodicals stored in the database
     *
     * @return long
     */
    public long getCount() {
        return periodicalDao.getCount();
    }

    /**
     * Method return periodicals for specified page
     *
     * @param page     page number
     * @param maxItems number of items per page
     * @return List<Periodical>
     */
    public List<Periodical> getPeriodicalsPage(int page, int maxItems) {
        int firstResult = page == 1 ? 0 : ((page - 1) * maxItems);
        return periodicalDao.findPerPage(firstResult, maxItems);
    }

    /**
     * Saves a new Periodical in the database.
     *
     * @param periodical New Periodical to be saved.
     * @return Stored Periodical in the database with generated id.
     */
    public Periodical save(Periodical periodical) {
        LOG.debug("Try to save new periodical: {}", periodical);
        return periodicalDao.save(periodical);
    }

    /**
     * Stores updated Periodical in the database.
     *
     * @param periodical Periodical to update.
     * @return stored Periodical in the database.
     */
    public Periodical update(Periodical periodical) {
        LOG.debug("Try to update new periodical: {}", periodical);
        return periodicalDao.update(periodical);
    }

    /**
     * Finds Periodical by its id.
     *
     * @param id periodical id.
     * @return Periodical with matching id from the database.
     */
    public Periodical getById(long id) {
        LOG.debug("Try to get periodical by id={}", id);
        return periodicalDao.getById(id);
    }

    /**
     * Deletes periodicals with the matching if from the database. Periodical will be deleted only in case no one is subscribed to it.
     *
     * @param periodicalId Periodical id
     * @throws EntityEngagedException If periodical is in invoice with status COMPLETE, CANCELLED or IN_PROGRESS, exception will be thrown.
     */
    public void deleteById(long periodicalId) throws EntityEngagedException {
        LOG.debug("Try to delete periodical, id={}", periodicalId);

        validatePeriodicalNotInUse(periodicalId);

        periodicalDao.deleteById(periodicalId);
    }

    /**
     * Finds all periodicals by invoice id.
     *
     * @param invoiceId Invoicec id.
     * @return List<Periodical>
     */
    public List<Periodical> findAllByInvoiceId(long invoiceId) {
        LOG.debug("Try to get all periodicals by invoice id={}", invoiceId);
        return periodicalDao.findAllByInvoiceId(invoiceId);
    }

    /**
     * Checks if periodical is related to any invoice with any status.
     * In case any relations are found, exception will be thrown.
     *
     * @param id periodical id.
     * @throws EntityEngagedException In case any relations are found, exception will be thrown.
     */
    private void validatePeriodicalNotInUse(long id) throws EntityEngagedException {

        if (orderItemDao.isPeriodicalInOrderItems(id)) {
            throw new EntityEngagedException(String.format("Periodical id=%d cannot be deleted because someone subscribed to it.", id));
        }

    }
    
}
