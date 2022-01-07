package com.enterprise.restraunt.texasHamburger.kafka;

import com.enterprise.restraunt.texasHamburger.order.Order;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;

@Service
public class KafkaProducer {

    private final static String TOPIC = "thc-topic";
    private final static String BOOTSTRAP_SERVERS = "localhost:9092";
    private final static String CLIENT_ID_CONFIG = "order-producer-thc";

    KafkaProducer(){

    }

    private static Producer<String, String> createProducer(){
        Properties props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, CLIENT_ID_CONFIG);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new org.apache.kafka.clients.producer.KafkaProducer<>(props);
    }

//    static void runProducer(final int sendMessageCount) throws Exception{
//        final Producer<String, String> producer = createProducer();
//        try{
//            for(int index = 0; index < sendMessageCount; index++){
//                final ProducerRecord<String, String> record =
//                        new ProducerRecord<>(
//                                TOPIC,
//                                Integer.toString(index),
//                                "Send a String here "+index
//                        );
//                RecordMetadata metadata = producer.send(record).get();
//                System.out.println(record.key()+" "+ record.value()+" "+ metadata.partition()+" "+metadata.offset());
//            }
//        } finally {
//            producer.flush();
//            producer.close();
//        }
//    }

    static void runProducer(List<Order> orderList) throws Exception{
        final Producer<String, String> producer = createProducer();
        try{
            for(int index = 0; index < orderList.size(); index++){
                final ProducerRecord<String, String> record =
                        new ProducerRecord<>(
                                TOPIC,
                                Integer.toString(index),
                                orderList.get(index).toString()
                        );
                RecordMetadata metadata = producer.send(record).get();
                System.out.println(record.key()+" "+ record.value()+" "+ metadata.partition()+" "+metadata.offset());
            }
        } finally {
            producer.flush();
            producer.close();
        }
    }

//    public static void main(String[] args) throws Exception{
//        runProducer(10);
//    }
}
