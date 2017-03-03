package server;

/**
 *
 * @author novemberizing, me@novemberizing.net
 * @since 2017. 2. 9.
 */
public class Exception {
    private static final int BASE = 0;

    private static final int KAFKA = 100000;
    public static final int FAIL_TO_DESERIALIZE_STRING_TO_REQ = KAFKA + 1;
    public static final int FAIL_TO_SET_REQ = KAFKA + 2;
    public static final int NOT_EXIST_CALLBACK = KAFKA + 3;
    public static final int FAIL_TO_GENERATE_RES = KAFKA + 4;
    public static final int FAIL_TO_SET_RES = KAFKA + 5;
    public static final int FAIL_TO_CALLBACK = KAFKA + 6;
    public static final int FAIL_TO_GET_RESPONSE_PATH = KAFKA + 7;

}
