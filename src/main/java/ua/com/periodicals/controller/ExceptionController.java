package ua.com.periodicals.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ExceptionController {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionController.class);

    @ExceptionHandler(value = Throwable.class)
    public ModelAndView defaultHandler(HttpServletRequest request, Exception ex) {

        LOG.error("Request " + request.getRequestURL() + " threw an Exception: ", ex);

        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", ex.getMessage());
        return modelAndView;

    }


}
