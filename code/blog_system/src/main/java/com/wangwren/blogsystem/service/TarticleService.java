package com.wangwren.blogsystem.service;

import com.wangwren.blogsystem.pojo.TarticleEntity;
import org.springframework.data.domain.PageImpl;

public interface TarticleService {

    PageImpl<TarticleEntity> page(Integer num);
}
