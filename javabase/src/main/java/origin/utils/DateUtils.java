package origin.utils;

import org.apache.commons.lang.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * @author muqi.lmq
 * @date 01/11/2016.
 */
public class DateUtils {
    public static final int ONE_DAY_SECONDES = 1 * 24 * 60 * 60;
    public static final String FORMAT_yyyyMMdd = "yyyyMMdd";
    public static final String FORMAT_yyyyMMddHHmm = "yyyyMMddHHmm";
    public static final String FORMAT_yyyyMMddHHmmss = "yyyyMMddHHmmss";
    public static final String FORMAT_1 = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_2 = "yyyy-MM-dd";
    public static final int ONE_MINUTE_SECONDES = 1 * 60;
    private static String zoneId = "Asia/Shanghai";

    /**
     * 根据日期获取unix timestamp
     *
     * @param data   字符串时间
     * @param format 字符串格式 yyyy-MM-dd 这样
     * @return 秒
     */
    public static long getUnixTimestampFromDate(String data, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        LocalDate ld = LocalDate.parse(data, formatter);
        long l = ld.atStartOfDay(ZoneId.of("Asia/Shanghai")).toEpochSecond();
        return l;
    }

    /**
     * 根据datetime获取unix timestamp
     *
     * @param data   字符串时间
     * @param format 字符串格式 yyyy-MM-dd HH:mm:ss这样
     * @return 秒
     */
    public static long getUnixTimestampFromTime(String data, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        LocalDateTime ldt = LocalDateTime.parse(data, formatter);
        long l = ldt.atZone(ZoneId.of("Asia/Shanghai")).toEpochSecond();
        return l;
    }


    /**
     * 判定checkSecond秒加上add分钟后 是否 是base的将来时间
     *
     * @param base        基线时间
     * @param checkSecond 需要验证的unixtime时间
     * @param second      增加秒数
     * @return true or false
     */
    public static boolean addSecondCheckAfter(long base, long checkSecond, int second) {
        ZonedDateTime dateTime = Instant.ofEpochSecond(checkSecond).atZone(ZoneId.of("Asia/Shanghai"));
        LocalDateTime localDateTime = dateTime.toLocalDateTime();
        ZonedDateTime nowT = Instant.ofEpochSecond(base).atZone(ZoneId.of("Asia/Shanghai"));
        LocalDateTime nl = nowT.toLocalDateTime();
        boolean after = localDateTime.plusSeconds(second).isAfter(nl);
        return after;
    }

    /**
     * 根据秒获取当天
     *
     * @param sec
     * @param format
     * @return
     */
    public static String getDayString(long sec, String format) {
        ZonedDateTime dateTime = Instant.ofEpochSecond(sec).atZone(ZoneId.of("Asia/Shanghai"));
        LocalDateTime localDateTime = dateTime.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        String format1 = localDateTime.format(formatter);
        return format1;
    }


    /**
     * 验证 日期  yyyyMMddHHmmss
     *
     * @param batch
     * @return
     */
    public static Long validBatch(Long batch, int maxLength) {
        if (batch == null || batch.toString().length() > maxLength) {
            return Long.valueOf(batch.toString().substring(0, maxLength));
        } else {
            String format = String.format("%1$-" + maxLength + "s", batch.toString()).replace(' ', '0');
            return Long.valueOf(format);
        }
    }

    public static Integer retrieveHourFromBatchTime(String batchTime) {
        //batch_time的时间格式20170121094600
        String hour = StringUtils.substring(batchTime, 8, 10);
        return Integer.parseInt(hour);
    }
}
