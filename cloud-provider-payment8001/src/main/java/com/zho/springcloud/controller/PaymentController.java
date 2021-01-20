package com.zho.springcloud.controller;


import com.zho.springcloud.entity.CommonResult;
import com.zho.springcloud.entity.Payment;
import com.zho.springcloud.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.omg.PortableInterceptor.INACTIVE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;


@RestController
@Slf4j
@RequestMapping("/payment")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @Value("${server.port}")
    private String serverPort;

    @Resource
    private DiscoveryClient discoveryClient;

    @PostMapping("/create")
    public CommonResult<Payment> create(@RequestBody Payment payment) {
        int i = paymentService.create(payment);
        log.info("----------插入结果：" + i);
        return i > 0 ? new CommonResult(200, "插入数据库成功  serverPort:" + serverPort, i) : new CommonResult(444, "插入数据库失败  serverPort:" + serverPort, null);
    }

    @GetMapping("get/{id}")
    public CommonResult<Payment> getPaymentById(@PathVariable("id") Long id) {
        Payment payment = paymentService.getPaymentById(id);

        log.info("----------获取结果：" + payment);
        return payment != null ? new CommonResult(200, "查询成功  serverPort:" + serverPort, payment) : new CommonResult(444, "没有记录，查询id：" + id + "  serverPort:" + serverPort, null);
    }

    @GetMapping("/discovery")
    public Object discovery(){
        List<String> services = discoveryClient.getServices();
        for (String service : services) {
            log.info("------------------element:" + service);
        }
        List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");
        for (ServiceInstance instance : instances) {
            log.info(instance.getServiceId() + "\t" + instance.getHost() + "\t" + instance.getPort() + "\t" + instance.getUri());
        }
        return this.discoveryClient;
    }
    @GetMapping(value = "/lb")
    public String getPaymentLB() {
        return serverPort;
    }

    @GetMapping("feign/timeout")
    public  String paymentFeignTimeout() {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return serverPort;
    }

}
