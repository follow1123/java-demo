package cn.y.java.minichat.core;

import cn.y.java.minichat.core.ui.ViewRegistrar;

import java.util.HashMap;
import java.util.Map;

public class ClientContext {

    private String token;
    private String username;
    private final MsgBus msgBus;
    private final Map<String, String> viewData = new HashMap<>();
    private final ViewRegistrar viewRegistrar;

    public ClientContext(MsgBus msgBus, ViewRegistrar viewRegistrar) {
        this.msgBus = msgBus;
        this.viewRegistrar = viewRegistrar;
    }

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}
    public String getToken() {return token;}
    public void setToken(String token) {this.token = token;}
    public MsgBus getMsgBus() {return msgBus;}
    public boolean isRunning() {return msgBus.isConnected();}

    public void nextView() {
        viewRegistrar.nextView();
    }

    public void addData(String name, String data){
        viewData.put(name, data);
    }

    public String getData(String friendName) {
        return viewData.get(friendName);
    }

    public void stop() {
        msgBus.disconnect();
    }
}
