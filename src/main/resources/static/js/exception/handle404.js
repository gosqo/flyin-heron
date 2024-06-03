export default class Handle404 {
    _404Flag;

    constructor() {
        this._404Flag = false;
    }

    async page404(response) {
        this._404Flag = true;

        const data = await response.text();

        document.documentElement.innerHTML = data;
        window.dispatchEvent(new Event('load', { bubbles: true, cancelable: true }));
    }
}
