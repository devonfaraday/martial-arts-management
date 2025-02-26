document.body.addEventListener('htmx:afterSwap', function (event) {
    // Check if the swap target is the notes list container
    if (event.detail.target && event.detail.target.id === "notes-list") {
      // Clear the note input field
      document.getElementById("noteContent").value = "";
    }
  });