# Authentication Setup Guide

## 🔐 **Basic Authentication Implementation**

This guide explains how to add basic authentication to your player service endpoints.

## 📋 **Prerequisites**

### 1. **Maven Dependency**
Add Spring Security to your `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### 2. **Security Configuration**
Create `src/main/java/com/app/playerservicejava/config/SecurityConfiguration.java`:

```java
package com.app.playerservicejava.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/h2-console/**").permitAll() // Allow H2 console access
                .anyRequest().authenticated() // Require authentication for all other requests
            )
            .httpBasic(basic -> {}) // Enable basic authentication
            .csrf(csrf -> csrf.disable()) // Disable CSRF for API endpoints
            .headers(headers -> headers.frameOptions().disable()); // Allow H2 console frames
        
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("admin123"))
            .roles("ADMIN")
            .build();

        UserDetails user = User.builder()
            .username("user")
            .password(passwordEncoder().encode("user123"))
            .roles("USER")
            .build();

        return new InMemoryUserDetailsManager(admin, user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### 3. **Application Configuration**
Add to `src/main/resources/application.yml`:

```yaml
# Security Configuration
spring:
  security:
    user:
      name: admin
      password: admin123
```

## 👥 **Default Users**

### **Admin User**
- **Username**: `admin`
- **Password**: `admin123`
- **Role**: `ADMIN`
- **Base64 Encoded**: `YWRtaW46YWRtaW4xMjM=`

### **Regular User**
- **Username**: `user`
- **Password**: `user123`
- **Role**: `USER`
- **Base64 Encoded**: `dXNlcjp1c2VyMTIz`

## 🧪 **Testing with Authentication**

### **Using curl**

#### **Create Player (Admin)**
```bash
curl -X POST http://localhost:8080/v1/players/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -d '{
    "playerId": "auth001",
    "firstName": "Auth",
    "lastName": "User",
    "birthYear": "1990"
  }'
```

#### **Get All Players (Admin)**
```bash
curl -X GET http://localhost:8080/v1/players \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="
```

#### **Get Specific Player (User)**
```bash
curl -X GET http://localhost:8080/v1/players/auth001 \
  -H "Authorization: Basic dXNlcjp1c2VyMTIz"
```

### **Using Postman**

1. **Add Authorization Header**:
   - Key: `Authorization`
   - Value: `Basic YWRtaW46YWRtaW4xMjM=` (for admin)
   - Value: `Basic dXNlcjp1c2VyMTIz` (for user)

2. **Or use Postman's Basic Auth**:
   - Type: `Basic Auth`
   - Username: `admin` or `user`
   - Password: `admin123` or `user123`

### **Using HTTP Test Files**
Use `collection/CreatePlayerWithAuth.http` for authenticated requests.

## 🔒 **Security Features**

### **What's Protected**
- ✅ All `/v1/players/**` endpoints
- ✅ POST, GET, PUT, DELETE operations
- ✅ Both general and specific create endpoints

### **What's Not Protected**
- ✅ `/h2-console/**` (database console access)
- ✅ Health check endpoints (if any)

### **Authentication Method**
- ✅ **Basic Authentication** (username/password)
- ✅ **Base64 encoded** credentials in Authorization header
- ✅ **In-memory user storage** (for development)

## 🚀 **Production Considerations**

### **For Production Use**
1. **Database User Storage**: Replace in-memory with database
2. **JWT Tokens**: Consider JWT for stateless authentication
3. **Password Policies**: Implement strong password requirements
4. **HTTPS**: Always use HTTPS in production
5. **Rate Limiting**: Add rate limiting to prevent abuse

### **Enhanced Security Configuration**
```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(authz -> authz
            .requestMatchers("/h2-console/**").permitAll()
            .requestMatchers("/v1/players/**").hasAnyRole("ADMIN", "USER")
            .requestMatchers("/v1/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        )
        .httpBasic(basic -> {})
        .csrf(csrf -> csrf.disable())
        .headers(headers -> headers.frameOptions().disable());
    
    return http.build();
}
```

## 🔧 **Troubleshooting**

### **Common Issues**

1. **401 Unauthorized**
   - Check username/password
   - Verify Base64 encoding
   - Ensure Authorization header is present

2. **403 Forbidden**
   - Check user roles
   - Verify endpoint permissions

3. **Dependency Issues**
   - Ensure Spring Security dependency is added
   - Check Maven repository connectivity

### **Testing Authentication**
```bash
# Test without auth (should return 401)
curl -X GET http://localhost:8080/v1/players

# Test with auth (should return 200)
curl -X GET http://localhost:8080/v1/players \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="
```

## 📚 **Additional Resources**

- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/)
- [Basic Authentication Guide](https://en.wikipedia.org/wiki/Basic_access_authentication)
- [HTTP Authorization Header](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Authorization)

## ✅ **Current Status**

- ✅ **Configuration Ready**: Security configuration is prepared
- ⏳ **Dependency Issue**: Maven repository connectivity problem
- ✅ **Documentation Complete**: Full setup guide provided
- ✅ **Test Files Ready**: Authentication test files created

**Next Step**: Resolve Maven dependency issues and uncomment the security configuration. 