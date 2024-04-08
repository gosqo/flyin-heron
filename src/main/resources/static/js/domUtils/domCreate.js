/**
 * document.createElement() 함수와, css 클래스명, html 지정자 프로퍼티를 받아 HTML Element 반환.
 * @param {HTMLElement} element 
 * @param {string} className 
 * @param {string} id 
 * @returns 매개변수로 이루어진 HTML Element.
 */
function createElement(element, className, id) {
    const createdElement = document.createElement(element);
    createdElement.className = className;
    createdElement.id = id;

    return createdElement;
}
