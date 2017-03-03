package firebase;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 *
 * @author novemberizing, me@novemberizing.net
 * @since 2017. 2. 2.
 */
@SuppressWarnings("unused")
public class Admin {

    public static void Init(String json) throws FileNotFoundException {
        FileInputStream serviceAccount = new FileInputStream(json);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
                .setDatabaseUrl("https://noragram-2b9d8.firebaseio.com/")
                .build();

        FirebaseApp.initializeApp(options);
    }
}
