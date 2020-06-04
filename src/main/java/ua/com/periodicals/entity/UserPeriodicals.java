package ua.com.periodicals.entity;

import javax.persistence.*;

@Entity
@Table(name = "users_periodicals")
public class UserPeriodicals {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "periodical_id")
    private long periodicalId;

    public UserPeriodicals() {
    }

    public UserPeriodicals(long userId, long periodicalId) {
        this.userId = userId;
        this.periodicalId = periodicalId;
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

    public long getPeriodicalId() {
        return periodicalId;
    }

    public void setPeriodicalId(long periodicalId) {
        this.periodicalId = periodicalId;
    }

    //    @ManyToOne
//    @JoinColumn(name = "user_id")
//    User user;
//
//    @ManyToOne
//    @JoinColumn(name = "periodical_id")
//    Periodical periodical;


}
