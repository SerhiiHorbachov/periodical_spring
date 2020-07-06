package ua.com.periodicals.controller;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.com.periodicals.dto.UserDto;
import ua.com.periodicals.entity.User;
import ua.com.periodicals.exception.DuplicateRecordException;
import ua.com.periodicals.service.UserService;

import javax.validation.Valid;

/**
 * @author Serhii Hor
 * @since 2020-06
 */
@Controller
public class SecurityController {

    private static final Logger LOG = (Logger) LoggerFactory.getLogger(SecurityController.class);

    private User user;

    @Autowired
    private UserService userService;

    @RequestMapping("/login")
    public String loginPage() {
        LOG.debug("Try to show login page");
        return "login";
    }

    @RequestMapping("/logout-success")
    public String logoutPage() {
        LOG.debug("Try to show logout success");
        return "logout";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        LOG.debug("Try to show register page");

        model.addAttribute("user", new UserDto());
        return "register";
    }


    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") @Valid UserDto user,
                               BindingResult bindingResult, Model model) {

        LOG.debug("Try to save new user: {}", user);
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            userService.register(user);
        } catch (DuplicateRecordException e) {
            LOG.warn("Already registered email was user: {}", user.getEmail());

            model.addAttribute("emailExists", true);
            return "register";
        }

        return "register-success";
    }

}
