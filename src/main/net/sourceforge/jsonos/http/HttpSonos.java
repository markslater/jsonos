package net.sourceforge.jsonos.http;

import argo.format.JsonFormatter;
import argo.format.PrettyJsonFormatter;
import argo.jdom.JsonNode;
import com.google.common.base.Function;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import net.sourceforge.jsonos.Alarm;
import net.sourceforge.jsonos.RememberingAlarmsListener;
import net.sourceforge.jsonos.SonosClient;
import net.sourceforge.jsonos.SysErrUnexpectedEventsListener;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;

import static argo.jdom.JsonNodeFactories.array;
import static argo.jdom.JsonNodeFactories.string;
import static com.google.common.collect.Iterables.transform;

public final class HttpSonos {

    private static final DateTimeFormatter RFC1123_DATE_TIME_FORMATTER = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'").withZoneUTC();
    public static final JsonFormatter JSON_FORMATTER = new PrettyJsonFormatter();

    private final HttpServer httpServer;

    public HttpSonos() throws IOException {
        final RememberingAlarmsListener alarmsListener = new RememberingAlarmsListener();
        final SonosClient sonosClient = new SonosClient(alarmsListener, new SysErrUnexpectedEventsListener());
        httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        httpServer.createContext("/alarm", new HttpHandler() {
            @Override
            public void handle(final HttpExchange httpExchange) throws IOException {
                httpExchange.getResponseHeaders().add("Content-Type", "application/json");
                httpExchange.getResponseHeaders().add("Date", RFC1123_DATE_TIME_FORMATTER.print(new DateTime()));
                httpExchange.sendResponseHeaders(200, 0);
                final OutputStream responseBody = httpExchange.getResponseBody();
                final OutputStreamWriter writer = new OutputStreamWriter(responseBody);
                JSON_FORMATTER.format(array(transform(alarmsListener.alarms(), new Function<Alarm, JsonNode>() {
                    @Override
                    public JsonNode apply(final Alarm input) {
                        return string(input.toString());
                    }
                })), writer);
                writer.close();
                responseBody.close();
            }
        });
        httpServer.createContext("/snooze", new HttpHandler() {
            @Override
            public void handle(final HttpExchange httpExchange) throws IOException {
                sonosClient.snooze();
                httpExchange.getResponseHeaders().add("Date", RFC1123_DATE_TIME_FORMATTER.print(new DateTime()));
                httpExchange.sendResponseHeaders(204, -1);
            }
        });
        httpServer.createContext("/sleep", new HttpHandler() {
            @Override
            public void handle(final HttpExchange httpExchange) throws IOException {
                sonosClient.sleep();
                httpExchange.getResponseHeaders().add("Date", RFC1123_DATE_TIME_FORMATTER.print(new DateTime()));
                httpExchange.sendResponseHeaders(204, -1);
            }
        });
        httpServer.createContext("/stop", new HttpHandler() {
            @Override
            public void handle(final HttpExchange httpExchange) throws IOException {
                sonosClient.stop();
                httpExchange.getResponseHeaders().add("Date", RFC1123_DATE_TIME_FORMATTER.print(new DateTime()));
                httpExchange.sendResponseHeaders(204, -1);
            }
        });
        httpServer.createContext("/runningAlarms", new HttpHandler() {
            @Override
            public void handle(final HttpExchange httpExchange) throws IOException {
                sonosClient.runningAlarms();
                httpExchange.getResponseHeaders().add("Date", RFC1123_DATE_TIME_FORMATTER.print(new DateTime()));
                httpExchange.sendResponseHeaders(204, -1);
            }
        });
    }

    public static void main(String[] args) throws IOException {
        new HttpSonos().start();
    }

    private void start() {
        httpServer.start();
    }
}
