/**
 * 
 * @returns 스토리지에 토큰이 있다면 true, 아니라면 false
 */
function hasAuth() {
    const accessToken = localStorage.getItem('access_token');
    return accessToken !== undefined && accessToken.startsWith('Bearer ');
}
