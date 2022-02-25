package com.mt.helper;

import com.netflix.discovery.EurekaClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TestHelper {
    @Autowired(required = false)
    private EurekaClient eurekaClient;
    @Value("${enable_registry}")
    private boolean registryEnable;

    public String getAccessUrl(String path) {
        String normalized = removeLeadingSlash(path);
        if(registryEnable){
            if(eurekaClient.getApplication("0C8AZTODP4H5")==null){
                return UserAction.proxyUrl+"/auth-svc/"+normalized;
            }
            return eurekaClient.getApplication("0C8AZTODP4H5").getInstances().get(0).getHomePageUrl() + normalized;
        }else{
            return "http://localhost:8080/" + normalized;
        }
    }

    public String getMallUrl(String path) {
        String normalized = removeLeadingSlash(path);
        if(registryEnable){
            return eurekaClient.getApplication("0C8HPGLXHMET").getInstances().get(0).getHomePageUrl() + normalized;
        }
        return "http://localhost:8083/" + normalized;
    }

    public String getUserProfileUrl(String path) {
        String normalized = removeLeadingSlash(path);
        if(registryEnable){
            return eurekaClient.getApplication("0C8HPGLXHMET").getInstances().get(0).getHomePageUrl() + normalized;
        }
        return "http://localhost:8083/" + normalized;
    }

    private String removeLeadingSlash(String path) {
        return path.replaceAll("^/+", "");
    }
}
