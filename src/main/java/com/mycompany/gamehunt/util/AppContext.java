package com.mycompany.gamehunt.util;

import com.mycompany.gamehunt.service.ProductService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/** Memuat Spring (applicationContext.xml) satu kali dan menyediakan bean-nya. */
public class AppContext {
    private static ClassPathXmlApplicationContext context;

    public static synchronized ClassPathXmlApplicationContext get() {
        if (context == null) {
            context = new ClassPathXmlApplicationContext("applicationContext.xml");
        }
        return context;
    }

    public static ProductService productService() {
        return get().getBean("productService", ProductService.class);
    }
}
