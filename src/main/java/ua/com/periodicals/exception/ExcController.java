package ua.com.periodicals.exception;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ExcController {

    @GetMapping("/accessDenied")
    public ModelAndView accessDenied(){
        ModelAndView mav = new ModelAndView("accessDenied");

        return mav;

    }
}
