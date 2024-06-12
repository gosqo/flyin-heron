import { Fetcher } from "../common/Fetcher.js";

export class State {
    static Event = class {
        static dispatchDOMContentLoaded() {
            document.dispatchEvent(new Event("DOMContentLoaded"));
        }
    }

    static async getViewWithAuth(pathToGet) {
        const url = pathToGet;
        let options = {
            headers: {
                "Authorization": localStorage.getItem("access_token")
            }
        }
        const data = await Fetcher.withAuth(url, options);

        State.pushHistory(data, pathToGet);
    }

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
        const parser = new DOMParser();
        const parsedDOM = parser.parseFromString(rawHtml, "text/html");
        const newBody = parsedDOM.querySelector("body");
        const currentBody = document.querySelector("body");
        const currentHtml = document.querySelector("html");

        currentHtml.replaceChild(newBody, currentBody);
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
}