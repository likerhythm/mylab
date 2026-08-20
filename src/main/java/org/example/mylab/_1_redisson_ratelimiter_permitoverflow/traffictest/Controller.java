package org.example.mylab._1_redisson_ratelimiter_permitoverflow.traffictest;

import org.example.mylab._1_redisson_ratelimiter_permitoverflow.RateLimiterConfig;
import org.example.mylab._1_redisson_ratelimiter_permitoverflow.SimulationConfig;
import org.redisson.api.RRateLimiter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class Controller {

    private static final RateLimiterConfig rateLimiterConfig = new RateLimiterConfig();
    private static final RRateLimiter rateLimiter = rateLimiterConfig.create();

    /** releaseAsync를 호출하지 않는 api */
    @PostMapping("/non-release")
    public void nonRelease() {
        simulate(false);
    }

    /** releaseAsync를 호출하는 api */
    @PostMapping("/do-release")
    public void doRelease() {
        simulate(true);
    }

    private void simulate(boolean release) {
        if (!rateLimiter.tryAcquire(1)) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS);
        }
        try {
            Thread.sleep(SimulationConfig.RELEASE_DELAY_MS);
        } catch (InterruptedException e) {
            System.out.println("시뮬레이션 도중 인터럽트됨");
        }
        if (release) rateLimiter.release(1);
    }
}
