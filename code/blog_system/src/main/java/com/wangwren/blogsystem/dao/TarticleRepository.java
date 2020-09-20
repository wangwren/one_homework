package com.wangwren.blogsystem.dao;

import com.wangwren.blogsystem.pojo.TarticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TarticleRepository extends JpaRepository<TarticleEntity,Integer>, JpaSpecificationExecutor<TarticleEntity> {
}
