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
    console.log(options);

    const response = await fetch(url, options);
    console.log(response);

    // access_token 의 만료, 여타 토큰 예외를 발생시키는 경우.
    if (response.status === 401) {
        try {

            const data = await response.json();
            console.log(data);
            console.log(data.message);

        } catch (error) { console.log('Error: ', error); }

        const refreshedTokens = await refreshTokenRequest(localStorage.getItem('refresh_token'));

        localStorage.setItem('access_token', `Bearer ${refreshedTokens.access_token}`);
        localStorage.setItem('refresh_token', `Bearer ${refreshedTokens.refresh_token}`);

        const updatedAccessToken = localStorage.getItem('access_token');
        let updatedOptions = options;
        updatedOptions.headers.Authorization = updatedAccessToken;
        console.log(updatedOptions);

        // 기존 요청을 새로운 access_token 과 함께 재요청
        return fetch(url, updatedOptions)
            .then(response => {
                console.log(response);
                if (response.ok) {

                    return response.json();

                } else if (response.status === 404) {

                    // alert('요청 자원이 존재하지 않습니다.');
                    self.location.href = '/error/404';

                } else if (response.status === 500) {

                    self.location.href = '/error/500';

                } else if (response.status === 400) {

                    // validation 등 클라이언트 요청 오류 response body 반환.
                    return response.json();
                    
                } else {

                    alert('요청이 정상적으로 처리되지 않았습니다.');

                }
            })
            .then(data => {
                console.log(data);
                return data;
            })
            .catch(error => {
                console.error(error);
                alert('Catch on retry in fetWithToken')
            });

    } else if (response.status === 400) {

        const data = await response.json();
        alert(data.message);
        return data;

    } else if (response.status === 404) {

        // alert('요청 자원이 존재하지 않습니다.');
        self.location.href = '/error/404';

    } else {
        try {
            const data = await response.json();
            console.log(data);
            return data;
    
        } catch (error) {
            console.error("Error: ", error);
            alert('Catch on first try in fetchWithToken');
        }
        
    }
}

/**
 * 보유한 리프레시 토큰을 요청 헤더에 담아 '/api/v1/auth/refresh-token' 에 요청합니다.
 * 응답이 정상적이라면, 서버에서 반환한 토큰(액세스, 리프레시)을 json 형태로 반환합니다.
 * 응답이 ok 가 아니라면 예외를 던지고, 사용자에게는 재로그인을 요청합니다.
 * 
 * param: 클라이언트가 보유하고 있는 refreshToken
 * returns: 토큰(액세스, 리프레시). 서버의 리프레시 토큰 발급 정책을 아래와 같습니다.
 * 
 * --- 아래 ---
 *   요청에 사용한 리프레시 토큰의 만료기간이 7일 이내로 남은 경우,
 *     리프레시 토큰도 갱신하여 반환.
 *   만료기간이 7일 이상 남은 경우,
 *     액세스 토큰만 갱신하고, 기존의 리프레시 토큰과 함께 반환. 
 */
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

    if (response.status !== 200) {
        alert('인증 정보에 문제가 있습니다.\n로그아웃 후 다시 로그인해주십시오.');
        throw new Error('Failed to refresh access token');
    } else {
        const data = await response.json();
        console.log(data);

        // data 자체를 반환, 함수 호출한 곳에서는 access-, refresh 모두 localStorage에 저장.
        return data;
    }
}
