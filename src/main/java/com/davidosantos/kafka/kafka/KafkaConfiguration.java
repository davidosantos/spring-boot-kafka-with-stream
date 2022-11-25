package com.davidosantos.kafka.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableKafkaStreams
 public class KafkaConfiguration {

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kafkaStreamsConfig(
        @Value("${spring.kafka.properties.bootstrap.servers}") final String bootstrapServers,
        @Value("${spring.kafka.properties.sasl.jaas.config}") final String jaas_config,
        @Value("${spring.kafka.properties.sasl.mechanism}") final String sasl_mechanism,
        @Value("${spring.kafka.properties.security.protocol}") final String security_protocol) {

        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-streams-demo");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // Use to specify location of stateful store.
//        props.put(STATE_DIR_CONFIG,"./rocksdb");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        // This default value serdes for Long type was required for the KTable aggregation step
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Long().getClass().getName());
        props.put("sasl.jaas.config", jaas_config);
        props.put("sasl.mechanism", sasl_mechanism);
        props.put(StreamsConfig.SECURITY_PROTOCOL_CONFIG, security_protocol);
        return new KafkaStreamsConfiguration(props);
    }

}