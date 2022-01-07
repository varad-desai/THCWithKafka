package com.enterprise.restraunt.texasHamburger.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.stereotype.Service;
import org.apache.kafka.clients.consumer.Consumer;

import java.time.Duration;
import java.util.*;


@Service
public class KafkaConsumer {

    private final static String TOPIC = "thc-topic";
    private final static String BOOTSTRAP_SERVERS = "localhost:9092";
    private final static String CLIENT_ID_CONFIG = "order-consumer-thc";
    private final static String GROUP_ID_CONFIG = "order-consumer-thc";

    public static Consumer<String, String> createConsumer(){
        final Properties props = new Properties();
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, CLIENT_ID_CONFIG);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID_CONFIG);

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");

        final Consumer<String, String> consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(props);

        return consumer;
    }

    static Map<Integer, String> runConsumer(){
        final Consumer<String, String> consumer = createConsumer();

        consumer.subscribe(Collections.singletonList(TOPIC));
        Map<Integer, String> result= new HashMap<>();

        try{
            while(true){
                final ConsumerRecords<String, String> consumerRecords = consumer.poll(1000);
                if (consumerRecords.isEmpty()){continue;}


                consumerRecords.forEach(
                        record -> result.put(
                                (int)record.offset(), record.key()+ record.value().toString()+ record.partition()
                        ));
            }
        } catch (Exception exception){
            System.out.println(exception);
        } finally {
            consumer.close();
            System.out.println("DONE");
        }

        return result;
    }
}
