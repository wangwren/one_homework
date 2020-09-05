package com.wangwren.service.impl;

import com.wangwren.dao.ResumeDao;
import com.wangwren.pojo.Resume;
import com.wangwren.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ResumeServiceImpl implements ResumeService {

    @Autowired
    private ResumeDao resumeDao;

    @Override
    public List<Resume> queryAll() {

        List<Resume> resumes = resumeDao.findAll();
        return resumes;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addResume(Resume resume) {
        resumeDao.save(resume);
    }

    @Override
    public void deleteById(Long id) {
        resumeDao.deleteById(id);
    }

    @Override
    public Resume queryInfo(Long id) {

        Optional<Resume> optional = resumeDao.findById(id);
        return optional.get();
    }
}
