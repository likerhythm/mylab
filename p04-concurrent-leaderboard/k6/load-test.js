import http from 'k6/http';
import exec from 'k6/execution';

const BASE_URL = 'http://localhost:8080';
const HEADERS = { 'Content-Type': 'application/json' };

const DURATION_SECONDS = 180;
const WARMUP_SECONDS = 5;
const MIN_AMOUNT = 10000;
const MAX_AMOUNT = 100000;
const AMOUNT_STEP = 1000;

export const options = {
    scenarios: {
        load: {
            executor: 'constant-arrival-rate',
            rate: 100,
            timeUnit: '1s',
            duration: `${DURATION_SECONDS}s`,
            preAllocatedVUs: 50,
            maxVUs: 200,
        },
    },
};

// 각 VU가 독립적으로 보유한 환불 가능 구매 목록 (userId → [{purchaseId, amount}])
const refundable = {};

export function setup() {
    const now = new Date();
    const end = new Date(now.getTime() + (DURATION_SECONDS + 1) * 1000);

    const fmt = (d) => {
        const pad = (n) => String(n).padStart(2, '0');
        return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T` +
               `${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`;
    };

    const res = http.post(
        `${BASE_URL}/api/event`,
        JSON.stringify({ startAt: fmt(now), endAt: fmt(end) }),
        { headers: HEADERS }
    );

    if (res.status !== 201) {
        console.error(`이벤트 설정 실패: ${res.status} ${res.body}`);
    } else {
        console.log(`이벤트 설정 완료: ${fmt(now)} ~ ${fmt(end)}`);
    }
}

export default function () {
    const userId = Math.floor(Math.random() * 10) + 1; // 1~10
    const elapsedSeconds = exec.scenario.progress * DURATION_SECONDS;
    const isWarmup = elapsedSeconds < WARMUP_SECONDS;

    const userRefundable = refundable[userId] || [];
    const canRefund = !isWarmup && userRefundable.length > 0 && Math.random() < 0.1;

    if (canRefund) {
        doRefund(userId, userRefundable);
    } else {
        doPurchase(userId);
    }
}

function doPurchase(userId) {
    const amount = (Math.floor(Math.random() * ((MAX_AMOUNT - MIN_AMOUNT) / AMOUNT_STEP + 1)) * AMOUNT_STEP) + MIN_AMOUNT;

    const res = http.post(
        `${BASE_URL}/api/purchase`,
        JSON.stringify({ userId, amount }),
        { headers: HEADERS }
    );

    if (res.status === 201) {
        const body = res.json();
        if (!refundable[userId]) refundable[userId] = [];
        refundable[userId].push({ purchaseId: body.purchaseId, amount });
    }
}

function doRefund(userId, userRefundable) {
    const idx = Math.floor(Math.random() * userRefundable.length);
    const item = userRefundable[idx];

    const res = http.post(
        `${BASE_URL}/api/refund`,
        JSON.stringify({ userId, amount: item.amount, purchaseId: item.purchaseId }),
        { headers: HEADERS }
    );

    if (res.status === 201) {
        userRefundable.splice(idx, 1);
    }
}
