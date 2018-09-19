package io.pivotal.pal.tracker;

import java.util.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;

@RestController
public class EnvController {

  private final String port;
  private final String memoryLimit;
  private final String cfInstanceIndex;
  private final String cfInstanceAddr;

  public EnvController(
      @Value("${PORT:NOT SET}") String port,
      @Value("${MEMORY_LIMIT:NOT SET}") String memoryLimit,
      @Value("${CF_INSTANCE_INDEX:NOT SET}") String cfInstanceIndex,
      @Value("${CF_INSTANCE_ADDR:NOT SET}") String cfInstanceAddr
  ) {
    this.port = port;
    this.memoryLimit = memoryLimit;
    this.cfInstanceIndex = cfInstanceIndex;
    this.cfInstanceAddr = cfInstanceAddr;
  }

  @GetMapping("/env")
  public Map<String, String> getEnv() {
    Map<String, String> envVars = new HashMap<String, String>();
    envVars.put("PORT", port);
    envVars.put("MEMORY_LIMIT", memoryLimit);
    envVars.put("CF_INSTANCE_ADDR", cfInstanceAddr);
    envVars.put("CF_INSTANCE_INDEX", cfInstanceIndex);
    return envVars;
  }
}