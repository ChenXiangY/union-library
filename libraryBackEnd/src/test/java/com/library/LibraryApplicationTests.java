package com.library;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootTest
@MapperScan("com.library.mapper")
class LibraryApplicationTests {

    @Test
    void contextLoads() {
    }

}
