package com.github.mahui53541.graduation.security.jwt;

import com.github.mahui53541.graduation.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;

/**
 * JwtToken工具类
 *
 * @Description: JwtToken工具类
 * @Author: MaHui
 * @CreateDate: 2018/5/4 15:07
 * @Version: 1.0
 */
@Component
public class JwtTokenUtil implements Serializable {
    private static final String CLAIM_KEY_USERNAME = "sub";
    private static final String CLAIM_KEY_CREATED = "created";
    private static final String CLAIM_KEY_AUTHORITIES = "authorities";
    private static final String CLAIM_KEY_USERDETAIL="userdetail";
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 从Token中获取用户名
     * @param token
     * @return
     */
    public String getUsernameFromToken(String token) {
        String username;
        try {
            final Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }
    /**
     * 从Token中获取用户
     * @param token
     * @return
     */
    public UserDetails getUserDetailsFromToken(String token) {
        try {
            final Claims claims = getClaimsFromToken(token);
            LinkedHashMap map= (LinkedHashMap) claims.get(CLAIM_KEY_USERDETAIL);
            List authorities=(ArrayList<LinkedHashMap>)map.get("authorities");
            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            for(Object authority:authorities){
                grantedAuthorities.add(new SimpleGrantedAuthority((String) ((LinkedHashMap)authority).get("authority")));
            }
            JwtUser userDetails=new JwtUser(
                    (Integer) map.get("id"),
                    (String) map.get("username"),
                    (String) map.get("password"),
                    (Boolean) map.get("sex"),
                    (String) map.get("phoneNumber"),
                    (String) map.get("nickname"),
                    (Boolean)map.get("deleted"),
                    grantedAuthorities
            );
            return userDetails;
        } catch (Exception e) {
            return null;
        }

    }
    /**
     * 从Token获取失效日期
     * @param token
     * @return
     */
    public Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            final Claims claims = getClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
        }
        return expiration;
    }

    /**
     * 从Token中获取声明
     * @param token
     * @return
     */
    private Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    /**
     * 生成失效时间
     * @return
     */
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

    /**
     * 验证Token是否过期
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 声明头部
     * @param userDetails
     * @return
     */
    public String generateToken(UserDetails userDetails) {
        JwtUser user = (JwtUser) userDetails;
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
        claims.put(CLAIM_KEY_USERDETAIL,userDetails);
        claims.put(CLAIM_KEY_CREATED, new Date());
        claims.put(CLAIM_KEY_AUTHORITIES,user.getAuthorities());
        return generateToken(claims);
    }

    /**
     * 生成token
     * @param claims
     * @return
     */
    String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 刷新Token
     * @param token
     * @return
     */
    public String refreshToken(String token) {
        String refreshedToken;
        try {
            final Claims claims = getClaimsFromToken(token);
            claims.put(CLAIM_KEY_CREATED, new Date());
            refreshedToken = generateToken(claims);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    /**
     * 验证Token
     * @param token
     * @return
     */
    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }
}
