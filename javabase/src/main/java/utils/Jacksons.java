package utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.List;

/**
 * @author muqi.lmq
 * @date 2018/7/3.
 */
public class Jacksons {
       public static <K, V> String transToKvJsonString(List<Kv<K, V>> baseCodeKvs) {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode node = objectMapper.createObjectNode();
            baseCodeKvs.stream().forEachOrdered(b -> {
                System.out.println(">>>:" + b);
                K k = b.getK();
                V v = b.getV();
                if (v instanceof Number) {
                    node.put(k.toString(), Long.valueOf(v.toString()));
                } else {
                    node.put(k.toString(), v.toString());
                }
                ;
            });
            return node.toString();
        }

        public static JsonNode getJsonNode(String content) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = null;
            try {
                node = objectMapper.readTree(content);
            } catch (IOException e) {
            }
            return node;
        }


        public static <T> T transObject(String content, Class<T> tClass) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            try {
                T t = objectMapper.readValue(content, tClass);
                return t;
            } catch (IOException e) {
            }
            return null;
        }

        public static <T> T transObject(String content, TypeReference typeReference) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            try {
                T t = objectMapper.readValue(content, typeReference);
                return t;
            } catch (IOException e) {
            }
            return null;
        }


        public static <T> String transObjectToString(T c) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String s = objectMapper.writeValueAsString(c);
                return s;
            } catch (IOException e) {
            }
            return null;
        }

}
