package org.example.authservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String protocol = request.getProtocol();

        // Получаем IP клиента
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null) {
            clientIp = request.getRemoteAddr();
        }

        // Логируем подозрительные запросы
        if (!method.matches("GET|POST|PUT|DELETE|PATCH|OPTIONS|HEAD")) {
            log.warn("⚠️ Некорректный HTTP метод: [{}] URI=[{}] protocol=[{}] clientIp=[{}]",
                    method, uri, protocol, clientIp);
        }

        filterChain.doFilter(request, response);
    }
}
