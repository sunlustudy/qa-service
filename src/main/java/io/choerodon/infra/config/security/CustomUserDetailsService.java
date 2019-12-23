package io.choerodon.infra.config.security;

import io.choerodon.app.service.AuthenticationService;
import io.choerodon.domain.BaseUser;
import io.choerodon.infra.exception.CommonException;
import io.choerodon.infra.mapper.BaseUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Component("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService, Serializable {

    private final BaseUserMapper userMapper;


    @Autowired
    private AuthenticationService authenticationService;

    @SuppressWarnings("all")
    public CustomUserDetailsService(BaseUserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


        BaseUser user = authenticationService.obtainUserByUsername(username);
        if (user == null) {
            throw new CommonException("此用户不存在");
        }
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(p -> new SimpleGrantedAuthority(p.getName()))
                .collect(Collectors.toList());
        return new CustomUser(user.getId(), user.getUsername(), user.getPassword(), authorities);
    }

}
