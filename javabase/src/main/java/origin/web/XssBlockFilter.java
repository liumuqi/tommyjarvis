package origin.web;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * @Author:qishan
 * @Date: 2019/11/4
 * @Desc:
 **/
public class XssBlockFilter implements Filter {
    private static boolean no_init = true;
    private FilterConfig config = null;
    private String apostrophe = "&#39;";

    public void init(FilterConfig paramFilterConfig) {
        this.config = paramFilterConfig;
        no_init = false;
        String str = paramFilterConfig.getInitParameter("apostrophe");
        if (str != null) {
            this.apostrophe = str.trim();
        }
    }

    public void destroy() {
        this.config = null;
    }

    public FilterConfig getFilterConfig() {
        return this.config;
    }

    public void setFilterConfig(FilterConfig paramFilterConfig) {
        if (no_init) {
            no_init = false;
            this.config = paramFilterConfig;
            String str = paramFilterConfig.getInitParameter("apostrophe");
            if (str != null) {
                this.apostrophe = str.trim();
            }
        }
    }

    public void doFilter(ServletRequest paramServletRequest, ServletResponse paramServletResponse, FilterChain paramFilterChain) throws IOException, ServletException {
        paramFilterChain.doFilter(new RequestWrapper((HttpServletRequest) paramServletRequest, this.apostrophe), paramServletResponse);
    }

    class RequestWrapper extends HttpServletRequestWrapper {
        private String apostrophe = "&#39;";

        public RequestWrapper(HttpServletRequest paramHttpServletRequest) {
            super(paramHttpServletRequest);
        }

        public RequestWrapper(HttpServletRequest paramHttpServletRequest, String paramString) {
            super(paramHttpServletRequest);
            this.apostrophe = paramString;
        }

        public String[] getParameterValues(String paramString) {
            String[] arrayOfString1 = super.getParameterValues(paramString);
            if (arrayOfString1 == null) {
                return null;
            }
            int i = arrayOfString1.length;
            String[] arrayOfString2 = new String[i];
            for (int j = 0; j < i; j++) {
                arrayOfString2[j] = cleanXSS(arrayOfString1[j]);
            }
            return arrayOfString2;
        }

        public String getParameter(String paramString) {
            String str = super.getParameter(paramString);
            if (str == null) {
                return null;
            }
            return cleanXSS(str);
        }

        public String getHeader(String paramString) {
            String str = super.getHeader(paramString);
            if (str == null) {
                return null;
            }
            return cleanXSS(str);
        }

        private String cleanXSS(String paramString) {
            if (paramString == null) {
                return "";
            }
            String str = paramString;
            str = str.replaceAll("\000", "");
            Pattern localPattern = Pattern.compile("<script>(.*?)</script>", 2);
            str = localPattern.matcher(str).replaceAll("");
            localPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\'(.*?)\\'", 42);
            str = localPattern.matcher(str).replaceAll("");
            localPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", 42);
            str = localPattern.matcher(str).replaceAll("");
            localPattern = Pattern.compile("</script>", 2);
            str = localPattern.matcher(str).replaceAll("");
            localPattern = Pattern.compile("<script(.*?)>", 42);
            str = localPattern.matcher(str).replaceAll("");
            localPattern = Pattern.compile("eval\\((.*?)\\)", 42);
            str = localPattern.matcher(str).replaceAll("");
            localPattern = Pattern.compile("expression\\((.*?)\\)", 42);
            str = localPattern.matcher(str).replaceAll("");
            localPattern = Pattern.compile("javascript:", 2);
            str = localPattern.matcher(str).replaceAll("");
            localPattern = Pattern.compile("vbscript:", 2);
            str = localPattern.matcher(str).replaceAll("");
            localPattern = Pattern.compile("onload(.*?)=", 42);
            str = localPattern.matcher(str).replaceAll("");
            str = str.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
            str = str.replaceAll("'", this.apostrophe);
            str = str.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
            return str;
        }
    }
}
