package innoticon.ds;

import com.google.gson.annotations.Expose;

/**
 *
 * @author novemberizing, me@novemberizing.net
 * @since 2017. 2. 12.
 */
public class Event<REQ extends Req, RES extends Res> {
    @Expose public REQ req;
    @Expose public RES res;

    public REQ req(){ return req; }
    public RES res(){ return res; }

    public REQ req(REQ req){ return this.req = req; }
    public RES res(RES res){ return this.res = res; }

    public Event(REQ req, RES res){
        this.req = req;
        this.res = res;
    }

    public Event(){
        req = null;
        res = null;
    }
}
