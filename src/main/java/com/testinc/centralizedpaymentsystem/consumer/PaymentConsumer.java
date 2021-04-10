package com.testinc.centralizedpaymentsystem.consumer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Collections;
import java.util.Properties;

public abstract class PaymentConsumer {


    protected  Consumer<String, String> consumer;

    public PaymentConsumer() {

    }

    public static Consumer<String, String> createConsumer(String topic,
                                                          String bootstrapServer,
                                                          String groupId,
                                                          long consumerReadTimeOutInterval,
                                                          int fetchSize){
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,
                groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,
                fetchSize);

        KafkaConsumer<String, String> kafkaConsumer =
                new KafkaConsumer<>(props);
        kafkaConsumer.subscribe(Collections.singletonList(topic));

        return kafkaConsumer;
    }


    public abstract void runConsumer();

}
