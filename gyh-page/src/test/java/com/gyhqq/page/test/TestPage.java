package com.gyhqq.page.test;

import com.gyhqq.page.service.PageService;
import net.bytebuddy.asm.Advice;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestPage {

    @Autowired
    private PageService pageService;
    @Test
    public void createHtml(){
        List<Long> ids = Arrays.asList(5L, 6L, 118L, 119L, 120L);
        for (Long id : ids) {
            pageService.createHtml(id);
        }

    }
}
