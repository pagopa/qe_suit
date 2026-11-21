package it.frontend.e2e.framework.web.adapter.selenium;

import com.sun.net.httpserver.HttpServer;
import it.frontend.e2e.framework.core.model.selector.XPathSelector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Opt-in local-browser validation: -DrequestCapture.browserTests=true.
 * Requires an installed Chrome browser and matching driver; uses no remote application.
 */
@EnabledIfSystemProperty(named = "requestCapture.browserTests", matches = "true")
class SeleniumRequestUrlsBrowserTest {
    private HttpServer server;
    private ExecutorService executor;
    private WebDriver driver;
    private String baseUrl;

    @BeforeEach
    void startLocalPage() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        executor = Executors.newCachedThreadPool();
        server.setExecutor(executor);
        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/prior")) {
                try {
                    Thread.sleep(700);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    exchange.close();
                    return;
                }
            }
            byte[] body = (path.equals("/") ? """
                    <button id="capture" onclick="
                        fetch('/api?state=REJECTED').then(r => r.text())
                            .then(() => fetch('/api?state=REJECTED')).then(r => r.text());
                        const xhr = new XMLHttpRequest();
                        xhr.open('GET', '/xhr?offset=0'); xhr.send();
                        new Image().src='/image';
                    ">Capture</button>
                    <button id="empty">Empty</button>
                    <button id="busy" onclick="
                        window.polling = setInterval(() => fetch('/poll').then(r => r.text()), 100);
                    ">Busy</button>
                    """ : "ok").getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", path.equals("/") ? "text/html" : "text/plain");
            exchange.getResponseHeaders().set("Cache-Control", "no-store");
            exchange.sendResponseHeaders(200, body.length);
            try (var output = exchange.getResponseBody()) {
                output.write(body);
            }
        });
        server.start();
        baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
        driver = new ChromeDriver(new ChromeOptions().addArguments("--headless=new"));
        driver.get(baseUrl + "/");
    }

    @AfterEach
    void stopLocalPage() {
        try {
            if (driver != null) {
                driver.quit();
            }
        } finally {
            if (server != null) {
                server.stop(0);
            }
            if (executor != null) {
                executor.shutdownNow();
            }
        }
    }

    @Test
    void capturesFetchAndXhrPreservesDuplicatesAndIgnoresPriorRequestsAndImages() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("fetch('/prior').then(r => r.text());");
        List<String> urls = new SeleniumApiAdapter(driver, 5)
                .clickAndGetRequestUrls(XPathSelector.of("//button[@id='capture']"));

        assertEquals(3, urls.size(), urls.toString());
        assertEquals(2, urls.stream().filter(url -> url.equals(baseUrl + "/api?state=REJECTED")).count());
        assertTrue(urls.contains(baseUrl + "/xhr?offset=0"));
        assertEquals(0L, js.executeScript(
                "return Object.keys(window).filter(key => key.startsWith('__suitRequests_')).length;"));
    }

    @Test
    void timesOutOnEmptyOrContinuousTrafficWithoutLeakingObservers() {
        SeleniumApiAdapter adapter = new SeleniumApiAdapter(driver, 2);
        assertThrows(TimeoutException.class,
                () -> adapter.clickAndGetRequestUrls(XPathSelector.of("//button[@id='empty']")));
        assertThrows(TimeoutException.class,
                () -> adapter.clickAndGetRequestUrls(XPathSelector.of("//button[@id='busy']")));
        assertEquals(0L, ((JavascriptExecutor) driver).executeScript(
                "clearInterval(window.polling); " +
                "return Object.keys(window).filter(key => key.startsWith('__suitRequests_')).length;"));
    }
}