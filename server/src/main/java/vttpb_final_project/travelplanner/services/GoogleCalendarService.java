package vttpb_final_project.travelplanner.services;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import jakarta.servlet.http.HttpSession;
import vttpb_final_project.travelplanner.models.Meal;
import vttpb_final_project.travelplanner.repositories.MealRepository;

@Service
public class GoogleCalendarService {

    @Autowired
    private MealRepository mealRepo;

    private static final Logger logger = Logger.getLogger(GoogleCalendarService.class.getName());
    private static final String APPLICATION_NAME = "PrepEase";
    private static final String DEFAULT_TIME_ZONE = "Asia/Singapore";

    @Autowired
    private GoogleAuthorizationCodeFlow flow;

    public String generateAuthorizationUrl(String redirectUri, String username) throws Exception {
        return flow.newAuthorizationUrl()
            .setRedirectUri(redirectUri)
            .setApprovalPrompt("force")
            .setState(username)
            .build();
    }

    public boolean handleOAuthCallback(String code, String redirectUri, HttpSession session, String username) throws Exception {
        TokenResponse tokenResponse = flow.newTokenRequest(code)
            .setRedirectUri(redirectUri)
            .execute();

        String userId = determineUserId(session);
        Credential credential = flow.createAndStoreCredential(tokenResponse, userId);

        return credential != null;
    }

    public int syncMealPlans(String userId, String username) throws Exception {
        Credential credential = flow.loadCredential(userId);
        if (credential == null) {
            throw new Exception("No valid credentials found");
        }

        Calendar service = new Calendar.Builder(
            GoogleNetHttpTransport.newTrustedTransport(), 
            GsonFactory.getDefaultInstance(), 
            credential
        ).setApplicationName(APPLICATION_NAME).build();

        // Fetch meal plans from your database
        List<Meal> mealPlans = mealRepo.getMealPlanForUser(username);
        
        int syncedEvents = 0;
        for (Meal meal : mealPlans) {
            Event event = createCalendarEvent(meal);
            service.events().insert("primary", event).execute();
            syncedEvents++;
        }

        return syncedEvents;
    }

    private Event createCalendarEvent(Meal meal) {
        Event event = new Event()
            .setSummary(meal.getMealDetails().getTitle())
            .setDescription(meal.getMealTime());

        DateTime startDateTime = new DateTime(meal.getMealDate());
        DateTime endDateTime = new DateTime(startDateTime.getValue() + 86400000);

        EventDateTime start = new EventDateTime()
            .setDateTime(startDateTime)
            .setTimeZone(DEFAULT_TIME_ZONE);

        EventDateTime end = new EventDateTime()
            .setDateTime(endDateTime)
            .setTimeZone(DEFAULT_TIME_ZONE);

        event.setStart(start);
        event.setEnd(end);

        return event;
    }

    private String determineUserId(HttpSession session) {
        String userId = (String) session.getAttribute("GOOGLE_USER_ID");
        if (userId == null) {
            userId = UUID.randomUUID().toString();
            session.setAttribute("GOOGLE_USER_ID", userId);
        }
        return userId;
    }
}
