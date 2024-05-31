/**
 * 토큰과 함께 서버의 자원을 요청하는 경우에 사용하는 함수
 * 
 * @param url 자원을 요청할 uri
 * @param options 
 *     HTTP request method, 
 *     access_token 을 담은 headers, 
 *     필요한 경우 body 를 포함한 객체
 * @returns 해당 uri 의 요청이 응답하는 json 형태의 data.
 */
async function fetchWithToken(url, options) {
    const response1 = await fetch(url, options);

    if (response1.status === 401) {
        const currentRefreshToken = localStorage.getItem('refresh_token')
        const reissuedTokens = await reissueTokenWith(currentRefreshToken);
        saveTokens(reissuedTokens);
        putReissuedTokenOnHeader(options);
        return await retryWithReissuedToken(url, options);

    } else if (response1.status === 404) {
        // Might not get 404 when fetchWithToken but, for the case might be happened.
        page404(response1);
        return;
    }
    try {
        return response1.json();

    } catch (error) {
        console.error("Error: ", error);
    }


}

function putReissuedTokenOnHeader(options) {
    const reissuedAccessToken = localStorage.getItem('access_token');
    options.headers.Authorization = reissuedAccessToken;
    // console.log(`options like: ${JSON.stringify(options)}`);
}

async function retryWithReissuedToken(url, options) {
    return await fetch(url, options)
        .then(response2 => {
            if (response2.status === 404) {
                page404(response2);
                return;
            }

            return response2.json();
        })
        .catch(error => {
            console.error("Error: ", error);
        });
}

function saveTokens(refreshedTokens) {
    localStorage.setItem('access_token', `Bearer ${refreshedTokens.access_token}`);
    localStorage.setItem('refresh_token', `Bearer ${refreshedTokens.refresh_token}`);
}

async function reissueTokenWith(refreshToken) {
    const url = '/api/v1/auth/refresh-token';
    const options = {
        method: 'POST',
        headers: {
            'Authorization': refreshToken
        }
    };

    const response = await fetch(url, options);

    if (response.status !== 200) {
        alert('인증 정보에 문제가 있습니다.\n로그아웃 후 다시 로그인해주십시오.');
        throw new Error('Failed to refresh access token');
    }

    console.log('refreshed, success');
    return await response.json();
}
