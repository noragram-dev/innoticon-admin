package firebase.rx;

import com.google.firebase.database.*;
import novemberizing.ds.tuple.Pair;

/**
 *
 * @author novemberizing, me@novemberizing.net
 * @since 2017. 2. 25
 */
@SuppressWarnings("DanglingJavadoc")
public class Set extends novemberizing.rx.operators.Sync<Pair<String, Object>,Exception> {

    @Override
    protected void on(Task<Pair<String, Object>, Exception> task, Pair<String, Object> in) {
        if(in!=null){
            if(in.first!=null && in.first.length()>0) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference(in.first);
                reference.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        mutableData.setValue(in.second);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError error, boolean commit, DataSnapshot snapshot) {
                        if(error!=null){
                            task.error(error.toException(), true);
                        } else {
                            task.complete();
                        }
                    }
                });
            } else {
                task.error(new RuntimeException("in.first!=null && in.first.length()>0"), true);
            }
        } else {
            task.error(new RuntimeException("in=null"), true);
        }
    }

}
