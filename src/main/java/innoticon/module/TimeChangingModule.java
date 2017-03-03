package innoticon.module;

import com.google.common.collect.ImmutableList;
import innoticon.ds.EmojiCollection;
import innoticon.ds.EmojiCollection.Tag;
import innoticon.ds.Message;
import innoticon.msg.Emoticon;
import javafx.util.Duration;

import java.util.List;

/**
 * Module for calculating innoticons changing as time passes.
 */
public class TimeChangingModule {
    private static EmojiCollection collection = EmojiCollection.getInstance();

    private static double ONE_HOUR = Duration.hours(1).toMillis();
    private static double TWO_HOUR = Duration.hours(2).toMillis();
    private static double FOUR_HOUR = Duration.hours(4).toMillis();
    private static double HALF_DAY = Duration.hours(12).toMillis();

    public static List<Message> changeByTime(long startTimestamp, String collectionId) {
        long passedTime = System.currentTimeMillis() - startTimestamp;
        Tag nextTag;
        if (passedTime < ONE_HOUR) {
            return null;
        } else if (passedTime < TWO_HOUR) {
            nextTag = Tag.WAITING;
        } else if (passedTime < FOUR_HOUR) {
            nextTag = Tag.SAD;
        } else if (passedTime < HALF_DAY) {
            nextTag = Tag.CRYING;
        } else {
            nextTag = Tag.SLEEPING;
        }
        String id = collection.getIdForTag(collectionId, nextTag);
        if (id == null) {
            return null;
        }

        Emoticon nextEmoji = new Emoticon();
        nextEmoji.resourceId = collectionId + "/" + id;
        return ImmutableList.of(nextEmoji);
    }
}
