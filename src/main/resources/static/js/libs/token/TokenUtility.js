import { Fetcher } from "../common/Fetcher.js";

export class TokenUtility {
    static parseJwt(token) {
        const base64Url = token.split(".")[1];
        const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
        const jsonPayload = decodeURIComponent(window.atob(base64)
            .split("")
            .map(function (c) {
                return "%"
                    + ("00" + c.charCodeAt(0).toString(16))
                        .slice(-2);
            })
            .join("")
        );

        return JSON.parse(jsonPayload);
    }

    static saveToken(responseToken) {
        if (responseToken.access_token === undefined || responseToken.accessToken === null)
            throw new Error("JWToken cannot be undefined or null.");

        localStorage.setItem("access_token", `Bearer ${responseToken.access_token}`);
    }

    static forceReissueAccessToken() {
        const accessToken = localStorage.getItem("access_token");
        const parsed = this.parseJwt(accessToken);
        const expiry = parsed.exp * 1000; //javascript epoch from milliseconds
        const now = Date.now();
        const toExpiry = expiry - now; // from milliseconds
        const fiveMinutes = 1000 * 60 * 5; // 5 mins
        const updateTimeout = toExpiry - fiveMinutes; // minus 5 mins.
        const intervalTimeout = 1000 * 60 * 25; // 25 mins

        setTimeout(() => {
            Fetcher.refreshBeforeAuthRequiredRequest();
        }, updateTimeout)

        setInterval(() => {
            Fetcher.refreshBeforeAuthRequiredRequest();
        }, intervalTimeout)
    }

    static invalidateRefreshTokenInLocalStorage() {
        const refreshTokenInLocalStorage = localStorage.getItem("refresh_token");

        if (refreshTokenInLocalStorage !== null) {
            localStorage.removeItem("access_token");
            localStorage.removeItem("refresh_token");
            
            alert("인증 정보 관리 방식 변경으로 로그아웃 처리되었습니다. 이후 로그인 하시면 정상적으로 회원 기능을 사용하실 수 있습니다.")
            throw new Error("logged out.");
        }
    }

}
