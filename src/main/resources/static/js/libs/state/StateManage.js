export class State {
    static pushHistory(rawHTML, pathToGet) {
        State.replaceCurrentBodyWith(rawHTML);
        State.pushStateWith(pathToGet);
        State.Event.dispatchDOMContentLoaded();
    }

    static replaceHistory(rawHTML, pathToGet) {
        State.replaceCurrentBodyWith(rawHTML);
        State.replaceStateWith(pathToGet);
        State.Event.dispatchDOMContentLoaded();
    }

    static replaceCurrentBodyWith(rawHtml) {
        const newBody = getParsedBodyFrom(rawHtml);
        const currentBody = document.querySelector("body");
        const currentHtml = document.querySelector("html");

        currentHtml.replaceChild(newBody, currentBody);

        function getParsedBodyFrom(rawHtml) {
            const parser = new DOMParser();
            const parsedDOM = parser.parseFromString(rawHtml, "text/html");
            const getParsedBody = parsedDOM.querySelector("body");

            return getParsedBody;
        }
    }

    static pushStateWith(pathToGet) {
        const state = {
            pathname: pathToGet
            , body: document.querySelector("body").outerHTML
        }
        const url = pathToGet;

        history.pushState(state, "", url);
    }

    static replaceStateWith(pathToGet) {
        const state = {
            pathname: pathToGet
            , body: document.querySelector("body").outerHTML
        }
        const url = pathToGet;

        history.replaceState(state, "", url);
    }

    static Event = class {
        static dispatchDOMContentLoaded() {
            document.dispatchEvent(new Event("DOMContentLoaded"));
        }
    }
}