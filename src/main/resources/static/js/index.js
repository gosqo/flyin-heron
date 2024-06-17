import { IndexDOM } from "./libs/IndexDOM.js";

window.addEventListener("load", () => {
    const indexDOM = new IndexDOM();
    
    indexDOM.addAuthDependButtons();
    indexDOM.addBoardListButton();
});


