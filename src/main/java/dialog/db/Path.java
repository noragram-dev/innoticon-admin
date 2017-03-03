package dialog.db;

import innoticon.ds.To;
import novemberizing.util.Log;

/**
 *
 * @author novemberizing, me@novemberizing.net
 * @since 2017. 2. 19.
 */
public class Path {
    public static String getEnvelopePath(innoticon.ds.Envelope envelope, To destination){
        if(envelope==null){
            Log.e("dialog.db:error>", "envelope==null");
            return null;
        }
        if(envelope.from==null ||  envelope.from.user==null || envelope.from.user.uid==null){
            Log.e("dialog.db:error>", "invalid envelope.from");
            return null;
        }
        if(destination==null){
            Log.e("dialog.db:error>", "destination==null");
            return null;
        }
        if(destination.type==To.Type.Dialog){
            Log.e("dialog.db:error>", "currently not support");
            return null;
        }
        if(destination.type==To.Type.DialogUser){
            Log.e("dialog.db:error>", "currently not support");
            return null;
        }
        if(destination.type==To.Type.User){
            if(destination.user==null || destination.user.uid==null){
                Log.e("dialog.db:error>", "invalid innoticon.ds.To.user");
                return null;
            }
            return client.db.Path.getUserEnvelopePath(destination.user.uid(), envelope);
        }
        Log.e("dialog.db:error>", "unknown destination type.");
        return null;
    }

    public static String getEnvelopePath(innoticon.ds.Envelope envelope, innoticon.ds.From source){
        if(envelope==null){
            Log.e("dialog.db:error>", "envelope==null");
            return null;
        }
        if(envelope.from==null ||  envelope.from.user==null || envelope.from.user.uid==null){
            Log.e("dialog.db:error>", "invalid envelope.from");
            return null;
        }
        if(source==null){
            Log.e("dialog.db:error>", "destination==null");
            return null;
        }


        if(source.user==null || source.user.uid==null){
            Log.e("dialog.db:error>", "invalid innoticon.ds.To.user");
            return null;
        }
        return client.db.Path.getUserEnvelopePath(source.user.uid(), envelope);

    }
}
