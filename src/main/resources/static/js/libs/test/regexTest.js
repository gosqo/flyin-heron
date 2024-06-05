const email = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z]){0,63}@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z]){0,251}\.[a-zA-Z]{2,3}$/;
const password = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d|.*[\W\S])[A-Za-z\d|\W\S]{8,20}$/;

const idsPass = [
    "heron@vong.com"
]

const idsFail = [
    "aaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbB@aaaaa.com"
    , "aa@aaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbB.com"
    , "aa@aaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaa.com"
]

const passwordsPass = [
    "paSsword!"
    , "Abc12345"
    , "StrongPwd2022!!"
    , "2PassWord"
    , "p2aSsWord@#"
    , "****pA****"
    , "asdfAsdf&7"
    , "asdf Asd f&7" // \s space 가능?
]

const passwordsFail = [
    "password1"
    , "PASSWORD1"
    , "P@ssw0rd1234567890123"
    , "12345678"
    , "*&^*&$^#&"
    , "  &*( &*( "
]

const pass = "true";
const fail = "false";

function test(regex, datum, expect) {
    result = regex.test(datum)
    console.log("\"", result, "\"", "expect", expect, "datum: ", datum);
}

idsPass.forEach(id => {
    test(email, id, pass);
})
idsFail.forEach(id => {
    test(email, id, fail);
})

passwordsPass.forEach(item => {
    test(password, item, pass);
});

passwordsFail.forEach(item => {
    test(password, item, fail);
});
