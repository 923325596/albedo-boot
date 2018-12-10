package com.albedo.java.common.security;

import com.albedo.java.common.AuthoritiesConstants;
import com.albedo.java.modules.sys.domain.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Utility class for Spring Security.
 */
@SuppressWarnings("unchecked")
public final class SecurityAuthUtil {

    protected static Logger logger = LoggerFactory.getLogger(SecurityUtil.class);
    private SecurityAuthUtil() {
    }

    public static boolean isSystemAdmin() {
        return SecurityAuthUtil.isSystemAdmin(SecurityUtil.getCurrentUserId());
    }
    public static boolean isSystemAdmin(String id) {
        return "1".equals(id);
    }

    public static boolean isAdmin(String id) {
        boolean admin = false;
        List<Module> moduleList = SecurityUtil.getModuleList(false, id);
        for (Module item : moduleList){
            if(AuthoritiesConstants.ADMIN.equals(item.getPermission())){
                admin=true;break;
            }
        }
        return admin;
    }


    /**
     * Get the login of the current user.
     *
     * @return the login of the current user
     */
    public static String getCurrentUserLogin() {
        String userName = null;
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                userName = userPrincipal.getUsername();
            } else if (authentication.getPrincipal() instanceof UserDetails) {
                UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
                userName = springSecurityUser.getUsername();
            } else if (authentication.getPrincipal() instanceof String && !"anonymousUser".equals(authentication.getPrincipal())) {
                userName = (String) authentication.getPrincipal();
            }
        }


        return userName;
    }


    /**
     * 签名算法
     *
     * @param data
     * @return
     */
    public static String sign(Map<String, String> data, String key) {
        String resp = "";
        StringBuffer unsignString = new StringBuffer();
        data.put("sig", key);
        List<String> nameList = new ArrayList<String>(data.keySet());
        // 首先按字段名的字典序排列
        Collections.sort(nameList);
        for (String name : nameList) {
            String value = data.get(name);
            if (value != null) {
                unsignString.append(name).append("=").append(value).append("&");
            }
        }
        try {
            if (unsignString.length() > 0) {
                unsignString.delete(unsignString.length() - 1, unsignString.length());
            }
            resp = md5(unsignString.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        resp = resp.toLowerCase();
        return resp;
    }

    public static String md5(String value) throws Exception {
        MessageDigest mdInst = MessageDigest.getInstance("MD5");
        mdInst.update(value.getBytes("UTF-8"));
        byte[] arr = mdInst.digest();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < arr.length; ++i) {
            sb.append(Integer.toHexString((arr[i] & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString();
    }

    /**
     * Get the JWT of the current user.
     *
     * @return the JWT of the current user
     */
    public static String getCurrentUserJWT() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null && authentication.getCredentials() instanceof String) {
            return (String) authentication.getCredentials();
        }
        return null;
    }

}
