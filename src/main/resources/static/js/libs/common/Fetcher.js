import TokenUtility from "../token/TokenUtility.js"

export class Fetcher {

    static async withAuth(url, options) {
        try {
            const response1 = await fetch(url, options);

            if (response1.status === 401) {
                const reissuedTokens = await this.reissueToken();
                TokenUtility.saveTokens(reissuedTokens);
                putReissuedTokenOnHeader(options);

                return await retryWithReissuedToken(url, options);
            }

            if (response1.headers.get("Content-Type") === "text/html;charset=UTF-8") {
                const data = await response1.text();
                return data;
            }

            return response1.json();
        } catch (error) {
            console.error("Error: ", error);
        }

        function putReissuedTokenOnHeader(options) {
            const reissuedAccessToken = localStorage.getItem("access_token");
            options.headers.Authorization = reissuedAccessToken;
        }

        async function retryWithReissuedToken(url, options) {
            return await fetch(url, options)
                .then(response2 => {
                    if (response2.headers.get("Content-Type") === "text/html;charset=UTF-8") {
                        const data = response2.text();
                        return data;
                    }
                    return response2.json();
                })
                .catch(error => {
                    console.error("Error: ", error);
                });
        }
    }

    static async reissueToken() {
        const url = "/api/v1/auth/refresh-token";
        const options = {
            method: "POST",
            headers: {
                "Authorization": localStorage.getItem("refresh_token")
            },
        };

        try {
            const response = await fetch(url, options);

            if (!response.ok) {
                alert("인증 정보에 문제가 있습니다.\n로그아웃 후 다시 로그인해주십시오.");
                throw new Error("Failed to refresh access token");
            }

            console.log("refreshed, success");
            return await response.json();
        } catch (error) {
            console.error("Error: ", error);
        }
    }


    static async getAuthRequiredView(pathToGet) {
        const url = pathToGet;
        let options = {
            headers: {
                "Authorization": localStorage.getItem("access_token")
            }
        };

        try {
            const data = await Fetcher.withAuth(url, options);
            return data;
        } catch (error) {
            console.error("Error: ", error);
        }
    }

    static async refreshBeforeAuthRequiredRequest() {
        console.log(localStorage.getItem("access_token"));

        const reissuedTokens = await this.reissueToken();
        TokenUtility.saveTokens(reissuedTokens);

        console.log(localStorage.getItem("access_token"));
    }
}
