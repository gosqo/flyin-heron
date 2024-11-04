export function throttle(callback, delay) {
    let timer;

    return (event) => {
        if (!timer) {
            callback(event);
            timer = setTimeout(() => {
                timer = null;
            }, delay);
        }
    };
}

export function consoleTest(event) {
    console.log(event.target);
}
