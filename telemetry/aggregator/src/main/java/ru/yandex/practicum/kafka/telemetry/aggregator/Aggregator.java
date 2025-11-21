package ru.yandex.practicum.kafka.telemetry.aggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.kafka.telemetry.aggregator.logic.AggregationStarter;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Aggregator {

    public static void main(String[] args) {

        ConfigurableApplicationContext ctx =
                SpringApplication.run(Aggregator.class, args);

        AggregationStarter starter = ctx.getBean(AggregationStarter.class);
        starter.start();
    }
}
