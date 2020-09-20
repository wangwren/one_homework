package com.wangwren.blogsystem.service.impl;

import com.wangwren.blogsystem.dao.TarticleRepository;
import com.wangwren.blogsystem.pojo.TarticleEntity;
import com.wangwren.blogsystem.service.TarticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author wwr
 */
@Service
public class TarticleServiceImpl implements TarticleService {

    @Autowired
    private TarticleRepository tarticleRepository;


    /**
     * 分页查询数据
     * @return
     */
    @Override
    public PageImpl<TarticleEntity> page(Integer num) {

        //从第0页开始，一页查询三条
        Pageable pageable = PageRequest.of(num == null ? 0 : num, 2);
        //返回page的实现类
        PageImpl<TarticleEntity> page = (PageImpl<TarticleEntity>) tarticleRepository.findAll(pageable);

        return page;
    }
}
