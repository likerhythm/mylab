import http from 'k6/http';
import { Counter } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const RUN_NO = __ENV.RUN_NO || '00';

// API별 성공/거부 카운터
const nonReleaseSuccess = new Counter('non_release_success');
const nonReleaseRejected = new Counter('non_release_rejected');
const doReleaseSuccess = new Counter('do_release_success');
const doReleaseRejected = new Counter('do_release_rejected');

export const options = {
    scenarios: {
        // 1) non-release 를 30초 동안 초당 20개
        non_release: {
            executor: 'constant-arrival-rate',
            rate: 20,
            timeUnit: '1s',
            duration: '30s',
            preAllocatedVUs: 50,
            maxVUs: 200,
            exec: 'testNonRelease',
            startTime: '0s',
        },
        // 2) non-release 끝나고 permit 회복 시간(10초)을 두고 do-release 실행
        do_release: {
            executor: 'constant-arrival-rate',
            rate: 20,
            timeUnit: '1s',
            duration: '30s',
            preAllocatedVUs: 50,
            maxVUs: 200,
            exec: 'testDoRelease',
            startTime: '40s',
        },
    },
};

export function testNonRelease() {
    const res = http.post(`${BASE_URL}/non-release`);
    if (res.status === 429) nonReleaseRejected.add(1);
    else if (res.status >= 200 && res.status < 300) nonReleaseSuccess.add(1);
}

export function testDoRelease() {
    const res = http.post(`${BASE_URL}/do-release`);
    if (res.status === 429) doReleaseRejected.add(1);
    else if (res.status >= 200 && res.status < 300) doReleaseSuccess.add(1);
}

export function handleSummary(data) {
    const get = (n) => (data.metrics[n] ? data.metrics[n].values.count : 0);

    const result = {
        run: parseInt(RUN_NO, 10),
        non_release: {
            success: get('non_release_success'),
            rejected: get('non_release_rejected'),
        },
        do_release: {
            success: get('do_release_success'),
            rejected: get('do_release_rejected'),
        },
    };

    const pad = String(RUN_NO).padStart(2, '0');

    return {
        // 실행 번호별로 파일 저장 → run_01.json, run_02.json ...
        [`results/run_${pad}.json`]: JSON.stringify(result, null, 2),
        stdout:
            `\n[Run ${result.run}] ` +
            `non-release ok=${result.non_release.success} rej=${result.non_release.rejected} | ` +
            `do-release ok=${result.do_release.success} rej=${result.do_release.rejected}\n`,
    };
}