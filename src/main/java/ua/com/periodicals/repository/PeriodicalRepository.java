package ua.com.periodicals.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.periodicals.entity.Periodical;

public interface PeriodicalRepository extends JpaRepository<Periodical, Long> {


}
