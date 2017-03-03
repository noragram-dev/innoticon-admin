package innoticon.backends;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 * Client for Google Apis.
 */
public class GoogleApiClient {

    /** Application name. */
    private static final String APPLICATION_NAME = "Innoticon";

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/calendar-java-quickstart
     */
    private static final List<String> SCOPES =
            Arrays.asList(CalendarScopes.CALENDAR_READONLY);

    private GoogleClientSecrets clientSecrets;

    public GoogleApiClient() {
        try {
            // Load client secrets.
            InputStream in =
                    this.getClass().getClassLoader().getResourceAsStream("client_secret_google.json");
            clientSecrets =
                    GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates and returns an authorized Credential object.
     * @throws IOException
     */
    private GoogleCredential createCredentialWithRefreshToken(String refreshToken) {
        return new GoogleCredential.Builder().setTransport(HTTP_TRANSPORT)
                .setJsonFactory(JSON_FACTORY)
                .setClientSecrets(clientSecrets)
                .build()
                .setRefreshToken(refreshToken);
    }

    /**
     * Build and return an authorized Calendar client service.
     * @throws IOException
     */
    private Calendar getCalendarService(
            String refreshToken) throws IOException {
        return new Calendar.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, createCredentialWithRefreshToken(refreshToken))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Retreives events that user is currently engaged in. return
     * @param refreshToken to use for accessing calendar
     * @return
     * @throws IOException
     */
    public Events getCurrentEvent(String refreshToken) throws IOException {
        Calendar calendarService = getCalendarService(refreshToken);
        DateTime now = new DateTime(System.currentTimeMillis());
        return calendarService.events().list("primary")
                .setMaxResults(10)
                .setTimeMax(now)
                .setTimeMin(now)
                .execute();
    }
}
