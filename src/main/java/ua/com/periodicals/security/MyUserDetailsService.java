package ua.com.periodicals.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.com.periodicals.dao.UserDao;
import ua.com.periodicals.entity.User;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(MyUserDetailsService.class);

    @Autowired
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userDao.findByEmail(email).orElseThrow(
            () -> new UsernameNotFoundException("User not found"));

        return new UserPrincipal(user);
    }

    public User getLoggedUser() {
        LOG.debug("Try to get logged user");
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDao.findById(principal.getId());
    }

}
