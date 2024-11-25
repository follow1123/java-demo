package cn.y.java.api;

import cn.y.java.TomcatServerExt;
import cn.y.java.minichatjavaweb.dto.response.StatusResult;
import cn.y.java.minichatjavaweb.entities.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.session.StandardSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Objects;

import static cn.y.java.minichatjavaweb.constants.UserConstant.SESSION_KEY;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TomcatServerExt.class)
public class AuthenticationServletTest {

    @ParameterizedTest
    @CsvSource({
            "username=123&password=2145",
            "username=&password=2145",
            "username=123&password=",
            "password=2145",
            "username=123",
            "''"
    })
    public void testLoginFailed(String query) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(TomcatServerExt.buildURI("/auth/login", query))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        String body = client.send(req, HttpResponse.BodyHandlers.ofString()).body();
        StatusResult result = new ObjectMapper().readValue(body, StatusResult.class);
        assertFalse(result.status());
    }

    @Test
    public void testLoginSucceed() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(TomcatServerExt.buildURI("/auth/login", "username=zs&password=123"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        List<HttpCookie> cookies = resp.headers().allValues("Set-Cookie")
                .stream().map(HttpCookie::parse)
                .flatMap(List::stream)
                .toList();
        String uname = null;
        String uid = null;
        User user = null;
        ObjectMapper om = new ObjectMapper();
        for (HttpCookie cookie : cookies) {
            if (Objects.equals("JSESSIONID", cookie.getName())){
                assertNotNull(cookie.getValue());
                StandardSession session = TomcatServerExt.getSession(cookie.getValue());
                assertNotNull(session);
                Object obj = session.getAttribute(SESSION_KEY);
                assertNotNull(obj);
                /*
                     由于Tomcat内部使用的类加载器和这个测试环境使用的类加载器不是同一个
                     直接强转成User对象会报错，所以使用json方式复制
                 */
                user = om.readValue(om.writeValueAsString(obj), User.class);
            }else if (Objects.equals("uname", cookie.getName())){
                assertNotNull(cookie.getValue());
                uname = cookie.getValue();
            }else if (Objects.equals("uid", cookie.getName())){
                assertNotNull(cookie.getValue());
                uid = cookie.getValue();
            }
        }

        // 测试发送的Cookie内容是否和Tomcat容器内Session的内容对的上
        assertEquals(user.getUsername(), uname);
        assertEquals(user.getId().toString(), uid);

        StatusResult result = om.readValue(resp.body(), StatusResult.class);
        assertTrue(result.status());
    }

}
