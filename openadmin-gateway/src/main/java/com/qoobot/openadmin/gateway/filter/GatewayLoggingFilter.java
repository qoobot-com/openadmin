package com.qoobot.openadmin.gateway.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GatewayLoggingFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.web.server.WebFilterChain chain) {
        System.out.println("[Gateway] request: " + exchange.getRequest().getURI());
        return chain.filter(exchange);
    }
}

