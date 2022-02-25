package com.mt.helper;

import com.netflix.discovery.EurekaClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TestHelper {
    @Autowired
    private EurekaClient eurekaClient;

    public String getAccessUrl(String path) {
        String normalized = removeLeadingSlash(path);
        if(eurekaClient.getApplication("0C8AZTODP4H5")==null){
            return UserAction.proxyUrl+"/auth-svc/"+normalized;
        }
        return eurekaClient.getApplication("0C8AZTODP4H5").getInstances().get(0).getHomePageUrl() + normalized;
    }

    public String getMallUrl(String path) {
        String normalized = removeLeadingSlash(path);
        return eurekaClient.getApplication("0C8HPGLXHMET").getInstances().get(0).getHomePageUrl() + normalized;
    }

    public String getUserProfileUrl(String path) {
        String normalized = removeLeadingSlash(path);
        return eurekaClient.getApplication("0C8HPGLXHMET").getInstances().get(0).getHomePageUrl() + normalized;
    }

    private String removeLeadingSlash(String path) {
        return path.replaceAll("^/+", "");
    }
}
