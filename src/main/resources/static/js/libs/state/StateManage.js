export class State {
    static Event= class {
        static dispatchDOMContentLoaded() {
            document.dispatchEvent(new Event("DOMContentLoaded"));
        }
    }

    static Body = class {
        static replaceCurrentBodyWith(rawHtml) {
            const parser = new DOMParser();
            const parsedDOM = parser.parseFromString(rawHtml, "text/html");
            const newBody = parsedDOM.querySelector("body");
            const currentBody = document.querySelector("body");
            const currentHtml = document.querySelector("html");

            currentHtml.replaceChild(newBody, currentBody);
        }
    }
}