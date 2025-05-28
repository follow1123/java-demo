package org.example.minichatjavaweb;

import lombok.Getter;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.session.StandardSession;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.File;
import java.net.URI;
import java.util.Objects;

public class TomcatServerExt implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {

    @Getter
    private static Tomcat tomcat;

    private void startTomcat() throws LifecycleException {
        tomcat = new Tomcat();
        tomcat.setPort(8081);
        tomcat.getConnector();
        // String webPath = new File("minichat-javaweb/src/main/webapp").getAbsolutePath();
        // String classPath = new File("minichat-javaweb/target/classes").getAbsolutePath();
        String webPath = new File("src/main/webapp").getAbsolutePath();
        String classPath = new File("target/classes").getAbsolutePath();
        Context ctx = tomcat.addWebapp("", webPath);

        WebResourceRoot resourceRoot = new StandardRoot(ctx);
        DirResourceSet dirResourceSet = new DirResourceSet(resourceRoot, "/WEB-INF/classes", classPath, "/");
        resourceRoot.addPreResources(dirResourceSet);
        ctx.setResources(resourceRoot);

        tomcat.start();
    }

    public static String getAddress() {
        return tomcat.getServer().getAddress();
    }

    public static int getPort() {
        return tomcat.getConnector().getPort();
    }

    public static URI buildURI(String path, String query) {
        return URI.create(String.format("http://%s:%d%s?%s", getAddress(), getPort(), path, query));
    }

    public static StandardSession getSession(String id) throws Exception {
        Context ctx = (Context) tomcat.getHost().findChild("");
        return (StandardSession) ctx.getManager().findSession(id);
    }


    public static URI buildURI(String path) {
        return buildURI(path, "");
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        if (!Objects.isNull(tomcat)) return;
        synchronized (TomcatServerExt.class) {
            if (Objects.isNull(tomcat)) {
                startTomcat();
                extensionContext.getRoot().getStore(ExtensionContext.Namespace.GLOBAL)
                        .put(getClass().getName(), this);
            }
        }
    }


    @Override
    public void close() throws Throwable {
        synchronized (TomcatServerExt.class) {
            if (!Objects.isNull(tomcat)) {
                tomcat.stop();
            }
        }
    }
}
