package com.albedo.java.common.security.service;

import com.albedo.java.common.AuthoritiesConstants;
import com.albedo.java.common.constants.UserStatusEnum;
import com.albedo.java.common.persistence.domain.BaseEntity;
import com.albedo.java.common.security.*;
import com.albedo.java.modules.sys.domain.User;
import com.albedo.java.modules.sys.repository.UserRepository;
import com.albedo.java.modules.sys.service.UserService;
import com.albedo.java.util.PublicUtil;
import com.albedo.java.util.StringUtil;
import com.albedo.java.util.base.Assert;
import com.albedo.java.util.exception.RuntimeMsgException;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Authenticate a user from the database.
 */
@Component
@ConditionalOnClass(UserDetailsService.class)
public class UserCustomDetailsService implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(UserCustomDetailsService.class);

    private final UserService userService;

    private final CacheManager cacheManager;

    public UserCustomDetailsService(UserService userService, CacheManager cacheManager) {
        this.userService = userService;
        this.cacheManager = cacheManager;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String loginId) {
        log.debug("Authenticating {}", loginId);
        return userService.findOneByLoginId(loginId).map(user -> {
            if (!BaseEntity.FLAG_NORMAL.equals(user.getStatus())) {
                throw new RuntimeMsgException("用户 " + loginId + " 登录信息已被"+ UserStatusEnum.get(user.getStatus()).getText());
            }

            List<GrantedAuthority> grantedAuthorities = Lists.newArrayList(
                new SimpleGrantedAuthority(AuthoritiesConstants.USER));
            if(SecurityAuthUtil.isAdmin(user.getId())){
                grantedAuthorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN));
            }
            SecurityUtil.getModuleList(user.getId()).forEach(authority -> {
                if (PublicUtil.isNotEmpty(authority.getPermission())) {
                    Lists.newArrayList(authority.getPermission().split(StringUtil.SPLIT_DEFAULT)).forEach(p -> grantedAuthorities.add(new SimpleGrantedAuthority(p)));
                }
            });
            return new UserPrincipal(user.getId(), loginId,
                user.getPassword(),
                grantedAuthorities);
        }).orElseThrow(() -> new RuntimeMsgException("用户 " + loginId + " 不存在"));
    }
}
