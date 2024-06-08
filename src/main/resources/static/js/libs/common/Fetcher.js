import TokenUtility from "../token/TokenUtility.js"

export default class Fetcher {
    static async fetchPageWithAuth(url, options) {
        const response1 = await fetch(url, options);

        if (response1.status === 401) {
            const currentRefreshToken = localStorage.getItem("refresh_token")
            const reissuedTokens = await reissueTokenWith(currentRefreshToken);
            TokenUtility.saveTokens(reissuedTokens);
            putReissuedTokenOnHeader(options);
            return await retryWithReissuedToken(url, options);
        }

        try {
            return response1.text();
        } catch (error) {
            console.error("Error: ", error);
        }

        async function reissueTokenWith(refreshToken) {
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

        function putReissuedTokenOnHeader(options) {
            const reissuedAccessToken = localStorage.getItem("access_token");
            options.headers.Authorization = reissuedAccessToken;
        }

        async function retryWithReissuedToken(url, options) {
            return await fetch(url, options)
                .then(response2 => { return response2.text(); })
                .catch(error => { console.error("Error: ", error); });
        }
    }

    static async fetchObjectWithAuth(url, options) {
        const response1 = await fetch(url, options);

        if (response1.status === 401) {
            const currentRefreshToken = localStorage.getItem("refresh_token")
            const reissuedTokens = await reissueTokenWith(currentRefreshToken);
            TokenUtility.saveTokens(reissuedTokens);
            putReissuedTokenOnHeader(options);
            return await retryWithReissuedToken(url, options);
        }

        try {
            return response1.json();
        } catch (error) {
            console.error("Error: ", error);
        }

        async function reissueTokenWith(refreshToken) {
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

        function putReissuedTokenOnHeader(options) {
            const reissuedAccessToken = localStorage.getItem("access_token");
            options.headers.Authorization = reissuedAccessToken;
        }

        async function retryWithReissuedToken(url, options) {
            return await fetch(url, options)
                .then(response2 => { return response2.json(); })
                .catch(error => { console.error("Error: ", error); });
        }
    }

}
