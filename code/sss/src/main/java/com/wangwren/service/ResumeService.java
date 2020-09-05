package com.wangwren.service;

import com.wangwren.pojo.Resume;

import java.util.List;

public interface ResumeService {

    List<Resume> queryAll();

    void addResume(Resume resume);

    void deleteById(Long id);

    Resume queryInfo(Long id);
}
