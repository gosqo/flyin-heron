export class DomCreate {
    static element(elementName, id, className) {
        const element = document.createElement(elementName);

        if (id !== null)
            element.id = id;
        if (className !== null)
            element.className = className;

        return element;
    }

    static button(id, className, textContent) {
        const element = document.createElement("button");

        if (id !== null)
            element.id = id;
        if (className !== null)
            element.className = className;
        if (textContent !== null)
            element.textContent = textContent;

        return element;
    }

    static anchor(id, className, href, textContent) {
        const element = document.createElement("a");

        if (id !== null)
            element.id = id;
        if (className !== null)
            element.className = className;
        if (href !== null)
            element.href = href;
        if (textContent !== null)
            element.textContent = textContent;

        return element;
    }

    static division(id, className, textContent) {
        const element = document.createElement("div");

        if (id !== null)
            element.id = id;
        if (className !== null)
            element.className = className;
        if (textContent !== null)
            element.textContent = textContent;

        return element;
    }

    static paragraph(id, className, textContent) {
        const element = document.createElement("p");

        if (id !== null)
            element.id = id;
        if (className !== null)
            element.className = className;
        if (textContent !== null)
            element.textContent = textContent;

        return element;
    }

    static small(id, className, textContent) {
        const element = document.createElement("small");

        if (id !== null)
            element.id = id;
        if (className !== null)
            element.className = className;
        if (textContent !== null)
            element.textContent = textContent;

        return element;
    }

    static span(id, className, textContent) {
        const element = document.createElement("span");

        if (id !== null)
            element.id = id;
        if (className !== null)
            element.className = className;
        if (textContent !== null)
            element.textContent = textContent;

        return element;
    }
}
