package account.logging;


import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


public class LogInfoAggregator {

    private static final ThreadLocal<String> urlPathHolder = new ThreadLocal<>();
    private static final ThreadLocal<String> userinfo = new ThreadLocal<>();

    private static final ThreadLocal<String> objectInfo = new ThreadLocal<>();

    public static String getUrlPath() {
        return urlPathHolder.get();
    }
    public static void setUrlPathForLogging(String urlPath) {
        urlPathHolder.set(urlPath);
    }

    public static void setUserNameForLogging(String username) {
        userinfo.set(username);
    }

    public static String getUserInfo() {
        return userinfo.get();
    }

    public static void setObjectInfoForLogging(String info) {
        objectInfo.set(info);
    }

    public static String getObectInfo() {
      return  objectInfo.get();
    }

    public static void removeThreads () {
        userinfo.remove();
        urlPathHolder.remove();
        objectInfo.remove();

    }
}
