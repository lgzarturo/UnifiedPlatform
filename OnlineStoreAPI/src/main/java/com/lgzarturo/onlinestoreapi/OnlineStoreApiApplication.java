package com.lgzarturo.onlinestoreapi;

import com.lgzarturo.common.dto.User;
import com.lgzarturo.common.libs.ShortRandomString;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OnlineStoreApiApplication {

    public static void main(String[] args) {
        User user = User.builder().username("Arturo").build();
        System.out.println(user.getUsername());
        System.out.println(ShortRandomString.generate(10));
        SpringApplication.run(OnlineStoreApiApplication.class, args);
    }

}
