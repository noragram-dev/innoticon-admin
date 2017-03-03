package innoticon.ds;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton class
 */
public class EmojiCollection {

    public enum Tag {
        HAPPY, OK, HELLO, SAD, CRYING, DONTKNOW, WAITING, SNEAKY, MOVING, SLEEPING, PASSIVE, LAUGHING, EATING
    }

    private static EmojiCollection singleton;

    private Map<String, Emoji> collection;

    /**
     * Static method to get instance.
     */
    public static EmojiCollection getInstance(){
        if(singleton == null){
            singleton = new EmojiCollection();
        }
        return singleton;
    }

    private EmojiCollection(){
        collection = ImmutableMap.of(
                "giraffee",
                new Emoji(
                        "Giraffee",
                        ImmutableMap.of(
                            "1", ImmutableList.of(Tag.HAPPY, Tag.OK),
                            "2", ImmutableList.of(Tag.HAPPY, Tag.OK),
                            "3", ImmutableList.of(Tag.HELLO),
                            "4", ImmutableList.of(Tag.DONTKNOW),
                            "5", ImmutableList.of(Tag.WAITING, Tag.SNEAKY)
                        )
                ),
                "shibamon",
                new Emoji(
                        "Shibamon",
                        ImmutableMap.of(
                                "5", ImmutableList.of(Tag.MOVING),
                                "6", ImmutableList.of(Tag.SLEEPING),
                                "13", ImmutableList.of(Tag.HAPPY, Tag.OK),
                                "17", ImmutableList.of(Tag.PASSIVE),
                                "19", ImmutableList.of(Tag.LAUGHING)
                        )
                )

        );
    }

    public String getIdForTag(String collectionId, Tag tag) {
        if(collection.containsKey(collectionId)) return null;
        return collection.get(collectionId).getIdForTag(tag);
    }

    public List<Tag> getTagsForId(String collectionId, String id) {
        if(collection.containsKey(collectionId)) return null;
        return collection.get(collectionId).getTagsForId(id);
    }

    class Emoji {
        private final String displayName;
        private final Map<String, List<Tag>> idToTags;
        private final Map<Tag, List<String>> tagsToId;

        Emoji(String displayName, Map<String, List<Tag>> idToTags){
            this.displayName = displayName;
            this.idToTags = idToTags;
            this.tagsToId = new HashMap<Tag, List<String>>();
            for (Map.Entry<String, List<Tag>> entry : this.idToTags.entrySet()) {
                for (Tag tag : entry.getValue()) {
                    if (!this.tagsToId.containsKey(tag)) {
                        this.tagsToId.put(tag, new ArrayList<>());
                    } else {
                        this.tagsToId.get(tag).add(entry.getKey());
                    }
                }
            }
        }

        String getIdForTag(Tag tag){
            if (this.tagsToId.containsKey(tag)) {
                List<String> validIds = this.tagsToId.get(tag);
                int idIndex = (int) (Math.random() * validIds.size());
                return validIds.get(idIndex);
            }
            return null;
        }

        List<Tag> getTagsForId(String id){
            return this.idToTags.get(id);
        }
    }
}
