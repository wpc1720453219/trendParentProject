package cn.how2j.trend.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import cn.how2j.trend.pojo.IndexData;
import cn.how2j.trend.util.SpringContextUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;

@Service
@CacheConfig(cacheNames = "index_datas")
public class IndexDataService {
    private Map<String, List<IndexData>> indexDatas = new HashMap<>();
    @Autowired
    RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "third_part_not_connected")
    public List<IndexData> fresh(String code) {
        List<IndexData> indexeDatas = fetch_indexes_from_third_part(code);

        indexDatas.put(code, indexeDatas);

        System.out.println("code:" + code);
        System.out.println("indexeDatas:" + indexDatas.get(code).size());

        IndexDataService indexDataService = SpringContextUtil.getBean(IndexDataService.class);
        indexDataService.remove(code);
        return indexDataService.store(code);
    }

    //用来标记要清空缓存的方法
    @CacheEvict(key = "'indexData-code-'+ #p0")
    public void remove(String code) {

    }

    //主要针对方法的配置，能够根据方法的请求参数对其结果进行缓存，和@Cacheable不同的是，它每次都会触发真实方法的调用
    // #p0的意思是指加有@Cacheable注解的方法中的第一个参数
    @CachePut(key = "'indexData-code-'+ #p0")
    public List<IndexData> store(String code) {
        return indexDatas.get(code);
    }

    @Cacheable(key = "'indexData-code-'+ #p0")
    public List<IndexData> get(String code) {
        return CollUtil.toList();
    }

    public List<IndexData> fetch_indexes_from_third_part(String code) {
        List<Map> temp = restTemplate.getForObject("http://127.0.0.1:8090/indexes/" + code + ".json", List.class);
        return map2IndexData(temp);
    }

    private List<IndexData> map2IndexData(List<Map> temp) {
        List<IndexData> indexDatas = new ArrayList<>();
        for (Map map : temp) {
            String date = map.get("date").toString();
            float closePoint = Convert.toFloat(map.get("closePoint"));
            IndexData indexData = new IndexData();

            indexData.setDate(date);
            indexData.setClosePoint(closePoint);
            indexDatas.add(indexData);
        }

        return indexDatas;
    }

    public List<IndexData> third_part_not_connected(String code) {
        System.out.println("third_part_not_connected()");
        IndexData index = new IndexData();
        index.setClosePoint(0);
        index.setDate("n/a");
        return CollectionUtil.toList(index);
    }

}