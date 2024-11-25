package cn.y.java.minichatjavaweb.dto.response;

public record StatusResult(boolean status, String message) {

    public StatusResult(boolean status) {
        this(status, null);
    }

    public StatusResult(String message){
        this(false, message);
    }
}
