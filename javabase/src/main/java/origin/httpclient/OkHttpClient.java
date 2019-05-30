public class OkHttpClient {
    private static OkHttpClient client = new OkHttpClient();

    public static HttpResult get(String url, List<String> headers, Map<String, String> paramValues) throws IOException {
        url = genUrl(url, paramValues);
        Request.Builder builder = new Request.Builder().url(url);
        builder = setHeaders(builder, headers);
        Request request = builder.build();
        Call call = client.newCall(request);
        Response execute = call.execute();
        return getHttpResult(execute);
    }

    private static String genUrl(String url, Map<String, String> paramValues) throws UnsupportedEncodingException {
        String encodedContent = encodingParams(paramValues);
        url += (null == encodedContent) ? "" : (url.contains("?") ? ("&" + encodedContent) : ("?" + encodedContent));
        return url;
    }

    public static HttpResult httpPost(String url, List<String> headers, Map<String, String> paramValues, byte[] content) throws IOException {
        url = genUrl(url, paramValues);
        MediaType parse = null;
        if (headers != null) {
            for (Iterator<String> iter = headers.iterator(); parse == null && iter.hasNext(); ) {
                String contentType = iter.next();
                parse = MediaType.parse(iter.next());
            }
        }
        Request.Builder builder = new Request.Builder().url(url);
        builder = setHeaders(builder, headers);
        Request request = null;
        if (parse == null || content == null) {
            request = builder.build();
        } else {
            RequestBody body = RequestBody.create(parse, content);
            request = builder.post(body).build();
        }
        Call call = client.newCall(request);
        Response execute = call.execute();
        return getHttpResult(execute);
    }

    public static String toString(byte[] data) {
        if (data != null) {
            try {
                return StringUtils.toString(data, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
        return "";
    }

    private static HttpResult getHttpResult(Response response) throws IOException {
        int respCode = response.code();
        byte[] resp;
        Headers headers = response.headers();
        Map<String, List<String>> multimap = headers.toMultimap();
        if (HttpStatus.OK.value() == respCode) {
            resp = response.body().bytes();
            return new HttpResult(respCode, resp, multimap);
        } else {
            return new HttpResult(respCode, null, multimap);
        }
    }

    private static Request.Builder setHeaders(Request.Builder builder, List<String> headers) {
        if (null != headers) {
            for (Iterator<String> iter = headers.iterator(); iter.hasNext(); ) {
                builder.addHeader(iter.next(), iter.next());
            }
            return builder;
        }
        return builder;
    }

    private static String encodingParams(Map<String, String> paramValues)
            throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        if (null == paramValues) {
            return null;
        }

        for (Iterator<String> iter = paramValues.keySet().iterator(); iter.hasNext(); ) {
            String key = iter.next();
            sb.append(key).append("=");
            sb.append(URLEncoder.encode(paramValues.get(key), "UTF-8"));
            if (iter.hasNext()) {
                sb.append("&");
            }
        }
        return sb.toString();
    }

    public static class HttpResult {
        final public int code;
        final public Optional<byte[]> content;
        final public Map<String, List<String>> headers;

        public HttpResult(int code, byte[] content, Map<String, List<String>> headers) {
            this.code = code;
            if (content == null) {
                this.content = Optional.empty();
            } else {
                this.content = Optional.of(content);
            }
            this.headers = headers;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("HttpResult{");
            sb.append("code=").append(code);
            sb.append(", content='").append(content.map(bs -> {
                try {
                    return StringUtils.toString(bs, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    return "";
                }
            })); //toString(content, "UTF-8")).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}
