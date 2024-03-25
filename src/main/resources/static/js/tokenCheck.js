/**
 * 
 * @returns 스토리지에 토큰이 있다면 true, 아니라면 false
 */
function tokenCheck() {
    const accessToken = localStorage.getItem('access_token');
    if (accessToken) { return true }
    return false;
}


/*
 * const 로 선언된 전역 변수는 페이지가 로드되면서 생성되고, 
 * 사용자가 페이지를 떠나거나 새로고침하지 않는 이상 계속 유지된다.
 * 새로고침이 없는 비동기 통신 이후에도 전역 변수는 유효하다. 
 * 해당 프로젝트에는 비동기 통신 이후 갱신되는 access_token 을 localStorage에 저장하는데, 
 * const 변수로 선언해둔 경우 이것이 바뀌지 않을 것이다.
 * 그렇다면 let 을 선언해두어야 할까 --> 변수가 예기치 못한 경우, 혹은 조작으로 수정될 수도 있지 않을까
 * 혹은 
 * 갱신된 토큰을 사용하기 위해서 서버와 통신할 때
 * 헤더에 항상 localStorage.getItem('access_token') 을 사용해서 담는 것이 나을까 --> localStorage 를 수정하지 않는다면 이 편이 나을 듯 하기도.
 */
