const token = sessionStorage.getItem('jwt')
// .substring(10, token.length - 2);
// let tokenOnHeader = '';

if (token) {

    console.log('token exist.');
    tokenOnHeader = 'Bearer ' + token;
    
    console.log(tokenOnHeader);

} else {
    console.log('no token exist.');
}
