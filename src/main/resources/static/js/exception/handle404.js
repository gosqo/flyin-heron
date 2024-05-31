
async function page404(response) {
    _404Flag = true;

    const data = await response.text();

    // const parser = new DOMParser();
    // const doc = parser.parseFromString(data, 'text/html');

    // document.documentElement.innerHTML = doc.documentElement.innerHTML;
    document.documentElement.innerHTML = data;

    // document.dispatchEvent(new Event('DOMContentLoaded', { bubbles: true, cancelable: true }));
    window.dispatchEvent(new Event('load', { bubbles: true, cancelable: true }));
}
