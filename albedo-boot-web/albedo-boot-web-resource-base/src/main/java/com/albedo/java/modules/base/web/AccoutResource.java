package com.albedo.java.modules.base.web;

import com.albedo.java.common.AuthoritiesConstants;
import com.albedo.java.common.persistence.pk.IdGen;
import com.albedo.java.common.security.SecurityAuthUtil;
import com.albedo.java.common.security.SecurityConstants;
import com.albedo.java.common.security.SecurityUtil;
import com.albedo.java.common.security.jwt.TokenProvider;
import com.albedo.java.modules.sys.domain.User;
import com.albedo.java.modules.sys.service.UserService;
import com.albedo.java.util.DateUtil;
import com.albedo.java.util.PublicUtil;
import com.albedo.java.util.RedisUtil;
import com.albedo.java.util.base.Assert;
import com.albedo.java.util.config.SystemConfig;
import com.albedo.java.util.domain.CustomMessage;
import com.albedo.java.util.domain.Globals;
import com.albedo.java.vo.account.LoginVo;
import com.albedo.java.vo.account.PasswordChangeVo;
import com.albedo.java.vo.account.PasswordRestVo;
import com.albedo.java.vo.sys.UserDataVo;
import com.albedo.java.vo.sys.UserRestVo;
import com.albedo.java.vo.sys.UserVo;
import com.albedo.java.web.rest.ResultBuilder;
import com.albedo.java.web.rest.base.BaseResource;
import com.albedo.java.web.rest.util.CookieUtil;
import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 账户相关数据接口
 *
 * @author somewhere
 */
@RestController
@RequestMapping("${application.adminPath}/")
@Api(tags = "账户相关数据接口")
public class AccoutResource extends BaseResource {

