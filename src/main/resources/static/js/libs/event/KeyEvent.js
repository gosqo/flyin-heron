export class KeyEvent {

    static preventInputsEnterKeyEvent() {
        const inputs = document.querySelectorAll("input");

        inputs.forEach((input) => {
            input.addEventListener('keydown', (event) => {
                if (event.key === 'Enter') event.preventDefault();
            });
        });
    }
}
