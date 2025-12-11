package com.innov8.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;

@Component
public class RequestCorrelationInterceptor implements HandlerInterceptor {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String CORRELATION_ID_MDC_KEY = "correlationId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }

        // Add to MDC for structured logging
        MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
        MDC.put("userId", request.getHeader("X-User-ID") != null ? request.getHeader("X-User-ID") : "ANONYMOUS");
        MDC.put("action", request.getMethod() + " " + request.getRequestURI());

        response.setHeader(CORRELATION_ID_HEADER, correlationId);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MDC.clear();
    }

}
