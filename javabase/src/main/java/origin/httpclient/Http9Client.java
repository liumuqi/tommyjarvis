package origin.httpclient;

//import jdk.incubator.http.HttpClient;
//import jdk.incubator.http.HttpRequest;
//import jdk.incubator.http.HttpResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.zip.GZIPInputStream;

//HotSpotIntrinsicCandidate
public class Http9Client {
    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        doGet();
        doPost();
    }


    public static void doPost() throws URISyntaxException, IOException, InterruptedException {
//        URI uri = new URI("https://www.baidu.com/");
//        HttpRequest.Builder post = HttpRequest.newBuilder(uri).POST(HttpRequest.BodyPublisher.fromString("###要请求的参数"));
//        //post.setHeader可以设置UA、Cookie等参数
//        post.setHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_3 like Mac OS X) AppleWebKit/602.1.50 (KHTML, like Gecko) CriOS/56.0.2924.75 Mobile/14E5239e Safari/602.1");
//
//        HttpClient httpClient = HttpClient.newHttpClient();
//        HttpResponse httpResponse = httpClient.send(post.build(), HttpResponse.BodyHandler.asString());
//        int code = httpResponse.statusCode();
//        System.out.println(code);
//        System.out.println(uncompress(httpResponse.body().toString().getBytes()));
    }

    public static String uncompress(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        try {
            GZIPInputStream ungzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = ungzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
        } catch (IOException e) {
            System.out.println(e);
        }

        return out.toString();
    }


    private static void doGet() throws IOException, InterruptedException, URISyntaxException {
//        HttpClient httpClient = HttpClient.newHttpClient();
//        HttpResponse httpResponse = httpClient.send(
//                HttpRequest
//                        .newBuilder(new URI("http://transport.opendata.ch/v1/connections?from=Bern&to=z%C3%BCrich&datetime=2017-09-21T17%3A00")
//                        ).GET().build(),
//                HttpResponse.BodyHandler.asString()
//        );
//        int code = httpResponse.statusCode();
//        System.out.println(code);
//        System.out.println(httpResponse.body().toString());
    }
}
