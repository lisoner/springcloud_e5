package com.example.controller;

import com.example.entity.User;
import com.example.feign.ServiceProviderService;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import jakarta.annotation.Resource;
import com.example.entity.CommonResult;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.sql.SQLDataException;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/cart")
@RefreshScope//开启动态刷新地址
public class Cartcontroller {
//    @Resource
//    private RestTemplate restTemplate;
    @Resource
    private ServiceProviderService serviceProviderService;
    @GetMapping("/getCartById/{userId}")
    @TimeLimiter(name = "backendA",fallbackMethod = "getCartByIdDown")
    public CompletableFuture<CommonResult<User>> getCartById(@PathVariable("userId") Integer userId) {
        System.out.println("进入方法！");
        return CompletableFuture.completedFuture(serviceProviderService.getUserById(userId));
    }
    public CompletableFuture<User> getCartByIdDown(Integer userId, Throwable e){
        e.printStackTrace();
        String message = "获取用户"+userId+"信息的服务当前被熔断，因此方法降级";
        System.out.println(message);
        CompletableFuture<User> result = CompletableFuture.completedFuture(new CommonResult<User>(400,"fallback",new User()).getUser());
        return result;
    }

    public CommonResult<User> getCartByIdDown(Integer userId, SQLDataException e){
        e.printStackTrace();
        String message = "请联系管理员，当前数据库异常，因此方法降级";
        System.out.println(message);
        CommonResult<User> result = new CommonResult<>(400,message,new User());
        return result;
    }
}
