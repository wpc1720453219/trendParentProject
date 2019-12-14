package cn.how2j.trend;

import brave.sampler.Sampler;
import cn.hutool.core.util.NetUtil;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

/**
 * 先启动 EurekaServerApplication
 * 然后启动 IndexCodesApplication 3个，端口号分别是 8011，8012，8013
 * 最后启动 IndexZuulServiceApplication
 * 访问网关地址：http://127.0.0.1:8031/api-codes/codes
 * 可以达到访问以下任意个微服务的效果
 * http://127.0.0.1:8011/codes
 * http://127.0.0.1:8012/codes
 * http://127.0.0.1:8013/codes
 * 因为这3个微服务是属于一个集群的，所以访问网关会自动在集群里的这些微服务之间进行切换，这样就达到了动态平衡的效果
 */
@SpringBootApplication
@EnableZuulProxy  //表示这是个网关
@EnableEurekaClient
@EnableDiscoveryClient
public class IndexZuulServiceApplication {
    //  http://127.0.0.1:8031/api-codes/codes
    public static void main(String[] args) {
        int port = 8031;
        if(!NetUtil.isUsableLocalPort(port)) {
            System.err.printf("端口%d被占用了，无法启动%n", port );
            System.exit(1);
        }
        new SpringApplicationBuilder(IndexZuulServiceApplication.class).properties("server.port=" + port).run(args);

    }

    @Bean
    public Sampler defaultSampler() {
        return Sampler.ALWAYS_SAMPLE;
    }
}