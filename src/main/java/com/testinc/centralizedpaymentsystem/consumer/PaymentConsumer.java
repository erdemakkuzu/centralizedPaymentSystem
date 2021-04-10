package com.testinc.centralizedpaymentsystem.consumer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Collections;
import java.util.Properties;

public class PaymentConsumer {

    private final long consumerReadTimeOutInterval;
    private final Consumer<String, String> consumer;

    public PaymentConsumer(String topic,
                           String bootstrapServer,
                           String groupId,
                           long consumerReadTimeOutInterval,
                           int fetchSize) {

        this.consumerReadTimeOutInterval = consumerReadTimeOutInterval;

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

        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));

    }

    public void runConsumer() {
        final ConsumerRecords<String, String> consumerRecords =
                consumer.poll(consumerReadTimeOutInterval);

        consumerRecords.forEach(record -> {
            System.out.printf("Consumer Record:(%s, %s, %d, %d)\n",
                    record.key(), record.value(),
                    record.partition(), record.offset());
        });

        consumer.commitAsync();

        consumer.close();
        System.out.println("DONE");
    }
}
