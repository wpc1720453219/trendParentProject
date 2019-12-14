package cn.how2j.trend.util;
 
import java.util.HashMap;
  
import cn.hutool.http.HttpUtil;
  //使用 post 的方式访问 http://localhost:8041/actuator/bus-refresh 地址，之所以要专门做一个 FreshConfigUtil 类，就是为了可以使用 post 访问，因为它不支持 get 方式访问，直接把这个地址放在浏览器里，是会抛出 405错误的。

/**
 * 因为视图服务进行了改造，支持了 rabbitMQ, 那么在默认情况下，它的信息就不会进入 Zipkin了。 在Zipkin 里看不到视图服务的资料了。
 为了解决这个问题，在启动 Zipkin 的时候 带一个参数就好了：--zipkin.collector.rabbitmq.addresses=localhost
 即本来是
 java -jar zipkin-server-2.10.1-exec.jar
 现在改成了
 java -jar zipkin-server-2.10.1-exec.jar --zipkin.collector.rabbitmq.addresses=localhost
 注： 重启 zipkin 后，要再访问业务地址才可以看到依赖关系：
 http://127.0.0.1:8031/api-view/
 */
public class FreshConfigUtil {
  
    public static void main(String[] args) {
        HashMap<String,String> headers =new HashMap<>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        System.out.println("因为要去git获取，还要刷新index-config-server, 会比较卡，所以一般会要好几秒才能完成，请耐心等待");
  
        String result = HttpUtil.createPost("http://localhost:8041/actuator/bus-refresh").addHeaders(headers).execute().body();
        System.out.println("result:"+result);
        System.out.println("refresh 完成");
    }
}