package com.example.storesaas.common;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 通用分页返回结构
 * @param records 当前页数据
 * @param total   总记录数
 * @param current 当前页码（从 1 开始）
 * @param size    每页条数
 */
public record PageResult<T>(List<T> records, long total, long current, long size) {

    public static <T> PageResult<T> of(IPage<?> page, List<T> records) {
        return new PageResult<>(records, page.getTotal(), page.getCurrent(), page.getSize());
    }
}