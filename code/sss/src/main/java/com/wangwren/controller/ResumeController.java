package com.wangwren.controller;

import com.wangwren.pojo.Resume;
import com.wangwren.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wwr
 */
@Controller
@RequestMapping("/resume")
public class ResumeController {


    @Autowired
    private ResumeService resumeService;

    /**
     * 查询所有
     * @return
     */
    @RequestMapping("queryAll")
    @ResponseBody
    public List<Resume> queryAll() {
        return resumeService.queryAll();
    }

    /**
     * 新增或修改
     */
    @PostMapping("addResume")
    public String addResume(Resume resume) {

        resumeService.addResume(resume);

        return "redirect:/list";
    }

    /**
     * 根据id删除
     */
    @GetMapping("deleteById")
    @ResponseBody
    public Map<Integer,String> deleteById(@RequestParam("id") Long id) {
        resumeService.deleteById(id);

        Map<Integer,String> map = new HashMap(16);
        map.put(200,"success");
        return map;
    }

    /**
     * 根据id查询
     * @return
     */
    @GetMapping("queryInfo")
    public ModelAndView queryInfo(@RequestParam("id") Long id) {
        Resume resume = resumeService.queryInfo(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject(resume);
        modelAndView.setViewName("update");

        return modelAndView;
    }
}