    public final static String LOGIN_FAIL_MAP = "loginFailMap";
    @Resource
    private Environment env;
    @Resource
    private UserService userService;
    private final TokenProvider tokenProvider;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AccoutResource(TokenProvider tokenProvider, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.passwordEncoder = passwordEncoder;
        log.warn("Abledo_Boot Web [{}] 框架开发版权所有 Copyright(c) 2010-2018\n未经授权非法复制,使用,传播,销售,本公司必究法律责任", SystemConfig.get("version"));
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
    }
    /**
     * 获取账户
     *
     * @return the current user
     * @throws RuntimeException 500 (Internal Server Error) if the user couldn't be returned
     */
    @GetMapping("/account")
    @Timed
    @ApiOperation("获取账户信息")
    public ResponseEntity getAccount() {
        String id = SecurityUtil.getCurrentUserId();
        try{
            if(PublicUtil.isNotEmpty(id)){
                Optional<UserDataVo> userVo = userService.findById(id)
                    .map(item -> userService.copyBeanToDataVo(item));
                UserDataVo user = userVo.get();
                user.setAuthorities(SecurityUtil.getCurrentUserAuthorities());
                return ResultBuilder.buildOk(user);
            }
        }catch (Exception e){
            log.warn("{}",e);
        }
        return ResultBuilder.buildFailed(HttpStatus.UNAUTHORIZED, "没有数据");
    }
    /**
     * 判断用户是否授权 返回当前登陆的用户
     * GET  /authenticate : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request
     * @return the login if the user is authenticated
     */
    @GetMapping("/authenticate")
    @Timed
    @ApiOperation("判断用户是否授权")
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     *
     * 功能描述: 认证授权
     *
     */
    @PostMapping("authenticate")
    @Timed
    @ApiOperation("认证授权")
    public ResponseEntity authorize(@Valid @RequestBody LoginVo loginVo) {

        Date canLoginDate = RedisUtil.getCacheObject(AuthoritiesConstants.DEFAULT_LOGIN_AFTER_24_KEY + loginVo.getUsername());
        if(canLoginDate!=null){
            return ResultBuilder.buildFailed(HttpStatus.UNAUTHORIZED, "您的账号在"+PublicUtil.fmtDate(canLoginDate)+"后才可登录");
        }
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(loginVo.getUsername(), loginVo.getPassword());

        String keyLoginCount = AuthoritiesConstants.DEFAULT_LOGIN_KEY + loginVo.getUsername();
        try {
            Authentication authentication = this.authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            boolean rememberMe = (loginVo.isRememberMe() == null) ? false : loginVo.isRememberMe();
            String jwt = tokenProvider.createToken(authentication, rememberMe);
            String keyPrefix = AuthoritiesConstants.DEFAULT_LOGIN_JWT_KEY+loginVo.getUsername()
                , key = keyPrefix + IdGen.uuid();
            Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
            if (!activeProfiles.contains(Globals.SPRING_PROFILE_DEVELOPMENT)) {
                Long count = RedisUtil.deleteStringLike(keyPrefix+"*");
                if(count>0){
//                    WebSocketServer.sendMessageAndClose(SecurityUtil.getCurrentUserId(),
//                        "您的账号已在其他入口登录，请核查", "logout");
                }
            }
            log.info("key{}",key);
            log.info("jwt{}",jwt);
            RedisUtil.setCacheString(key, jwt,
                tokenProvider.getTokenValidityInSeconds(), TimeUnit.SECONDS);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(SecurityConstants.AUTHORIZATION_HEADER, key);
            RedisUtil.setCacheObject(keyLoginCount, 1);
            return new ResponseEntity<>(CustomMessage.createSuccessData(key), httpHeaders, HttpStatus.OK);

        } catch (AuthenticationException ae) {
            log.warn("Authentication exception trace: {}", ae);
            String msg = ae.getMessage();
            if(ae instanceof BadCredentialsException){
                Integer cacheObject = RedisUtil.getCacheObject(keyLoginCount);
                if(cacheObject == null){
                    cacheObject = 1;
                }
                msg = "密码错误，请重试";
                if(cacheObject >= 5 && cacheObject<9){
                    msg = "您还剩"+(10-cacheObject)+"次密码输入机会，建议点击‘忘记密码’";
                }else if(cacheObject == 9){
                    msg = "您还剩1次密码输入机会，再次错误，您的账号将被暂时锁定24小时，24小时内禁止登录";
                }else if(cacheObject > 9){
                    msg = "您密码错误次数已超过10次，您的账号将被暂时锁定24小时，建议点击‘忘记密码’，凭手机号码重置密码，24小时后再尝试登录";
                    cacheObject=0;
//                    RedisUtil.setCacheObject(AuthoritiesConstants.DEFAULT_LOGIN_AFTER_24_KEY +loginVo.getUsername(), DateUtil.addDays(PublicUtil.getCurrentDate(), 1), 1, TimeUnit.DAYS);
                }
                RedisUtil.setCacheObject(keyLoginCount, 1+cacheObject);
            }
            return ResultBuilder.buildFailed(HttpStatus.UNAUTHORIZED, msg);
        }
    }

    /*
     * 登出
     */
    @GetMapping(value = "logout")
    @ApiOperation("登出")
    public ResponseEntity logout(HttpServletRequest request, HttpServletResponse response) {
        RedisUtil.deleteStringLike(AuthoritiesConstants.DEFAULT_LOGIN_JWT_KEY+
            SecurityAuthUtil.getCurrentUserLogin()+"*");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        CookieUtil.removeCookie(request, response, SecurityConstants.AUTHORIZATION_HEADER);
        request.getSession().invalidate();
        return ResultBuilder.buildOk("退出登录成功");

    }

