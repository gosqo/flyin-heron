document.addEventListener("DOMContentLoaded", () => {
    initDarkMode();
});

function initDarkMode() {
    console.log("darkMode.js initiated.");

    const themeToggleButton = document.querySelector("#theme-toggle-button");
    var localTheme = localStorage.getItem("themePreference");

    if (!localTheme) {

        localStorage.setItem("themePreference", "light");
        localTheme = localStorage.getItem("themePreference");

        toggleThemeButtonText(themeToggleButton, localTheme);

    } else {

        toggleTheme(themeToggleButton, localTheme);

    }

    themeToggleButton.addEventListener("click", () => {

        if (localTheme === "light") {

            localStorage.setItem("themePreference", "dark");
            localTheme = localStorage.getItem("themePreference");

            toggleTheme(themeToggleButton, localTheme);

        } else {

            localStorage.setItem("themePreference", "light");
            localTheme = localStorage.getItem("themePreference");

            toggleTheme(themeToggleButton, localTheme);

        }
    });
}

function toggleTheme(themeToggleButton, preference) {

    if (preference === "dark") {

        toggleThemeButtonText(themeToggleButton, preference);
        toggleThemeButtonColor(themeToggleButton, preference);
        toggleDarkModeCss(preference);
        toggleColorIfDarkButtonExist();

    } else if (preference === "light") {
        
        toggleThemeButtonText(themeToggleButton, preference);
        toggleThemeButtonColor(themeToggleButton, preference);
        toggleDarkModeCss(preference);
        toggleColorIfLightButtonExist();
    }
}

function toggleThemeButtonText(themeToggleButton, preference) {

    themeToggleButton.textContent = preference === "light"
        ? "Dark mode"
        : "Light mode";
}

function toggleThemeButtonColor(themeToggleButton, preference) {
    if (preference === "light") {
        themeToggleButton.classList.remove("btn-light");
        themeToggleButton.classList.add("btn-dark");

        return;
    }

    themeToggleButton.classList.remove("btn-dark");
    themeToggleButton.classList.add("btn-light");

}

function toggleColorIfDarkButtonExist() {
    const darkButtons = document.querySelectorAll(".btn-dark");

    darkButtons.forEach(element => {
        element.classList.remove("btn-dark");
        element.classList.add("btn-light");
    });
}

function toggleColorIfLightButtonExist() {
    const lightButtons = document.querySelectorAll(".btn-light");
    
    lightButtons.forEach(element => {
        element.classList.remove("btn-light");
        element.classList.add("btn-dark");
    });
}

function toggleDarkModeCss(preference) {
    const mightExist = document.querySelector(`link[href="/css/darkMode.css"]`);

    if (preference === "dark" && mightExist === null) {

        let darkModeCss = document.createElement("link");
        darkModeCss.setAttribute("rel", "stylesheet");
        darkModeCss.setAttribute("href", "/css/darkMode.css");

        document.querySelector("head").appendChild(darkModeCss);

    } else if (preference === "dark" && mightExist !== null) {
        mightExist.remove()

        let darkModeCss = document.createElement("link");
        darkModeCss.setAttribute("rel", "stylesheet");
        darkModeCss.setAttribute("href", "/css/darkMode.css");

        document.querySelector("head").appendChild(darkModeCss);

    } else {

        var targetNode = document.querySelector(`link[href="/css/darkMode.css"]`);
        if (targetNode) targetNode.remove();

    }
}
