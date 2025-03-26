package vttpb_final_project.travelplanner.config;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.client.json.JsonFactory;

@Configuration
public class GoogleCalendarConfig {
    
    @Value("${google.client.client.id}")
    private String clientId;

    @Value("${google.client.client.secret}")
    private String clientSecret;

    @Value("${google.client.redirect.uri}")
    private String redirectUri;

    @Bean
    public GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow() throws Exception {
        GoogleClientSecrets.Details web = new GoogleClientSecrets.Details();
        web.setClientId(clientId);
        web.setClientSecret(clientSecret);

        GoogleClientSecrets clientSecrets = new GoogleClientSecrets().setWeb(web);
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        return new GoogleAuthorizationCodeFlow.Builder(
            httpTransport, 
            jsonFactory, 
            clientSecrets,
            Collections.singleton(CalendarScopes.CALENDAR)
        ).build();
    }
}
