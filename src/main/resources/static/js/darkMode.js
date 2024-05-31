window.addEventListener('load', () => {
    initDarkMode();
});

function initDarkMode() {
    console.log('darkMode.js initiated.');

    const themeToggleButton = document.querySelector('#theme-toggle-button');
    var localTheme = localStorage.getItem('themePreference');

    if (!localTheme) {

        localStorage.setItem('themePreference', 'light');
        localTheme = localStorage.getItem('themePreference');

        themeToggleButtonText(themeToggleButton, localTheme);

    } else {

        toggleTheme(themeToggleButton, localTheme);

    }

    themeToggleButton.addEventListener('click', () => {

        if (localTheme === 'light') {

            localStorage.setItem('themePreference', 'dark');
            localTheme = localStorage.getItem('themePreference');

            toggleTheme(themeToggleButton, localTheme);

        } else {

            localStorage.setItem('themePreference', 'light');
            localTheme = localStorage.getItem('themePreference');

            toggleTheme(themeToggleButton, localTheme);

        }
    });
}

function toggleTheme(themeToggleButton, preference) {

    if (preference === 'dark') {

        themeToggleButtonText(themeToggleButton, preference);
        toggleDarkModeCss(preference);

    } else if (preference === 'light') {

        themeToggleButtonText(themeToggleButton, preference);
        toggleDarkModeCss(preference);

    }
}

function themeToggleButtonText(themeToggleButton, preference) {
    
    themeToggleButton.textContent = preference === 'light'
        ? 'Dark mode'
        : 'Light mode';
}

function toggleDarkModeCss(preference) {

    if (preference === 'dark') {

        var darkModeCss = document.createElement('link');
        darkModeCss.setAttribute('rel', 'stylesheet');
        darkModeCss.setAttribute('href', '/css/darkMode.css');

        document.querySelector('head').appendChild(darkModeCss);

    } else {

        var targetNode = document.querySelector('link[href="/css/darkMode.css"]');
        if (targetNode) targetNode.remove();

    }
}
