package origin.concurrency;

import org.apache.commons.codec.binary.Base64;

import java.time.*;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.Adler32;
import java.util.zip.CRC32;

/**
 * @author muqi.lmq
 * @date 2018/6/14.
 */
public class UUIDTest {
    private static ConcurrentHashMap map = new ConcurrentHashMap(1 << 100);

    public static void main(String[] args) throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(8);
        CountDownLatch cdl = new CountDownLatch(8);
        for (int i = 0; i < 8; i++) {
            service.submit(() -> {
                for (int j = 0; j < 100000000; j++) {
                    String d1 = UUID.randomUUID().toString();
                    String d2 = UUID.randomUUID().toString();
                    long pid = generatePid(d1, d2, false);
                    map.computeIfPresent(pid, (a, b) -> {
                        System.err.println(a + ":::duplicate:::" + b + ":::" + pid);
                        return null;
                    });
                    map.put(pid, 1);
                }
                cdl.countDown();
            });
        }
        LocalDateTime start = Instant.now().atZone(ZoneId.of("Asia/Shanghai")).toLocalDateTime();
        System.out.println(start.toString()+":waiting.....................");
        LocalDateTime start2 = Instant.now().atZone(ZoneId.of("Asia/Shanghai")).toLocalDateTime();
        long l1 = Duration.between(start, start2).toNanos();
        System.out.println(l1);
        cdl.await();
        Instant now = Instant.now();
        long l = Duration.between(start, now).toMinutes();
        System.out.println(l+" mi =====================");
        System.out.println(map.size());
        System.out.println("=====================");
    }


    public static long generatePid(String data1, String data2, boolean base64) {
        Adler32 adler32 = new Adler32();
        CRC32 crc32 = new CRC32();
        adler32.reset();
        crc32.reset();
        data1 = data1.toUpperCase();
        if (!base64) {
            data2 = new String(Base64.encodeBase64(data2.getBytes()));
        }
        data2 = (null == data2 ? "" : data2);
        String key = data2 + "." + data1;

        adler32.update(key.getBytes());
        crc32.update(key.getBytes());

        long adlervalue = adler32.getValue();
        long crc32value = crc32.getValue();
        crc32value = (crc32.getValue() << 32) & 0x7FFFFFFFFFFFFFFFl;
        return crc32value | adlervalue;
    }
}
