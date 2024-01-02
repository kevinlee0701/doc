package kevinlee.demo.es;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @description: 京东产品
 * @Author kevinlee
 * @Date  2021/11/22
 **/
@Repository
public interface LianJiaDao extends ElasticsearchRepository<com.kevinlee.demo.es.LianJia,String> {
}
