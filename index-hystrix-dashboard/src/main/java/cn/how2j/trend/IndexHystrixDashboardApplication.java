package cn.how2j.trend;
 //断路器监控台
/**
 * 先启动 EurekaServerApplication
 然后启动 IndexDataApplication
 接着启动 TrendTradingBackTestServiceApplication。
 最后启动 IndexHystrixDashboardApplication
 运行 AccessService 类
 注： 记得运行redis-server.exe 以启动 redis 服务器
 然后访问地址：
 http://localhost:8070/hystrix
 输入参数：
 http://localhost:8051/actuator/hystrix.stream
 */

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
   
import cn.hutool.core.util.NetUtil;
   
@SpringBootApplication
@EnableHystrixDashboard
public class IndexHystrixDashboardApplication {
    public static void main(String[] args) {
        int port = 8070;
        int eurekaServerPort = 8761;
        if(NetUtil.isUsableLocalPort(eurekaServerPort)) {
            System.err.printf("检查到端口%d 未启用，判断 eureka 服务器没有启动，本服务无法使用，故退出%n", eurekaServerPort );
            System.exit(1);
        }
        if(!NetUtil.isUsableLocalPort(port)) {
            System.err.printf("端口%d被占用了，无法启动%n", port );
            System.exit(1);
        }
 
        new SpringApplicationBuilder(IndexHystrixDashboardApplication.class).properties("server.port=" + port).run(args);
   
    }
   
}