package com.davidosantos.kafka.kafka;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class StreamProcessor {

    @Value("${spring.kafka.properties.schema.registry.url}")
    String schema_registry_url;

    @Value("${spring.kafka.properties.basic.auth.credentials.source}")
    String credentials_source;

    @Value("${spring.kafka.properties.basic.auth.user.info}")
    String auth_user_info;

    @Autowired
    public void process(StreamsBuilder builder) {

        Map<String, String> serdeConfig = Map.of(
                "schema.registry.url", schema_registry_url,
                "schema.registry.basic.auth.user.info", auth_user_info,
                "basic.auth.credentials.source", credentials_source);
                
        Serde<User> userAvroSerdes = new SpecificAvroSerde<>();
        userAvroSerdes.configure(serdeConfig, false);

        KTable<String, User> users = builder.table("users", Consumed.with(Serdes.String(), userAvroSerdes));

        Serde<Survey> surveyAvroSerdes = new SpecificAvroSerde<>();
        surveyAvroSerdes.configure(serdeConfig, false);

        KStream<String, Survey> surveys = builder.stream("surveys", Consumed.with(Serdes.String(), surveyAvroSerdes));

        Serde<UserSurvey> userSurveyAvroSerdes = new SpecificAvroSerde<>();
        userSurveyAvroSerdes.configure(serdeConfig, false);

        surveys.join(users, (survey, user) -> UserSurvey.newBuilder()
                .setId(survey.getId())
                .setUserId(user.getId())
                .setName(user.getName())
                .setUserId(user.getId())
                .setEmail(user.getEmail())
                .setBestActorForMe(survey.getBestActorForMe())
                .build()

        ).peek((k, v) -> log.info("key= " + k + " value= " + v))
                .to("users_survey_results", Produced.with(Serdes.String(), userSurveyAvroSerdes));

    }
}
