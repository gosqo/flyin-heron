// 요청 헤더에 토큰이 담긴 경우 사용
async function fetchWithToken(url, options) {
    
    const response = await fetch(url, options);
    console.log(response);
    // 토큰 만료, 위조된 토큰 등의 경우.
    if (response.status === 401) {
        
        const newAccessToken = await refreshTokenRequest(refresh_token);
        localStorage.setItem('access_token', `Bearer ${newAccessToken}`);
        
        options.headers.Authorization = localStorage.getItem('access_token');
        console.log(options.headers.Authorization);

        // 기존 요청을 새로운 access_token 과 함께 재요청
        return fetch(url, options)
        .then(response => {
            if (!response.ok) {
                alert('인증에 문제가 있습니다. 다시 로그인 해주십시오.');
            } else {
                return response.json();
            }
        })
        .then(data => {
            console.log(data);
            return data;
        })
        .catch(error => console.error(error));

    } else {
        
        const data = await response.json();
        console.log(data);
        return data;

    }

}

async function refreshTokenRequest(refreshToken) {
    const url = '/api/v1/auth/refresh-token';
    const options = {
        method: 'POST',
        headers: {
            'Authorization': refreshToken
        }
    };
    const response = await fetch(url, options);
    console.log(response);

    if (!response.ok) {
        alert('인증 정보에 문제가 생겼습니다. 다시 로그인해주십시오.');
        throw new Error('Failed to refresh access token');
    } else {
        const data = await response.json();
        console.log(data);
        return data.access_token;
    }
    
    
}
