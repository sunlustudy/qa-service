package io.choerodon.app.service.impl;

import com.google.common.collect.ImmutableMap;
import io.choerodon.api.dto.BaseUserDTO;
import io.choerodon.app.service.AuthenticationService;
import io.choerodon.domain.BaseUser;
import io.choerodon.domain.LoginVM;
import io.choerodon.domain.UserRoleRelation;
import io.choerodon.infra.config.security.AuthoritiesConstants;
import io.choerodon.infra.exception.CommonException;
import io.choerodon.infra.mapper.BaseUserMapper;
import io.choerodon.infra.mapper.UserRoleRelationMapper;
import io.choerodon.infra.utils.JwtTokenUtil;
import io.choerodon.infra.utils.RandomUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.choerodon.infra.utils.BeanUtil.isNotEmpty;
import static io.choerodon.infra.utils.RandomUtils.cutString;
import static io.choerodon.infra.utils.web.WebConstants.*;


/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-09-16 17:27
 */
@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private SmsServiceImpl smsService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRoleRelationMapper userRoleRelationMapper;

    @Autowired
    private BaseUserMapper userMapper;

    @Qualifier("userRedisTemplate")
    @Autowired
    private RedisTemplate<String, BaseUser> userRedisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    private static final String REGISTER_KEY_SUFFIX = ":register";
    private static final String CHANGE_PWD_SUFFIX = ":changePWD";


    @Value("${test.code}")
    private String testCode;


    @Override
    public boolean sendCode(String phone) {
        List<BaseUser> users = userMapper.selectByMap(ImmutableMap.of("phone", phone));
        if (isNotEmpty(users)) {
            throw new CommonException("此手机号已被注册");
        }
        String code = RandomUtils.generateCodeNum();
        if (smsService.sendRegisterCode(code, phone)) {
            log.info("发送验证码 code:{}", code);
            redisTemplate.opsForValue().set(phone + REGISTER_KEY_SUFFIX, code, 5, TimeUnit.MINUTES);
            return true;
        }
        return false;
    }


    @Override
    public boolean sendChangePWDCode(String phone) {
        String code = RandomUtils.generateCodeNum();
        if (smsService.sendChangePWDCode(code, phone)) {
            redisTemplate.opsForValue().set(phone + CHANGE_PWD_SUFFIX, code, 5, TimeUnit.MINUTES);
            return true;
        }
        return false;
    }


    @Override
    public Boolean sendPwdChange(String username) {
        BaseUser user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new CommonException("无此用户");
        }
        return sendChangePWDCode(user.getPhone());
    }


    @Override
    @Transactional
    public Boolean register(BaseUserDTO userDTO, String code) {


//        测试用
//        if (!testCode.equals("") && testCode.equals(code)) {
//            return createUser(userDTO);
//        }

        String savedCode = redisTemplate.opsForValue().get(userDTO.getPhone() + REGISTER_KEY_SUFFIX);

        if (savedCode == null) {
            throw new CommonException(INVALID_CODE);
        }
        if (savedCode.equals(code)) {
//            删除已使用的验证码
            redisTemplate.delete(userDTO.getPhone() + REGISTER_KEY_SUFFIX);
            return createUser(userDTO);
        } else {
            throw new CommonException(INVALID_CODE);
        }

    }

    @Override
    public Map<String, Object> auth(LoginVM loginVM) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginVM.getUsername(), loginVM.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        String jwt = JwtTokenUtil.generateToken((String) authentication.getPrincipal(), 60 * 60);
        String refreshToken = JwtTokenUtil.generateToken((String) authentication.getPrincipal(), 60 * 60 * 24 * 2);
        redisTemplate.opsForValue().set(loginVM.getUsername() + ":" + ACCESS_TOKEN + ":" + cutString(jwt), jwt, 60, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(loginVM.getUsername() + ":" + REFRESH_TOKEN + ":" + cutString(refreshToken), refreshToken, 2, TimeUnit.DAYS);
        return ImmutableMap.of(ACCESS_TOKEN, jwt,
                ACCESS_EXPIRE, DEFAULT_ACCESS_EXPIRE,
                REFRESH_TOKEN, refreshToken,
                REFRESH_EXPIRE, DEFAULT_REFRESH_EXPIRE);
    }


    @Override
    public Map<String, Object> refreshToken(String username, String token) {
        String stored = redisTemplate.opsForValue().get(username + ":" + REFRESH_TOKEN);
        if (stored == null || !stored.equals(token) || !JwtTokenUtil.parseToken(token).equals(username)) {
            throw new CommonException("token is invalid");
        } else {
            String jwt = JwtTokenUtil.generateToken(username, 60 * 60);
            String refreshToken = JwtTokenUtil.generateToken(username, 60 * 24 * 2);
            redisTemplate.opsForValue().set(username + ":" + ACCESS_TOKEN + ":" + cutString(jwt), jwt, 60, TimeUnit.MINUTES);
            redisTemplate.opsForValue().set(username + ":" + REFRESH_TOKEN + ":" + cutString(refreshToken), refreshToken, 2, TimeUnit.DAYS);
//            删除已使用的刷新 token
            redisTemplate.delete(username + ":" + REFRESH_TOKEN + ":" + cutString(token));
            return ImmutableMap.of(ACCESS_TOKEN, jwt,
                    ACCESS_EXPIRE, DEFAULT_ACCESS_EXPIRE,
                    REFRESH_TOKEN, refreshToken,
                    REFRESH_EXPIRE, DEFAULT_REFRESH_EXPIRE);
        }

    }

    @Override
    public Boolean isUsernameExist(String username) {
        return !ObjectUtils.isEmpty(userMapper.selectByMap(ImmutableMap.of("username", username)));
    }


    @Override
    @Transactional
    public Boolean createUser(BaseUserDTO userDTO) {
        BaseUser user = new BaseUser();
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setName(userDTO.getName());
        user.setUsername(userDTO.getUsername());
        user.setPhone(userDTO.getPhone());
        if (userMapper.insert(user) != 1) {
            throw new CommonException("注册用户失败");
        }
        if (userRoleRelationMapper.insert(new UserRoleRelation(user.getId(), 1)) != 1) {
            throw new CommonException("创建基础用户权限失败");
        }
        return true;
    }


    @Override
    @Transactional
    public boolean deleteUser(Integer id) {
        if (userMapper.deleteById(id) != 1) {
            throw new CommonException("删除失败，可能不存在此 id 用户");
        }
        userRoleRelationMapper.deleteByMap(ImmutableMap.of("user_id", id));
        return true;
    }

    @Transactional
    @Override
    public boolean deleteUser(List<Integer> ids) {
        if (isNotEmpty(ids)) {
            ids.forEach(this::deleteUser);
        }
        return true;
    }


    /**
     * 修改密码
     *
     * @param username
     * @param newPassword
     * @param code
     * @return
     */
    @Override
    public boolean changePassword(String username, String newPassword, String code) {
        BaseUser user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new CommonException("username not found");
        } else {
//            测试用
//            if (!testCode.equals("") && testCode.equals(code)) {
//                user.setPassword(passwordEncoder.encode(newPassword));
//                return userMapper.updateById(user) > 0;
//            }

            String stored = redisTemplate.opsForValue().get(user.getPhone() + CHANGE_PWD_SUFFIX);
            if (stored == null) {
                throw new CommonException("code is invalid");
            } else if (!stored.equals(code)) {
                throw new CommonException("code is error");
            }
//        删除验证码
            redisTemplate.delete(user.getPhone() + CHANGE_PWD_SUFFIX);
            if (passwordEncoder.matches(newPassword, user.getPassword())) {
                throw new CommonException("新旧密码一致");
            }
            // 删除 redis 中缓存
            userRedisTemplate.delete(USER_KEY + username);
            user.setPassword(passwordEncoder.encode(newPassword));
            return userMapper.updateById(user) > 0;
        }

    }


    @Override
    public boolean updateUser(BaseUser user) {
        BaseUser newUser = new BaseUser();
        newUser.setId(user.getId());
        newUser.setPhone(user.getPhone());
        newUser.setUsername(user.getUsername());
        newUser.setName(user.getName());
        if (userMapper.updateById(newUser) != 1) {
            throw new CommonException("更新失败");
        }
        return true;
    }


    /**
     * 获取用户信息，管理员可无限制，用户身份则只能获取自身信息
     */
    @Override
    public BaseUser obtainUser(String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        boolean hasAuthen = authentication.getAuthorities().stream()
                .anyMatch(p -> p.getAuthority().equals(AuthoritiesConstants.ADMIN));

        if (hasAuthen || username.equals(userDetails.getUsername())) {
            BaseUser baseUser = userMapper.selectByUsername(username);
            if (baseUser != null) {
                baseUser.setPassword(null);
            }
            return baseUser;
        } else {
            throw new CommonException("获取失败！");
        }
    }


    private static final String USER_KEY = "user:username:"; // redis 中 user 缓存key

    @Override
    public BaseUser obtainUserByUsername(String username) {
        BaseUser user = userRedisTemplate.opsForValue().get(USER_KEY + username);
        if (user == null) {
            user = userMapper.selectByUsername(username);
            if (user != null) {
                userRedisTemplate.opsForValue().set(USER_KEY + username, user, 60, TimeUnit.MINUTES);
            }
        }
        return user;
    }

}
