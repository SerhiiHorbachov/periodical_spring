package ua.com.periodicals.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.periodicals.entity.UserPeriodicals;

public interface UserPeriodicalsRepository extends JpaRepository<UserPeriodicals, Long> {

    UserPeriodicals findByUserIdAndAndPeriodicalId(long userId, long periodicalId);

    int deleteByUserIdAndAndPeriodicalId(long userId, long periodicalId);
}
