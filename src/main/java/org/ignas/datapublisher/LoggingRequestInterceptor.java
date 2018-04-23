
package org.ignas.datapublisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StopWatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

    final static Logger log = LoggerFactory.getLogger(LoggingRequestInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        traceRequest(request, body);
        StopWatch watch = new StopWatch();
        watch.start();
        ClientHttpResponse response = execution.execute(request, body);
        watch.stop();

        traceResponse(response, watch);
        return response;
    }

    private void traceRequest(HttpRequest request, byte[] body) throws IOException {
        log.debug("===========================request begin================================================");
        log.debug("URI         : {}", request.getURI());
        log.debug("Request body: {}", new String(body, "UTF-8"));
    }

    private void traceResponse(ClientHttpResponse response, StopWatch watch) throws IOException {
        StringBuilder inputStringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), "UTF-8"));
        String line = bufferedReader.readLine();
        while (line != null) {
            inputStringBuilder.append(line);
            inputStringBuilder.append('\n');
            line = bufferedReader.readLine();
        }
        log.debug("Status code  : {}, Time {}, Response body: {}", response.getStatusCode(), watch.getTotalTimeMillis(), inputStringBuilder.toString());
        log.debug("============================response ends==========================================");

    }

}