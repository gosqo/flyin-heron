/**
 * 
 * @returns 스토리지에 토큰이 있다면 true, 아니라면 false
 */
function tokenCheck() {
    const accessToken = localStorage.getItem('access_token');
    if (accessToken) { return true }
    return false;
}
