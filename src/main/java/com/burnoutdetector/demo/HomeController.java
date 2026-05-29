package com.burnoutdetector.demo;

import com.burnoutdetector.demo.model.CalendarSummary;
import com.burnoutdetector.demo.service.CalendarService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final CalendarService calendarService;

    public HomeController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(@RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient,
                            Model model) throws Exception {
        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        CalendarSummary summary = calendarService.getCalendarSummary(accessToken);
        model.addAttribute("summary", summary);
        return "dashboard";
    }

    @GetMapping("/report")
    public String report() {
        return "report";
    }

}