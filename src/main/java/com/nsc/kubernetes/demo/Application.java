package com.nsc.kubernetes.demo;

import com.nsc.kubernetes.demo.model.AppUser;
import com.nsc.kubernetes.demo.model.Item;
import com.nsc.kubernetes.demo.repository.ItemRepository;
import com.nsc.kubernetes.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
@EnableSwagger2
public class Application {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    public void initialize() {
        Set<Item> items = new HashSet<>();
        for (int i = 1; i <= 10; i++) {
            Item item = new Item();
            item.setClinicalItemId("Clinical_Item_ID_" + i);
            item.setName("Clinical_Item_Name_" + i);
            items.add(item);
        }
        itemRepository.saveAll(items);
    }

    @PostConstruct
    public void addAdminUser() {
        Set<String> roles = new HashSet<>();
        roles.add("ADMIN");
        roles.add("USER");
        AppUser appUser = AppUser.builder().userId("admin")
                .name("Admin")
                .password(bCryptPasswordEncoder.encode("admin"))
                .roles(roles)
                .build();

        userRepository.save(appUser);
    }
}
