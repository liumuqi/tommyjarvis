package origin.web;

import org.apache.commons.io.IOUtils;
import org.apache.coyote.ActionCode;
import org.apache.coyote.ContainerThreadMarker;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

/**
 * @Author:qishan
 * @Date: 2019-03-16
 * @Desc:
 **/
public class WrapperRequestFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }
    @Override
    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        ResettableStreamHttpServletRequest wrappedRequest = new ResettableStreamHttpServletRequest((HttpServletRequest) request);
        // wrappedRequest.getInputStream().read();
        String body = IOUtils.toString(wrappedRequest.getReader());
//        auditor.audit(wrappedRequest.getRequestURI(), wrappedRequest.getUserPrincipal(), body);
        wrappedRequest.resetInputStream();
        chain.doFilter(wrappedRequest, response);

    }


    private static class ResettableStreamHttpServletRequest extends HttpServletRequestWrapper {
        private byte[] rawData;
        private HttpServletRequest request;
        private ResettableServletInputStream servletStream;

        public ResettableStreamHttpServletRequest(HttpServletRequest request) {
            super(request);
            this.request = request;
            this.servletStream = new ResettableServletInputStream();
        }

        public void resetInputStream() {
            servletStream.stream = new ByteArrayInputStream(rawData);
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            if (rawData == null) {
                rawData = IOUtils.toByteArray(this.request.getReader());
                servletStream.stream = new ByteArrayInputStream(rawData);
            }
            return servletStream;
        }

        @Override
        public BufferedReader getReader() throws IOException {
            if (rawData == null) {
                rawData = IOUtils.toByteArray(this.request.getReader());
                servletStream.stream = new ByteArrayInputStream(rawData);
            }
            return new BufferedReader(new InputStreamReader(servletStream));
        }

        private class ResettableServletInputStream extends ServletInputStream {
            private InputStream stream;
            @Override
            public int read() throws IOException {
                return stream.read();
            }

            @Override
            public boolean isFinished() {
                try {
                    return stream.available() > 0 ? false : true;
                } catch (IOException e) {
                }
                return false;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }
        }
    }
}
