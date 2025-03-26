package vttpb_final_project.travelplanner.controllers;

import java.net.URI;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;

import jakarta.servlet.http.HttpSession;
import vttpb_final_project.travelplanner.services.GoogleCalendarService;

@RestController
@RequestMapping("/api/calendar")
public class GoogleCalendarController {

    private final GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow;
    private static final Logger logger = Logger.getLogger(GoogleCalendarController.class.getName());

    @Autowired
    private GoogleCalendarService calendarService;

    @Autowired
    public GoogleCalendarController(GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow) {
        this.googleAuthorizationCodeFlow = googleAuthorizationCodeFlow;
    }

    @Value("${google.client.redirect.uri}")
    private String redirectUri;

    @GetMapping("/auth-url")
    public ResponseEntity<Map<String, String>> getAuthorizationUrl(@RequestParam(required = true) String username) throws Exception {
        String authUrl = calendarService.generateAuthorizationUrl(redirectUri, username);

        // Return JSON response with authUrl
        return ResponseEntity.ok(Map.of("authUrl", authUrl));
    }

    @GetMapping("/oauth-callback")
    public ResponseEntity<?> handleOAuthCallback(
            @RequestParam(value = "username", required = false) String username,
            @RequestParam("code") String code,
            @RequestParam("state") String state,
            HttpSession session) {

        String effectiveUsername = username != null ? username : state;

        if (effectiveUsername == null || effectiveUsername.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Missing username parameter"
            ));
        }

        try {
            boolean success = calendarService.handleOAuthCallback(code, redirectUri, session, username);
            if (success) {
                // HttpHeaders headers = new HttpHeaders();
                // headers.setLocation(URI.create("http://localhost:4200/home"));
                // return new ResponseEntity<>(headers, HttpStatus.FOUND);
                return ResponseEntity.ok().body(Map.of(
                        "status", "success",
                        "message", "Google Calendar connected successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("status", "error", "message", "Authentication failed"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @GetMapping("/google-status")
    public ResponseEntity<Map<String, Boolean>> getGoogleAuthStatus(HttpSession session) {
        try {
            String userId = (String) session.getAttribute("GOOGLE_USER_ID");

            // Check if user ID exists in session
            if (userId == null) {
                return ResponseEntity.ok(Map.of("authenticated", false));
            }

            // Try to load credential
            Credential credential = googleAuthorizationCodeFlow.loadCredential(userId);

            // Check if credential exists and is valid
            boolean isAuthenticated = credential != null
                    && credential.getAccessToken() != null
                    && !credential.getAccessToken().isEmpty();

            return ResponseEntity.ok(Map.of("authenticated", isAuthenticated));
        } catch (Exception e) {
            logger.warning("Error checking Google authentication status: %s".formatted(e));
            return ResponseEntity.ok(Map.of("authenticated", false));
        }
    }

    @PostMapping("/google-disconnect")
    public ResponseEntity<Map<String, String>> disconnectGoogleCalendar(HttpSession session) {
        try {
            String userId = (String) session.getAttribute("GOOGLE_USER_ID");

            if (userId != null) {
                // Revoke the token if possible
                Credential credential = googleAuthorizationCodeFlow.loadCredential(userId);

                if (credential != null && credential.getAccessToken() != null) {
                    // Create HTTP transport
                    HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

                    // Revoke token
                    String revokeUrl = "https://oauth2.googleapis.com/revoke?token=" + credential.getAccessToken();
                    HttpRequest request = httpTransport.createRequestFactory()
                            .buildGetRequest(new GenericUrl(revokeUrl));

                    HttpResponse response = request.execute();

                    // Remove credential from data store
                    googleAuthorizationCodeFlow.getCredentialDataStore().delete(userId);
                }

                // Remove user ID from session
                session.removeAttribute("GOOGLE_USER_ID");
            }

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Successfully disconnected from Google"));
        } catch (Exception e) {
            logger.warning("Error disconnecting Google account: %s".formatted(e));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", "Failed to disconnect Google account"));
        }
    }

    @PostMapping("/sync-meal-plans")
    public ResponseEntity<?> syncMealPlans(HttpSession session, @RequestParam String username) {
        try {
            String userId = (String) session.getAttribute("GOOGLE_USER_ID");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("status", "error", "message", "Not authenticated"));
            }

            int syncedEvents = calendarService.syncMealPlans(userId, username);
            return ResponseEntity.ok().body(Map.of(
                    "status", "success",
                    "message", syncedEvents + " meal plans synced"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}