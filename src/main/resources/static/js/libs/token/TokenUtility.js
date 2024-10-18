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
        const bearerRefreshToken = localStorage.getItem("refresh_token");

        if (bearerRefreshToken !== null) {

            localStorage.removeItem("access_token");
            localStorage.removeItem("refresh_token");

            const refreshToken = bearerRefreshToken.substring(7);

            let date = new Date();
            date.setMinutes(date.getMinutes() + 1);

            document.cookie = "sref=" + refreshToken + "; path=/; expires=" + date.toUTCString() + ";";

            const url = "/api/v1/auth/logout";
            let options = {
                headers: {
                },
                method: "POST",
            };

            fetch(url, options)
                .then(response => {
                    console.log(response);
                    return response.json();
                })
                .then(data => {
                    console.log(data); // data.status, data.message 접근 가능.
                    alert("인증 정보 관리 방식 변경으로 로그아웃 처리되었습니다. 이후 로그인 하시면 정상적으로 회원 기능을 사용하실 수 있습니다.")
                })
                .catch(error => {
                    console.error("Error: ", error);
                });

            throw new Error("server processed user logged out."); // 회원 전용 UI 노출, 기능 제제를 위한 예외.
        }
    }

}
