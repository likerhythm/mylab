package org.example.mylab._1_redisson_ratelimiter_permitoverflow;

public class Main {

    public static void main(String[] args) {
        Simulator simulator = new Simulator();
        try {
            simulator.run();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("실행 중 인터럽트됨");
        } finally {
            RateLimiterConfig.shutdown();
        }
    }
}
