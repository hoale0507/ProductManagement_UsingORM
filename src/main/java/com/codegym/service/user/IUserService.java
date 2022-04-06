package com.codegym.service.user;

import com.codegym.model.User;
import com.codegym.service.IGenneralService;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IUserService extends IGenneralService<User>, UserDetailsService {
    User findByUsername(String username);
}
// UserDetailsService dung de load du lieu tu DB

