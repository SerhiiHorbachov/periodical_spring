package ua.com.periodicals.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.periodicals.entity.Periodical;
import ua.com.periodicals.repository.PeriodicalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class PeriodicalService {

    private static final Logger LOG = LoggerFactory.getLogger(PeriodicalService.class);

    @Autowired
    PeriodicalRepository periodicalRepository;

    public List<Periodical> getAllPeriodicals() {
        List<Periodical> periodicals = periodicalRepository.findAll();
        if (periodicals.size() > 0) {
            return periodicals;
        } else {
            return new ArrayList<Periodical>();
        }
    }

    @Transactional
    public Periodical save(Periodical periodical) {
        return periodicalRepository.save(periodical);
    }

    public Periodical getById(long id) {
        LOG.debug("Try to get periodical by id={}", id);
        Optional<Periodical> periodical = periodicalRepository.findById(id);

        if (!periodical.isPresent()) {
            LOG.info("Periodical id={} not found");
            throw new NoSuchElementException(String.format("Periodical id= %d not found", id));
        }

        return periodical.get();
    }

    @Transactional
    public void deleteById(long periodicalId) {
        LOG.debug("Try to delete periodical, id={}", periodicalId);
        periodicalRepository.deleteById(periodicalId);
    }

}
