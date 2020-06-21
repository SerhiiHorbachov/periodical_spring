package ua.com.periodicals.dao;

import ua.com.periodicals.entity.Periodical;

import java.util.List;

public interface PeriodicalDao {
    List<Periodical> findAll();

    List<Periodical> findPerPage(int firstResult, int maxResults);

    Periodical save(Periodical periodical);

    Periodical getById(long id);

    Periodical update(Periodical periodical);

    void deleteById(long id);

    long getCount();

    List<Periodical> findAllByInvoiceId(long id);
}
