package ru.yandex.practicum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

@Component
public class ProxyTraceFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(ProxyTraceFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange)
                .doOnSubscribe(sub -> {
                    Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
                    URI target = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);

                    log.info("GW proxy routeId={} -> target={}",
                            route != null ? route.getId() : "n/a",
                            target);
                });
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
