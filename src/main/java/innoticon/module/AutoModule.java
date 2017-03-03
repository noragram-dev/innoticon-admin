package innoticon.module;

import com.google.gson.Gson;
import innoticon.backends.GoogleApiClient;
import innoticon.client.req.api.Enable;
import innoticon.ds.Message;
import innoticon.ds.UserState;

import java.io.IOException;
import java.util.List;

/**
 * Module that calculates and determines calculate responses from users.
 */
public class AutoModule {

    private GoogleApiClient googleApiClient;
    private Gson gson;

    public AutoModule(GoogleApiClient googleApiClient, Gson gson) {
        this.googleApiClient = googleApiClient;
        this.gson = gson;
    }

    /**
     * Calculates if auto message set can be generated. If none generated, returns null.
     * @param uid receiver's uid, to make auto response for.
     * @param success called upon success. May return null if auto response can't be generated.
     * @param fail called when failure occurs.
     */
    public void autoRespond(String uid,
                            novemberizing.ds.on.Single<List<Message>> success,
                            novemberizing.ds.on.Single<Throwable> fail) {
        UserState.Builder builder = UserState.Builder();
        // Fetch Event from CALENDAR API
        innoticon.rx.Data.Get(
                client.db.Path.getUserApiPath(uid, Enable.Type.CALENDAR.name()), String.class, gson)
        .exist(token->{
            try {
                builder.setEventState(googleApiClient.getCurrentEvent(token));
            } catch (IOException e) {
                e.printStackTrace();
            }
            // TODO(insunj): expand user state model.

            success.on(InnoticonEngine.calculate(builder.build()));
        }, e->{
            // Can happen if the api not enabled.
            fail.on(e);
        });
    }
}
