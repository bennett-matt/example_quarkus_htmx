(() => {
    window.addEventListener("clear-post-add", () =>  {
        document.querySelectorAll(".new-post").forEach((elm) => (elm.value = ""));
    });
})();