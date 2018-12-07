package com.albedo.java.common.security.jwt;

import com.albedo.java.common.config.ApplicationProperties;
import com.albedo.java.common.security.SecurityConstants;
import com.albedo.java.util.PublicUtil;
import com.albedo.java.util.RedisUtil;
import com.albedo.java.web.rest.util.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Filters incoming requests and installs a Spring Security principal if a header corresponding to a valid user is
 * found.
 */
@Slf4j
public class JWTFilter extends GenericFilterBean {

    private TokenProvider tokenProvider;

    private ApplicationProperties applicationProperties;

    public JWTFilter(TokenProvider tokenProvider, ApplicationProperties applicationProperties) {
        this.tokenProvider = tokenProvider;
        this.applicationProperties = applicationProperties;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String key = resolveToken(httpServletRequest);
        if (StringUtils.hasText(key)) {
            String jwt = RedisUtil.getCacheString(key);
            if(PublicUtil.isNotEmpty(jwt)){
                RedisUtil.setCacheString(key, jwt, tokenProvider.getTokenValidityInSeconds(), TimeUnit.SECONDS);
            }
            if(this.tokenProvider.validateToken(jwt)){
                Authentication authentication = this.tokenProvider.getAuthentication(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }else{
                CookieUtil.deleteCookie(httpServletRequest, (HttpServletResponse) servletResponse, SecurityConstants.AUTHORIZATION_HEADER);
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private String resolveToken(HttpServletRequest request){
        String bearerToken = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7, bearerToken.length());
//        }
        return bearerToken;
    }
}
