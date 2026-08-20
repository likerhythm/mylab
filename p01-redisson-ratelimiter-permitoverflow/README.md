# Lab: Rate Limiter throughput exceeding its cap when using RedissonRateLimiter.release()

This lab is split into two versions: one without Spring (the `nonspring_test` package) and one with Spring (the `spring_test` package).

## nonspring_test

Demonstrates that when the permit count is observed via `availablePermitsAsync()`, the count rises above the configured rate.

When the `main` method of the `Main` class finishes running, a `permit-history.json` file is generated. Upload this file to `nonspring_test/visualize.html` to see the permit count fluctuations plotted as a graph.

## spring_test

Shows that because the permit count rises above the rate, the Rate Limiter's actual throughput increases.

Start the Spring server, then open a terminal and navigate to the `spring_test/k6` folder. Run the `./run-10x.ps1` command to execute a total of 5 k6 tests.

This produces an `aggregated.json` file in the `spring_test/k6/results` folder. Upload it to `spring_test/k6/visualize.html` to see the number of successful and rejected requests plotted as a graph.

# RedissonRateLimiter의 release()를 사용하면 Rate Limiter의 throughput이 상한을 넘는 현상 실험실

이 실험은 스프링을 사용하지 않은 버전(`nonspring_test` 패키지)과 사용한 버전(`spring_test` 패키지)으로 나뉜다.

## nonspring_test

`availablePermitsAsync()`로 permit 수를 관측할 때 그 수가 rate보다 높아지는 현상을 보여준다.

`Main` 클래스의 `main` 함수 실행이 완료되면 `permit-history.json` 파일이 생성된다. 이 파일을 `nonspring_test/visualize.html`에 업로드하면 permit 수의 변동이 그래프로 그려진다.

## spring_test

permit 수가 rate보다 높아지는 현상으로 인해 실제로 Rate Limiter의 throughput이 커지는 현상을 보여준다.

스프링 서버를 실행한 뒤, 터미널에서 `spring_test/k6` 폴더로 이동해 `./run-10x.ps1` 명령어를 실행하면 총 5번의 k6 테스트가 진행된다.

그 결과 `spring_test/k6/results` 폴더에 `aggregated.json` 파일이 생성되고, 이를 `spring_test/k6/visualize.html`에 업로드하면 성공한 요청과 거부된 요청의 수가 그래프로 나타난다.
