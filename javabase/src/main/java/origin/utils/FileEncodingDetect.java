package origin.utils;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author muqi.lmq
 * @date 2018/7/16.
 */
public class FileEncodingDetect {
    public static String checkCharset(InputStream inputStream) {
        UniversalDetector detector = new UniversalDetector(null);
        String encoding = null;
        try {
            int nread;
            byte[] buf = new byte[4096];
            while ((nread = inputStream.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
            detector.dataEnd();
            encoding = detector.getDetectedCharset();
            detector.reset();
        } catch (IOException e) {
        }
        return encoding;
    }
}
