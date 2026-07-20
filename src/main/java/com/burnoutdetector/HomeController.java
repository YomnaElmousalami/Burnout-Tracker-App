package com.burnoutdetector;

import com.burnoutdetector.model.BurnoutScore;
import com.burnoutdetector.model.CalendarSummary;
import com.burnoutdetector.repository.BurnoutScoreRepository;
import com.burnoutdetector.service.AiService;
import com.burnoutdetector.service.BurnoutAnalysisService;
import com.burnoutdetector.service.CalendarService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class HomeController {

    private final CalendarService calendarService;
    private final BurnoutAnalysisService burnoutAnalysisService;
    private final AiService aiService;
    private final BurnoutScoreRepository burnoutScoreRepository;

    public HomeController(CalendarService calendarService, BurnoutAnalysisService burnoutAnalysisService, AiService aiService, BurnoutScoreRepository burnoutScoreRepository) {
        this.calendarService = calendarService;
        this.burnoutAnalysisService = burnoutAnalysisService;
        this.aiService = aiService;
        this.burnoutScoreRepository = burnoutScoreRepository;
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
    public String report(@RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient,
                         @AuthenticationPrincipal OidcUser principal,
                         Model model) throws Exception {
        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        CalendarSummary summary = calendarService.getCalendarSummary(accessToken);
        int score = burnoutAnalysisService.calculateScore(summary);
        String recommendations = aiService.getRecommendations(summary, score);
        String scoreColor = score >= 70 ? "#f44336" : score >= 40 ? "#FF9800" : "#4CAF50";
        String riskLevel = score >= 70 ? "High Risk" : score >= 40 ? "Moderate Risk" : "Low Risk";

        burnoutScoreRepository.save(new BurnoutScore(principal.getEmail(), score, riskLevel, LocalDateTime.now()));

        model.addAttribute("score", score);
        model.addAttribute("scoreColor", scoreColor);
        model.addAttribute("riskLevel", riskLevel);
        model.addAttribute("recommendations", recommendations);
        return "report";
    }

    @GetMapping("/history")
    public String history(@AuthenticationPrincipal OidcUser principal, Model model) {
        List<BurnoutScore> scores = burnoutScoreRepository.findByUserEmailOrderByRecordedAtDesc(principal.getEmail());
        model.addAttribute("scores", scores);
        return "history";
    }

}
