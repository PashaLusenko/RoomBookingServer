package com.example.start.booking.rest;

import com.example.start.booking.data.UserRepository;
import com.example.start.booking.model.AngularUser;
import com.example.start.booking.model.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class RestUsersController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping()
    public List<AngularUser> getAllUsers() {
        return userRepository.findAll().parallelStream()
                .map(AngularUser::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public AngularUser getUser(@PathVariable("id") Long id) {
        return new AngularUser(userRepository.findById(id).get());
    }
    @PutMapping()
    public AngularUser updateUser(@RequestBody AngularUser updatedUser) {
        User originalUser = userRepository.findById(updatedUser.getId()).get();
        originalUser.setName(updatedUser.getName());
        return new AngularUser(userRepository.save(originalUser));
    }
    @PostMapping()
    public AngularUser newUser(@RequestBody User user) {
        return new AngularUser(userRepository.save(user));
    }
    @DeleteMapping("/{id}")
    public void deleteUser (@PathVariable("id") Long id) {
        userRepository.deleteById(id);
    }
    @GetMapping("/resetPassword/{id}")
    public void resetPassword (@PathVariable("id") Long id) {
        User user = userRepository.findById(id).get();
        user.setPassword("secret");
        userRepository.save(user);
    }

    @GetMapping("/currentUserRole")
    public Map<String, String> getCurrentUserRole() {
        Collection<GrantedAuthority> roles = (Collection<GrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        String role = "";
        if (roles.size() > 0) {
            GrantedAuthority ga = roles.iterator().next();
            role = ga.getAuthority().substring(5);
        }
        Map<String, String> results = new HashMap<>();
        results.put("role", role);
        return results;
    }
    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setPath("/api");
        cookie.setHttpOnly(true);
//        cookie.setSecure(true); при развертывании на живом сервере, который использует ssl
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        SecurityContextHolder.getContext().setAuthentication(null);
        return "";
    }
}
