/**
 * document.createElement() 함수와, css 클래스명, html 지정자 프로퍼티를 받아 HTML Element 반환.
 * @param {HTMLElement} elementName 
 * @param {string} id 
 * @param {string} className 
 * @returns 매개변수로 이루어진 HTML Element.
 */
function createElement(elementName, id, className) {
    const element = document.createElement(elementName);

    if (id !== null)
        element.id = id;
    if (className !== null)
        element.className = className;

    return element;
}

function createButton(id, className, textContent) {
    const element = document.createElement("button");

    if (id !== null)
        element.id = id;
    if (className !== null)
        element.className = className;
    if (textContent !== null)
        element.textContent = textContent;

    return element;
}

function createAnchor(id, className, href, textContent) {
    const element = document.createElement("a");

    if (id !== null)
        element.id = id;
    if (className !== null)
        element.className = className;
    if (href !== null)
        element.href = href;
    if (textContent !== null)
        element.textContent = textContent

    return element;
}

function createDivision(id, className) {
    const element = document.createElement('div');

    if (id !== null)
        element.id = id;
    if (className !== null)
        element.className = className;

    return element;
}

function createParagraph(id, className, textContent) {
    const element = document.createElement("p");

    if (id !== null)
        element.id = id;
    if (className !== null)
        element.className = className;
    if (textContent !== null)
        element.textContent = textContent

    return element;
}
