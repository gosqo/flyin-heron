package com.vong.manidues.config.trackingip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@Getter
@ConfigurationProperties(prefix = "blacklisted-ips")
public class BlacklistedIps {

    private List<String> blacklistedIps;
}
