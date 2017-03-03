package innoticon.ds;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Class that represents current user state.
 */
public class UserState {

    private EventState eventState = null;

    // Recognized event that the user is currently engaged in.
    public EventState getEventState() {
        return eventState;
    }

    public static Builder Builder(){
        return new Builder();
    }

    //Builder Class
    public static class Builder {

        private UserState userState;

        Builder(){
            userState = new UserState();
        }

        public Builder setEventState(Events events) {
            List<Event> eventList = events.getItems();
            if (eventList.size() == 0) {
                userState.eventState = new EventState(EventState.EventType.EMPTY, null);
            } else {
                // Find the first non-unknown event; if not just use the first.
                for (Event event : eventList) {
                    EventState.EventType type = EventState.convertToType(event.getSummary());
                    if (type != EventState.EventType.UNKNOWN) {
                        userState.eventState = new EventState(type, event.getLocation());
                        break;
                    }
                }
                if (userState.eventState == null) {
                    userState.eventState = new EventState(
                            EventState.EventType.UNKNOWN, eventList.get(0).getLocation());
                }
            }
            return this;
        }

        public UserState build(){
            return userState;
        }
    }

    // Recognized event that the user is currently engaged in.
    public static class EventState {

        public enum EventType {
            EMPTY,      // No calendar event found
            MEETING,    // In a meeting
            EATING,     // Having meal
            PARTYING,   // Party event
            STUDYING,   // Studying
            FLIGHT,     // On a flight
            UNKNOWN,    // Unknown event
        }

        private static Map<EventType, Pattern> typeMatcher = ImmutableMap.<EventType, Pattern>builder()
                .put(EventType.EATING, regexJoin("meal", "breakfast", "lunch", "dinner", "식사", "아침", "점심", "저녁"))
                .put(EventType.MEETING, regexJoin("meeting", "미팅", "회의"))
                .put(EventType.PARTYING, regexJoin("party", "파티"))
                .put(EventType.STUDYING, regexJoin("study", "lecture", "class", "스터디", "공부", "수업"))
                .put(EventType.FLIGHT, regexJoin("flight to"))
            .build();

        private static Pattern regexJoin(String... args) {
            return Pattern.compile("(" + String.join("|", args) + ")", Pattern.CASE_INSENSITIVE);
        }

        final EventType eventType;
        final String location;

        public EventState(EventType eventType, String location) {
            this.eventType = eventType;
            this.location = location;
        }

        public EventType getEventType() {
            return eventType;
        }

        public String getLocation() {
            return location;
        }

        // Rough event type recognition. Needs to be more sophisticated
        private static EventType convertToType(String eventSummary) {
            for (Map.Entry<EventType, Pattern> typeMatch : typeMatcher.entrySet()) {
                if(typeMatch.getValue().matcher(eventSummary).matches()) {
                    return typeMatch.getKey();
                }
            }
            return EventType.UNKNOWN;
        }
    }

    private UserState() {}
}
