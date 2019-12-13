import org.apache.kafka.clients.producer.*;

import java.util.Properties;

public class KafkaProducter {
    public static void main(String[] args) {

        Properties props = new Properties();
        // Kafka服务端的主机名和端口号
        props.put("bootstrap.servers", "vm-bw-server17:6667");
        // 等待所有副本节点的应答
        props.put("acks", "all");
        // 消息发送最大尝试次数
        props.put("retries", 0);
        // 发送缓存区内存大小
        props.put("buffer.memory", 33554432);
        // key序列化
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        // value序列化
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> kafkaProducer = new KafkaProducer<>(props);

        for (int i = 0; i < 50; i++) {

            kafkaProducer.send(new ProducerRecord<String, String>("first", "hello" + i), new Callback() {

                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {

                    if (metadata != null) {

                        System.err.println(metadata.partition() + "---" + metadata.offset());
                    }
                }
            });
        }
        kafkaProducer.close();
    }
}

