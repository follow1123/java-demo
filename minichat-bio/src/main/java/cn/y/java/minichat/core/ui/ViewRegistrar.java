package cn.y.java.minichat.core.ui;

import java.util.ArrayList;
import java.util.List;

public class ViewRegistrar {

    private final List<View> views = new ArrayList<>();

    private int authenticationViewIndex = 0;

    private int curViewIndex = 0;

    public void registerView(View view, boolean authenticationView){
        views.add(view);
        if (authenticationView){
            authenticationViewIndex = views.size() - 1;
        }
    }

    public void registerView(View view){
        registerView(view, false);
    }

    private void assertCorrectIndex(int index){
        if (index < 0 || index > views.size() - 1) throw new RuntimeException("no view on index: " + index);
    }

    public View getView(int index, boolean move){
        assertCorrectIndex(index);
        if (move) curViewIndex = index;
        return views.get(index);
    }

    public View getView(int index){
        return getView(index, false);
    }

    public View nextView(){
        return getView(curViewIndex + 1, true);
    }

    public View previousView(){
        return getView(curViewIndex - 1, true);
    }

    public View getCurrView(){
        return views.get(curViewIndex);
    }

    public View getAuthView(){
        return views.get(authenticationViewIndex);
    }
}
