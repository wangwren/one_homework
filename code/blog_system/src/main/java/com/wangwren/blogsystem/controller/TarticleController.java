package com.wangwren.blogsystem.controller;

import com.wangwren.blogsystem.pojo.TarticleEntity;
import com.wangwren.blogsystem.service.TarticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("blog")
public class TarticleController {

    @Autowired
    private TarticleService tarticleService;

    @GetMapping("index")
    public String page(Integer num, Model model) {

        PageImpl<TarticleEntity> page = tarticleService.page(num);

        model.addAttribute("page",page);

        return "client/index";
    }
}