    /**
     *
     * 功能描述: 检查密码长度
     *
     * @param: [password]
     * @return: boolean
     */
    private static boolean checkPasswordLength(String password) {
        return !StringUtils.isEmpty(password) &&
            password.length() >= UserVo.PASSWORD_MIN_LENGTH &&
            password.length() <= UserVo.PASSWORD_MAX_LENGTH;
    }
    /**
     *  修改密码
     * POST  /account/changePassword : changes the current user's password
     *
     * @param passwordChangeVo the passwordVo
     */
    @ApiOperation(value = "修改密码")
    @PostMapping(path = "/account/changePassword")
    @Timed
    public ResponseEntity changePassword(@Valid @RequestBody PasswordChangeVo passwordChangeVo) {
        Assert.assertIsTrue(passwordChangeVo!=null&&
            checkPasswordLength(passwordChangeVo.getNewPassword()), "密码格式有误");
        Assert.assertIsTrue(!passwordChangeVo.getNewPassword().equals(passwordChangeVo.getOldPassword()),
            "新旧密码不能相同");
        Assert.assertIsTrue(passwordChangeVo.getNewPassword().equals(passwordChangeVo.getConfirmPassword()),
            "两次输入密码不一致");
        Assert.assertIsTrue(passwordEncoder.matches(passwordChangeVo.getOldPassword(),
            userService.findOneVo(SecurityUtil.getCurrentUserId()).getPassword()),
            "输入原密码有误");

        passwordChangeVo.setNewPassword(passwordEncoder.encode(passwordChangeVo.getNewPassword()));
        userService.changePassword(SecurityAuthUtil.getCurrentUserLogin(),
            passwordChangeVo);
        return ResultBuilder.buildOk("修改成功");
    }


//    @GetMapping(path = "/code/{randomStr}")
//    @Timed
//    @ApiOperation(value = "获取验证码")
//    public void valicode(@PathVariable String randomStr, HttpServletResponse response) throws IOException {
//        Assert.isNotEmpty(randomStr, "机器码不能为空");
//        response.setHeader("Cache-Control", "no-store, no-cache");
//        response.setContentType("image/jpeg");
//        //生成文字验证码
//        String text = producer.createText();
//        //生成图片验证码
//        BufferedImage image = producer.createImage(text);
//        userService.saveImageCode(randomStr, text);
//        ServletOutputStream out = response.getOutputStream();
//        ImageIO.write(image, "JPEG", out);
//        IOUtil.closeSafe(out);
//    }


    /**
     * 发送手机验证码
     * 后期要加接口限制
     *
     * @param mobile 手机号
     * @return R
     */
    @GetMapping("/rest/smsCode/{mobile}")
    @ApiOperation(value = "发送手机验证码")
    public ResponseEntity createCode(@PathVariable String mobile) {
        Assert.isNotEmpty(mobile, "手机号不能为空");
        userService.sendSmsCode(mobile);
        return ResultBuilder.buildOk("发送成功");
    }

    /**
     * 重置密码
     * @param passwordRestVo
     * @return
     */
    @PostMapping("/rest/password")
    @ApiOperation(value = "重置密码")
    public ResponseEntity resetPassword(@RequestBody @Valid PasswordRestVo passwordRestVo) {

        Assert.assertIsTrue(passwordRestVo.getNewPassword().equals(passwordRestVo.getConfirmPassword()),
            "两次输入密码不一致");
        passwordRestVo.setPasswordPlaintext(passwordRestVo.getNewPassword());
        passwordRestVo.setNewPassword(passwordEncoder.encode(passwordRestVo.getNewPassword()));
        userService.resetPassword(passwordRestVo);
        return ResultBuilder.buildOk("发送成功");
    }

    /**
     * 获取重置用户信息
     * @param loginId
     * @return
     */
    @GetMapping("/rest/info/{loginId}")
    @ApiOperation(value = "获取重置用户信息")
    public ResponseEntity resetPassword(@PathVariable String loginId) {
        UserVo oneByLoginId = userService.findOneVoByLoginId(loginId);
        Assert.assertIsTrue(oneByLoginId!=null && User.FLAG_NORMAL.equals(oneByLoginId.getStatus()), "当前账号已锁定");
        return ResultBuilder.buildOk(new UserRestVo(oneByLoginId));
    }


}
