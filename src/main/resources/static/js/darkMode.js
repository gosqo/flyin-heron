// TODO 페이지 로드 시 스토리지 확인해서 띰 적용.

window.onload = () => {

    const ThemeToggleButton = document.querySelector('#theme-toggle-button');
    var localTheme = localStorage.getItem('themePreference')

    if (!localTheme) {
        localStorage.setItem('themePreference', 'light');
        ThemeToggleButton.textContent = 'Dark mode';
    } else {
        if (localTheme === 'light') {
            ThemeToggleButton.textContent = 'Dark mode';
        } else {
            ThemeToggleButton.textContent = 'Light mode';
        }
    }

    ThemeToggleButton.addEventListener('click', () => {

        const userThemePreference = localStorage.getItem('themePreference');

        if (userThemePreference === 'light') {
            localStorage.setItem('themePreference', 'dark');
            var updatedTheme = localStorage.getItem('themePreference');
            toggleTheme(ThemeToggleButton, updatedTheme);
        } else {
            localStorage.setItem('themePreference', 'light');
            var updatedTheme = localStorage.getItem('themePreference');
            toggleTheme(ThemeToggleButton, updatedTheme);
        }



    });
};

function toggleTheme(ThemeToggleButton, updatedTheme) {

    if (updatedTheme === 'dark') {
        ThemeToggleButton.textContent = 'Light mode';

        var darkModeCss = document.createElement('link');
        darkModeCss.setAttribute('rel', 'stylesheet');
        darkModeCss.setAttribute('href', '/css/darkMode.css');

        document.querySelector('head').appendChild(darkModeCss);

    } else if (updatedTheme === 'light') {
        ThemeToggleButton.textContent = 'Dark mode';

        var targetNode = document.querySelector('link[href="/css/darkMode.css"]');
        if (targetNode) targetNode.remove();

    }
}
