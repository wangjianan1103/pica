package com.core.pica.config;

import com.core.pica.util.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SimpleCORSFilter implements Filter {
    private final static Logger logger = LoggerFactory.getLogger(SimpleCORSFilter.class);

    @Value("${vendor.cors_access.allow_origins}")
    private String[] allowOrigins;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse rsp, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) rsp;

        final String originHeader = request.getHeader("Origin");

        boolean bAllow = false;

        if (originHeader == null) {
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", "Origin,X-Requested-With,Content-Type,Accept");
            response.setHeader("Access-Control-Allow-Credentials", "true");
        } else {
            for (String tmp : allowOrigins) {
                if (Utility.isBlank(tmp)) {
                    continue;
                }
                if (equalsAddress(originHeader, tmp)) {
                    bAllow = true;
                    break;
                }
            }
        }

        if (bAllow) {
            response.setHeader("Access-Control-Allow-Origin", originHeader);
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", "Origin,X-Requested-With,Content-Type,Accept");
            response.setHeader("Access-Control-Allow-Credentials", "true");
        }
        chain.doFilter(request, response);
    }

    private static boolean equalsAddress(String address, String regex) {
        regex = regex.replace(".", "\\.");
        regex = regex.replace("*", "(.*)");
        Pattern pattern = Pattern.compile("^" + regex + "$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(address);
        return matcher.find();
    }

    @Override
    public void destroy() {
    }
}


