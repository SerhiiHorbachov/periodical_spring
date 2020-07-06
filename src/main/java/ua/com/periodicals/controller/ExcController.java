package ua.com.periodicals.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Serhii Hor
 * @since 2020-06
 */
@Controller
public class ExcController {

    @GetMapping("/accessDenied")
    public ModelAndView accessDenied(){
        ModelAndView mav = new ModelAndView("accessDenied");

        return mav;

    }
}
