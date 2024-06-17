export default class TokenUtility {
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
}
