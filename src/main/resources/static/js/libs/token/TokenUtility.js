import { Fetcher } from "../common/Fetcher.js";

export default class TokenUtility {
    static removeTokensIfExpired() {
        const refreshToken = localStorage.getItem('refresh_token');

        if (refreshToken === null) throw new Error("인증 정보에 문제가 있습니다. 로그아웃 후 재로그인을 권장합니다.");

        const parsedJwt = TokenUtility.parseJwt(refreshToken);
        const expiration = parsedJwt.exp;
        const current = Math.floor(Date.now() / 1000);

        if (current >= expiration) {
            localStorage.removeItem('access_token');
            localStorage.removeItem('refresh_token');
            
            console.log('expired refresh token. removed.');
            
            location.reload();
        }
    }

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

    static saveTokens(refreshedTokens) {
        if (refreshedTokens.access_token === undefined || refreshedTokens.refresh_token === undefined)
            throw new Error("JWToken cannot be undefined.");

        localStorage.setItem("access_token", `Bearer ${refreshedTokens.access_token}`);
        localStorage.setItem("refresh_token", `Bearer ${refreshedTokens.refresh_token}`);
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

        if (toExpiry < fiveMinutes) { // 30 mins left
            Fetcher.refreshBeforeAuthRequiredRequest();

            setInterval(() => {
                Fetcher.refreshBeforeAuthRequiredRequest();
            }, intervalTimeout)
            return ;
        }

        setTimeout(() => {
            Fetcher.refreshBeforeAuthRequiredRequest();
        }, updateTimeout)

        setInterval(() => {
            Fetcher.refreshBeforeAuthRequiredRequest();
        }, intervalTimeout)

    }
}
