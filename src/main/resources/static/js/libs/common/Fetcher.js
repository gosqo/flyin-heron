import TokenUtility from "../token/TokenUtility.js"

export default class Fetcher {
    static async withAuth(url, options) {
        const response1 = await fetch(url, options);

        if (response1.status === 401) {
            const currentRefreshToken = localStorage.getItem("refresh_token")
            const reissuedTokens = await this.reissueTokenWith(currentRefreshToken);
            TokenUtility.saveTokens(reissuedTokens);
            this.putReissuedTokenOnHeader(options);
            return await this.retryWithReissuedToken(url, options);
        }

        try {
            return response1.json();
        } catch (error) {
            console.error("Error: ", error);
        }
    }

    static async reissueTokenWith(refreshToken) {
        const url = "/api/v1/auth/refresh-token";
        const options = {
            method: "POST",
            headers: {
                "Authorization": refreshToken
            }
        };

        const response = await fetch(url, options);

        if (response.status !== 200) {
            alert("인증 정보에 문제가 있습니다.\n로그아웃 후 다시 로그인해주십시오.");
            throw new Error("Failed to refresh access token");
        }

        console.log("refreshed, success");
        return await response.json();
    }

    static putReissuedTokenOnHeader(options) {
        const reissuedAccessToken = localStorage.getItem("access_token");
        options.headers.Authorization = reissuedAccessToken;
    }

    static async retryWithReissuedToken(url, options) {
        return await fetch(url, options)
            .then(response2 => { return response2.json(); })
            .catch(error => { console.error("Error: ", error); });
    }
}
