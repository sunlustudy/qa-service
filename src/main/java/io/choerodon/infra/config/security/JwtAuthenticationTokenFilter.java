package io.choerodon.infra.config.security;

import io.choerodon.infra.utils.JwtTokenUtil;
import io.choerodon.infra.utils.web.WebConstants;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static io.choerodon.infra.utils.RandomUtils.cutString;
import static io.choerodon.infra.utils.web.WebConstants.ACCESS_TOKEN;


@Component
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;

    private final RedisTemplate<String, String> redisTemplate;

    public JwtAuthenticationTokenFilter(UserDetailsService userDetailsService, RedisTemplate<String, String> redisTemplate) {
        this.userDetailsService = userDetailsService;
        this.redisTemplate = redisTemplate;
    }

    private static final String BEARER = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        //请求头为 Authorization
        //请求体为 Bearer token
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith(BEARER)) {
            final String authToken = authHeader.substring(BEARER.length());
            String username = null;
            try {
                username = JwtTokenUtil.parseToken(authToken);
                String stored = redisTemplate.opsForValue().get(username + ":" + ACCESS_TOKEN + ":" + cutString(authToken));
                if (stored == null || !stored.equals(authToken)) {
                    username = null;
                    log.warn(WebConstants.INVALID_CODE);
                }
            } catch (ExpiredJwtException e) {
                log.warn("token 过期");
            }


            if (username != null) {
//                获取用户身份，用以之后判断是否有权限
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (userDetails != null) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        chain.doFilter(request, response);
    }

}
