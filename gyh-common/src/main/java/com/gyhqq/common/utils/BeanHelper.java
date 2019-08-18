package com.gyhqq.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author huyi.zhang
 */
@Slf4j
public class BeanHelper {

    /**
     * 把类转为另外一个类
     * @param source
     * @param target
     * @param <T>
     * @return
     */
    public static <T> T copyProperties(Object source, Class<T> target){
        try {
            T t = target.newInstance();
            BeanUtils.copyProperties(source, t);
            return t;
        } catch (Exception e) {
            log.error("【数据转换】数据转换出错，目标对象{}构造函数异常", target.getName(), e);
//            throw new LyException(ExceptionEnum.DATA_TRANSFER_ERROR);
            return null;
        }
    }

    /**
     * 把一个list转换成某一种类型的list
     * @param sourceList
     * @param target
     * @param <T>
     * @return
     */
    public static <T> List<T> copyWithCollection(List<?> sourceList, Class<T> target){
        try {
            return sourceList.stream().map(s -> copyProperties(s, target)).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("【数据转换】数据转换出错，目标对象{}构造函数异常", target.getName(), e);
//            throw new LyException(ExceptionEnum.DATA_TRANSFER_ERROR);
            return null;
        }
    }

    /**
     * 转一下set集合,其实是转里面的泛型
     * @param sourceList
     * @param target
     * @param <T>
     * @return
     */
    public static <T> Set<T> copyWithCollection(Set<?> sourceList, Class<T> target){
        try {
            return sourceList.stream().map(s -> copyProperties(s, target)).collect(Collectors.toSet());
        } catch (Exception e) {
            log.error("【数据转换】数据转换出错，目标对象{}构造函数异常", target.getName(), e);
            //throw new LyException(ExceptionEnum.DATA_TRANSFER_ERROR);
            return null;
        }
    }
}
