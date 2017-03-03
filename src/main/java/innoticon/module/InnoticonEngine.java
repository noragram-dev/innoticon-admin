package innoticon.module;

import innoticon.ds.Message;
import innoticon.ds.UserState;

import java.util.List;

/**
 * Engine that determines the special msg/changes.
 */
public class InnoticonEngine {

    // Calculates the most likely message set for userState
    public static List<Message> calculate(UserState userState) {
        UserState.EventState eventState = userState.getEventState();
        if (eventState == null) {
            return null;
        }

        switch(eventState.getEventType()) {
            case MEETING:
                break;
            case EATING:
                break;
            case PARTYING:
            case STUDYING:
            case FLIGHT:
            case UNKNOWN:
                // Maps to busy
                break;
        }
        return null;
    }
}
